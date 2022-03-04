package com.github.pdfhandler4j.imp;

import com.github.filehandler4j.imp.DefaultFileSlice;
import com.github.pdfhandler4j.IPagesSlice;

public final class DefaultPagesSlice extends DefaultFileSlice implements IPagesSlice {

  public DefaultPagesSlice() {
    this(1, Integer.MAX_VALUE);
  }

  public DefaultPagesSlice(long startPage, long endPage) {
    super(startPage, endPage);
  }
}
