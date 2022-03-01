package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPagesSlice;
import com.github.utils4j.imp.Args;

public class ByCountPdfSplitter extends ByVolumePdfSplitter {

  private final long pageCount;
  
  public ByCountPdfSplitter(long pageCount) {
    this.pageCount = Args.requirePositive(pageCount, "pageCount is < 1");
  }
  
  @Override
  protected boolean mustSplit(long currentCombined, IPagesSlice range, long max, long totalPages) {
    return currentCombined >= pageCount;
  }
}
