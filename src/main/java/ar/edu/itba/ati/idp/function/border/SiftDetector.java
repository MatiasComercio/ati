package ar.edu.itba.ati.idp.function.border;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;

// Thanks: http://dummyscodes.blogspot.com/2015/12/using-siftsurf-for-object-recognition.html
/*
 * OpenCV useful facts:
 * - Mat: https://docs.opencv.org/2.4/doc/tutorials/core/mat_the_basic_image_container/mat_the_basic_image_container.html
 *   - Mat is basically a class with two data parts:
 *     - the matrix header (containing information such as the size of the matrix,
 *        the method used for storing, at which address is the matrix stored, and so on).
 *     - a pointer to the matrix containing the pixel values (taking any dimensionality
 *        depending on the method chosen for storing).
 * - KeyPoint: https://docs.opencv.org/3.2.0/d2/d29/classcv_1_1KeyPoint.html
 * - Descriptor extractors: https://docs.opencv.org/2.4/modules/features2d/doc/common_interfaces_of_descriptor_extractors.html
 *   - In this interface, a keypoint descriptor can be represented as a dense,
 *     fixed-dimension vector of a basic type. Most descriptors follow this pattern as it simplifies
 *     computing distances between descriptors.
 *     Therefore, a collection of descriptors is represented as Mat ,
 *     where each row is a keypoint descriptor.
 * - Descriptor matchers: https://docs.opencv.org/2.4/modules/features2d/doc/common_interfaces_of_descriptor_matchers.html
 *   - Abstract base class for matching keypoint descriptors.
 * - DMatch distance: https://stackoverflow.com/questions/16996800/what-does-the-distance-attribute-in-dmatches-mean
 */
public enum SiftDetector {
  INSTANCE;

  private static final Scalar KEYPOINT_COLOR = new Scalar(0, 0, 255); // Blue, Green, Red (proved). WTF?
  private static final Scalar MATCH_COLOR = new Scalar(255, 0, 255);
  private static final String OUTPUT_PATH = "/tmp/";
  /*
   * No need to distinguish between greyscale & color image (same results when image is greyscale).
   * Left unused on purpose.
   */
  @SuppressWarnings("unused")
  private static final int GREYSCALE_IMAGE = Highgui.CV_LOAD_IMAGE_GRAYSCALE;
  private static final int COLOR_IMAGE = Highgui.CV_LOAD_IMAGE_COLOR;
  private static final FeatureDetector FEATURE_DETECTOR = FeatureDetector.create(FeatureDetector.SIFT);
  private static final DescriptorExtractor DESCRIPTOR_EXTRACTOR = DescriptorExtractor.create(DescriptorExtractor.SIFT);
  private static final DescriptorMatcher DESCRIPTOR_MATCHER = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
  private static final int NO_FLAGS = 0;
  private static final String IMAGE_1_FILE_NAME = "image1KeyPoints";
  private static final String IMAGE_2_FILE_NAME = "image2KeyPoints";
  private static final String IMAGE_MATCHING_FILE_NAME = "imageMatchingKeyPoints";
  private static final String EXTENSION = ".png";

  SiftDetector() {
    nu.pattern.OpenCV.loadLocally();
  }

  public List<File> apply(final String imageFile1, final String imageFile2,
                          final int matchingDistance, final double matchingPercentage) {
    final Mat image1 = Highgui.imread(imageFile1, COLOR_IMAGE);
    final Mat image2 = Highgui.imread(imageFile2, COLOR_IMAGE);

    // For each image, calculate its keyPoints & their descriptors (i.e.: transform them into vectors).
    final MatOfKeyPoint keyPointsImage1 = detectKeyPoints(image1);
    final MatOfKeyPoint descriptorsImage1 = extractDescriptors(image1, keyPointsImage1);

    final MatOfKeyPoint keyPointsImage2 = detectKeyPoints(image2);
    final MatOfKeyPoint descriptorsImage2 = extractDescriptors(image2, keyPointsImage2);

    // Match descriptor of all images, using the second image descriptors as the one to train.
    final MatOfDMatch matches = new MatOfDMatch();
    DESCRIPTOR_MATCHER.match(descriptorsImage1, descriptorsImage2, matches);

    // Collect only matches with distance lower to the given matching distance.
    final List<DMatch> goodMatchesList = Arrays.stream(matches.toArray())
        .filter(dMatch -> dMatch.distance < matchingDistance)
        .collect(Collectors.toList());

    // Give the result: "image found" or "image not found".
    final boolean imageFound = hasImageBeenFound(keyPointsImage1, keyPointsImage2, goodMatchesList,
                                                 matchingPercentage);
    if (imageFound) {
      System.out.println("SIFT: Image found.");
    } else {
      System.out.println("SIFT: Image not found.");
    }

    // Save tmp images with the calculated keyPoints drawn over the images.
    final List<File> tmpImageFiles = new LinkedList<>();
    tmpImageFiles.add(saveKeyPointImage(image1, keyPointsImage1, IMAGE_1_FILE_NAME));
    tmpImageFiles.add(saveKeyPointImage(image2, keyPointsImage2, IMAGE_2_FILE_NAME));
    tmpImageFiles.add(saveMatchingKeyPointImage(image1, keyPointsImage1,
                                                image2, keyPointsImage2,
                                                goodMatchesList));

    return tmpImageFiles;
  }

  private MatOfKeyPoint detectKeyPoints(final Mat image) {
    final MatOfKeyPoint imageKeyPoints = new MatOfKeyPoint();
    FEATURE_DETECTOR.detect(image, imageKeyPoints);
    return imageKeyPoints;
  }

  private MatOfKeyPoint extractDescriptors(final Mat image,
                                           final MatOfKeyPoint imageKeyPoints) {
    final MatOfKeyPoint keyPointsDescriptors = new MatOfKeyPoint();
    DESCRIPTOR_EXTRACTOR.compute(image, imageKeyPoints, keyPointsDescriptors);
    return keyPointsDescriptors;
  }

  /**
   *  Give the result: "image found" or "image not found",
   *  depending on the percentage of good matches.
   *  Percentage is calculated using the minimum available key points set.
   */
  private boolean hasImageBeenFound(final MatOfKeyPoint keyPointsImage1,
                                    final MatOfKeyPoint keyPointsImage2,
                                    final List<DMatch> goodMatches,
                                    final double matchingPercentage) {
    final int numKeyPoints1 = getNumKeyPoints(keyPointsImage1);
    final int numKeyPoints2 = getNumKeyPoints(keyPointsImage2);
    final int minNumKeyPoints = numKeyPoints1 < numKeyPoints2 ? numKeyPoints1 : numKeyPoints2;
    final int numGoodMatches = goodMatches.size();
    System.out.println("numKeyPoints1: " + numKeyPoints1);
    System.out.println("numKeyPoints2: " + numKeyPoints2);
    System.out.println("numGoodMatches: " + numGoodMatches);
    return numGoodMatches > minNumKeyPoints * matchingPercentage;
  }

  private int getNumKeyPoints(final MatOfKeyPoint imageKeyPoints) {
    return imageKeyPoints.rows() * imageKeyPoints.cols();
  }

  private File saveKeyPointImage(final Mat image, final MatOfKeyPoint imageKeyPoints, final String fileName) {
    final Mat outputImage = new Mat(image.rows(), image.cols(), COLOR_IMAGE);
    Features2d.drawKeypoints(image, imageKeyPoints, outputImage, KEYPOINT_COLOR, NO_FLAGS);
    final String outputFileName = OUTPUT_PATH + fileName + EXTENSION;
    Highgui.imwrite(outputFileName, outputImage);
    return new File(outputFileName);
  }

  private File saveMatchingKeyPointImage(final Mat image1, final MatOfKeyPoint keyPointsImage1,
                                         final Mat image2, final MatOfKeyPoint keyPointsImage2,
                                         final List<DMatch> goodMatchesList) {
    final Mat outputImage = new Mat(image1.rows() + image2.rows(),
                                    image1.cols() + image2.cols(),
                                    COLOR_IMAGE);
    final MatOfDMatch goodMatches = new MatOfDMatch();
    goodMatches.fromList(goodMatchesList);
    Features2d.drawMatches(image1, keyPointsImage1, image2, keyPointsImage2, goodMatches,
                           outputImage, MATCH_COLOR,
                           KEYPOINT_COLOR, new MatOfByte(), 2);
    final String outputFileName = OUTPUT_PATH + IMAGE_MATCHING_FILE_NAME + EXTENSION;
    Highgui.imwrite(outputFileName, outputImage);
    return new File(outputFileName);
  }
}
