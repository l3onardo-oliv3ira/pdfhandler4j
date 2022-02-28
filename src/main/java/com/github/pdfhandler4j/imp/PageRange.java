package com.github.pdfhandler4j.imp;

import com.github.filehandler4j.imp.FileRange;
import com.github.pdfhandler4j.IPageRange;

public final class PageRange extends FileRange implements IPageRange {

  public PageRange() {
    this(1, Integer.MAX_VALUE);
  }

  public PageRange(long startPage, long endPage) {
    super(startPage, endPage);
  }
}
