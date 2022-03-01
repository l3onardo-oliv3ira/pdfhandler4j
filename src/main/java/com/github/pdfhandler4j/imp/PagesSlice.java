package com.github.pdfhandler4j.imp;

import com.github.filehandler4j.imp.FileSlice;
import com.github.pdfhandler4j.IPagesSlice;

public final class PagesSlice extends FileSlice implements IPagesSlice {

  public PagesSlice() {
    this(1, Integer.MAX_VALUE);
  }

  public PagesSlice(long startPage, long endPage) {
    super(startPage, endPage);
  }
}
