package ar.edu.itba.ati.idp.io;

import ar.edu.itba.ati.idp.model.ImageMatrix.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageIOs {
  private static final Map<Type, ImageIO> TYPE_TO_IMAGE_IOS;

  static {
    final Map<Type, ImageIO> imageIOs = new HashMap<>();

    imageIOs.put(Type.BYTE_B, RawImageIO.INSTANCE);
    imageIOs.put(Type.BYTE_G, PxmImageIO.PGM_PLAIN);
    imageIOs.put(Type.BYTE_RGB, PxmImageIO.PPM_PLAIN);
    imageIOs.put(Type.BYTE_ARGB, DefaultImageIO.INSTANCE);

    TYPE_TO_IMAGE_IOS = Collections.unmodifiableMap(imageIOs);
  }

  public static ImageIO getImageIO(final Type type) {
    final ImageIO imageIO = TYPE_TO_IMAGE_IOS.get(type);
    if (imageIO == null) {
      throw new IllegalStateException("Unsupported type for IO: " + type);
    }
    return imageIO;
  }
}
