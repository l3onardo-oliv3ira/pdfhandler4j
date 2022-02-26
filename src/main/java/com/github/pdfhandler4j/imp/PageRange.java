package com.github.pdfhandler4j.imp;

import com.github.filehandler4j.imp.FileRange;

public final class PageRange extends FileRange {

  public PageRange() {
    this(1, Integer.MAX_VALUE);
  }

  public PageRange(long startPage, long endPage) {
    super(startPage, endPage);
  }
}
