package com.github.pdfhandler4j.imp;

import static com.github.utils4j.imp.Strings.padStart;

public class BySinglePagePdfSplitter extends ByCountPdfSplitter {
  public BySinglePagePdfSplitter() {
    super(1);
  }
  
  @Override
  protected String computeFileName(long beginPage) {
    return "pg-" + padStart(beginPage, 5);
  }
}
