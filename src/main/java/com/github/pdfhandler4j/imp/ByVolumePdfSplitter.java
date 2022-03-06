package com.github.pdfhandler4j.imp;

import static com.github.utils4j.imp.Strings.padStart;

public abstract class ByVolumePdfSplitter extends AbstractPdfSplitter {

  private int currentVolume = 1;
  
  protected ByVolumePdfSplitter() {}
  
  protected ByVolumePdfSplitter(DefaultPagesSlice... ranges) {
    super(ranges);
  }

  @Override
  public void reset() {
    currentVolume = 1;
    super.reset();
  }  
  
  @Override
  protected String computeFileName(String originalName, long beginPage) {
    return originalName + "_VOLUME-" + padStart(currentVolume++, 2) + " (pg-" + beginPage + ")";
  }
}
