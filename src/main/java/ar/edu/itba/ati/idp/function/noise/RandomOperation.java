package ar.edu.itba.ati.idp.function.noise;

import ar.edu.itba.ati.idp.function.DoubleArray2DUnaryOperator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleUnaryOperator;
import javafx.geometry.Point2D;

public class RandomOperation implements DoubleArray2DUnaryOperator {

  private final DoubleUnaryOperator operation;
  private final Iterable<Point2D> points;

  public RandomOperation(final double percentage, final int width, final int height,
      final DoubleUnaryOperator operation) {
    if (percentage < 0 || percentage > 1) {
      throw new IllegalArgumentException("Invalid percentage");
    }

    this.operation = operation;
    this.points = getRandomUniquePoints((int) (percentage * width * height), width, height);
  }

  @Override
  public final double[][] apply(final double[][] pixels) {
    for (final Point2D point : points) {
      final int x = (int) point.getX();
      final int y = (int) point.getY();

      pixels[y][x] = operation.applyAsDouble(pixels[y][x]);
    }

    return pixels;
  }

  private static Iterable<Point2D> getRandomUniquePoints(final int size, final int maxX,
      final int maxY) {
//    final Stream<Point2D> point2DStream = Stream
//        .generate(() -> new Point2D(ThreadLocalRandom.current().nextDouble(maxX),
//            ThreadLocalRandom.current().nextDouble(maxY)));
//
//    return point2DStream.distinct().limit(size).collect(Collectors.toSet());
    final Set<Point2D> points = new HashSet<>();
    final Iterator<Integer> xs = ThreadLocalRandom.current().ints(0, maxX).iterator();
    final Iterator<Integer> ys = ThreadLocalRandom.current().ints(0, maxY).iterator();

    do {
      points.add(new Point2D(xs.next(), ys.next()));
    } while (points.size() < size);

    return points;
  }
}
