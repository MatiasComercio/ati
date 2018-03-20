package ar.edu.itba.ati.idp.model;

import ar.edu.itba.ati.idp.model.ImageMatrix.Band;
import java.util.Collections;
import java.util.List;

public class ImageHistogram {
  private final List<BandHistogram> bandHistograms;

  private ImageHistogram(final List<BandHistogram> bandHistograms) {
    this.bandHistograms = bandHistograms;
  }

  public static ImageHistogram from(final List<BandHistogram> bandHistograms) {
    return new ImageHistogram(Collections.unmodifiableList(bandHistograms));
  }

  public List<BandHistogram> getBandHistograms() {
    return bandHistograms;
  }

  public static class BandHistogram {
    private final Band band;
    private final int[] plainHistogram;

    private BandHistogram(final Band band, final int[] plainHistogram) {
      this.band = band;
      this.plainHistogram = plainHistogram;
    }

    public static BandHistogram from(final Band band, final int[] plainHistogram) {
      return new BandHistogram(band, plainHistogram);
    }

    public Band getBand() {
      return band;
    }

    public int[] getPlainHistogram() {
      return plainHistogram;
    }
  }
}
