package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPdfPageRange;
import com.github.utils4j.imp.Args;

public class ByCountPdfSplitter extends ByVolumePdfSplitter {

  private final int pageCount;
  
  public ByCountPdfSplitter(int pageCount) {
    this.pageCount = Args.requirePositive(pageCount, "pageCount is < 1");
  }
  
  @Override
  protected boolean mustSplit(long currentCombined, IPdfPageRange range, long max, int totalPages) {
    return currentCombined >= pageCount;
  }
}
