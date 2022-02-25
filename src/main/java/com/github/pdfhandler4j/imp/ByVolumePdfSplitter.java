package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPdfPageRange;
import com.github.utils4j.imp.Strings;

public abstract class ByVolumePdfSplitter extends AbstractPdfSplitter {

  private int currentVolume = 1;
  
  protected ByVolumePdfSplitter() {}
  
  protected ByVolumePdfSplitter(IPdfPageRange[] ranges) {
    super(ranges);
  }

  @Override
  public void reset() {
    currentVolume = 1;
    super.reset();
  }  
  
  @Override
  protected String computeFileName(int beginPage) {
    return "VOLUME-" + Strings.leftFill(currentVolume++, 2, '0') + " (pg-" + beginPage + ")";
  }
}
