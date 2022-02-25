package com.github.pdfhandler4j.imp;

import com.github.utils4j.imp.Strings;

public class SplitBySinglePagePdfHandler extends SplitByCountPdfHandler {
  public SplitBySinglePagePdfHandler() {
    super(1);
  }
  
  @Override
  protected String computeFileName(int beginPage) {
    return "pg-" + Strings.leftFill(beginPage, 5, '0');
  }
}
