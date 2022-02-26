package com.github.pdfhandler4j.imp;

import com.github.utils4j.imp.Strings;

public class BySinglePagePdfSplitter extends ByCountPdfSplitter {
  public BySinglePagePdfSplitter() {
    super(1);
  }
  
  @Override
  protected String computeFileName(long beginPage) {
    return "pg-" + Strings.leftFill(beginPage, 5, '0');
  }
}
