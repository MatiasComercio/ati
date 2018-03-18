package ar.edu.itba.ati.idp.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/* package-private */ abstract class PxmImageIOs {
  private static final char COMMENT = '#';
  private static final int EOF = -1;

  /**
   * Find the next whitespace-delimited string in a stream, ignoring any comments.
   *
   * @param stream the stream load from
   * @return the next whitespace-delimited string
   * @throws IOException .
   */
  /* package-private */ static String next(final InputStream stream) throws IOException {
    final List<Byte> bytes = new LinkedList<>();
    while (true) {
      final int b = stream.read();

      if (b != -1) {
        final char c = (char) b;
        if (c == COMMENT) { // Skip comments: http://netpbm.sourceforge.net/doc/pbm.html
          int d;
          do {
            d = stream.read();
          } while (d != -1 && d != '\n' && d != '\r');
        } else if (!Character.isWhitespace(c)) {
          bytes.add((byte) b);
        } else if (bytes.size()
            > 0) { // If there is any space saved when `c` is a whitespace => return that
          break;
        }
      } else { // Finished the file
        break;
      }
    }
    final byte[] bytesArray = new byte[bytes.size()];
    for (int i = 0; i < bytesArray.length; ++i) {
      bytesArray[i] = bytes.get(i);
    }
    return new String(bytesArray);
  }

  /**
   * Read the next byte from the given stream, ensuring that it is not the EOF byte.
   *
   * @param stream The stream from where to read the next byte.
   * @return The next byte of the given stream.
   * @throws IOException If an unexpected IO error occurs.
   * @throws IllegalStateException If the read byte is the EOF byte.
   */
  /* package-private */ static int safeReadByte(final InputStream stream) throws IOException {
    final int dataByte = stream.read();
    if (dataByte == EOF) {
      throw new IllegalStateException("Reached end-of-file-prematurely");
    }
    return dataByte;
  }
}
