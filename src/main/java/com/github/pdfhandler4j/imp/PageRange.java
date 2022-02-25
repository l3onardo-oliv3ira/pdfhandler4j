package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPdfPageRange;
import com.github.utils4j.imp.Args;

public final class PageRange implements IPdfPageRange {

  private final int startPage;
  private final int endPage;
  
  public PageRange() {
    this(1, Integer.MAX_VALUE);
  }

  public PageRange(int startPage, int endPage) {
    Args.requireTrue(startPage <= endPage, "startPage > endPage");
    this.startPage = Args.requirePositive(startPage, "stargPage isn't positive");
    this.endPage = endPage;
  }
  
  @Override
  public final int startPage() {
    return startPage;
  }

  @Override
  public final int endPage() {
    return endPage;
  }
}
