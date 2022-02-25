package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPdfPageRange;
import com.github.utils4j.imp.Strings;

public abstract class SplitByVolumePdfHandler extends AbstractPdfHandler {

  private int currentVolume = 1;
  
  protected SplitByVolumePdfHandler() {}
  
  protected SplitByVolumePdfHandler(IPdfPageRange[] ranges) {
    super(ranges);
  }

  @Override
  protected String computeFileName(int beginPage) {
    return "VOL-" + Strings.leftFill(currentVolume++, 2, '0') + " (pg-" + beginPage + ")";
  }
}
