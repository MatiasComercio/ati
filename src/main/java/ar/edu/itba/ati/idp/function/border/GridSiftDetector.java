package ar.edu.itba.ati.idp.function.border;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.FilenameUtils.getExtension;

import ar.edu.itba.ati.idp.io.ImageLoader;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridSiftDetector {

  private static final Logger LOGGER = LoggerFactory.getLogger(GridSiftDetector.class);

  /*
   * No need to distinguish between greyscale & color image (same results when image is greyscale).
   * Left unused on purpose.
   */
  @SuppressWarnings("unused")
  private static final int GREYSCALE_IMAGE = Highgui.CV_LOAD_IMAGE_GRAYSCALE;
  private static final int COLOR_IMAGE = Highgui.CV_LOAD_IMAGE_COLOR;
  private static final DescriptorExtractor DESCRIPTOR_EXTRACTOR = DescriptorExtractor
      .create(DescriptorExtractor.SIFT);
  private static final int KEY_POINT_SIZE = 1;

  private final Map<File, MatOfKeyPoint> trainImagesDescriptors;
  private final int k;

  public GridSiftDetector(final File trainImagesFolder, final int k) {
    this(requireNonNull(trainImagesFolder)
        .listFiles(pn -> pn.isFile() && ImageLoader
            .getSupportedFileExtensions().contains(getExtension(pn.getPath()))), k);
  }

  private GridSiftDetector(final File[] trainImagesFiles, final int k) {
    if (k <= 0) {
      throw new IllegalArgumentException("Parameter k must be greater than 0");
    }
    if (requireNonNull(trainImagesFiles).length == 0) {
      throw new IllegalArgumentException("No training images provided");
    }

    this.trainImagesDescriptors = getDescriptors(trainImagesFiles, k);
    this.k = k;
  }

  public File apply(final File testImageFile) {
    final Mat testImageMat = Highgui.imread(requireNonNull(testImageFile).getPath(), COLOR_IMAGE);
    final MatOfKeyPoint testImageKeyPoints = detectKeyPoints(testImageMat, k);
    final MatOfKeyPoint testImageDescriptors = extractDescriptors(testImageMat, testImageKeyPoints);

    return findBestMatch(testImageDescriptors, trainImagesDescriptors);
  }

  private static Map<File, MatOfKeyPoint> getDescriptors(final File[] trainImagesFiles,
      final int k) {
    final Map<File, MatOfKeyPoint> trainImagesDescriptors = new HashMap<>();

    for (final File imageFile : trainImagesFiles) {
      final Mat imageMat = Highgui.imread(imageFile.getPath(), COLOR_IMAGE);
      LOGGER.info("Getting keypoints of {}", imageFile.getPath());
      final MatOfKeyPoint imageKeyPoints = detectKeyPoints(imageMat, k);
      LOGGER.info("Getting descriptors of {}", imageFile.getPath());
      final MatOfKeyPoint imageDescriptors = extractDescriptors(imageMat, imageKeyPoints);

      trainImagesDescriptors.put(imageFile, imageDescriptors);
    }

    return trainImagesDescriptors;
  }

  // TODO: Validate k
  private static MatOfKeyPoint detectKeyPoints(final Mat image, final int k) {
    final List<KeyPoint> keyPoints = new LinkedList<>();
    final int gridCellWidth = image.width() / (k + 2);
    final int gridCellHeight = image.height() / (k + 2);

    for (int i = 1; i <= k; i++) {
      for (int j = 1; j <= k; j++) {
        keyPoints.add(new KeyPoint(gridCellWidth * i, gridCellHeight * j, KEY_POINT_SIZE));
      }
    }

    final MatOfKeyPoint imageKeyPoints = new MatOfKeyPoint();
    imageKeyPoints.fromList(keyPoints);

    return imageKeyPoints;
  }

  private static MatOfKeyPoint extractDescriptors(final Mat image,
      final MatOfKeyPoint imageKeyPoints) {
    final MatOfKeyPoint keyPointsDescriptors = new MatOfKeyPoint();
    DESCRIPTOR_EXTRACTOR.compute(image, imageKeyPoints, keyPointsDescriptors);
    return keyPointsDescriptors;
  }

  private static File findBestMatch(final MatOfKeyPoint testImageDescriptors,
      final Map<File, MatOfKeyPoint> trainImagesDescriptors) {
    File minDistanceImageFile = null;
    double minDistance = Double.MAX_VALUE;

    for (final Map.Entry<File, MatOfKeyPoint> imageDescriptors : trainImagesDescriptors
        .entrySet()) {
      LOGGER.info("Matching file: {}", imageDescriptors.getKey().getPath());
      double distance = Double.MAX_VALUE;
      try {
        distance = distance(testImageDescriptors, imageDescriptors.getValue());
      } catch (final Exception exception) {
        LOGGER.error("File: {}", imageDescriptors.getKey().getPath(), exception);
      }

      if (distance < minDistance) {
        minDistance = distance;
        minDistanceImageFile = imageDescriptors.getKey();
      }
    }

    return minDistanceImageFile;
  }

  private static double distance(final MatOfKeyPoint testImageDescriptor,
      final MatOfKeyPoint trainImagesDescriptor) {
    double distance = 0.0;

    for (int r = 0; r < testImageDescriptor.height(); r++) {
      for (int c = 0; c < testImageDescriptor.width(); c++) {
        distance += pow(testImageDescriptor.get(r, c)[0] - trainImagesDescriptor.get(r, c)[0], 2);
      }
    }

    return sqrt(distance);
  }
}
