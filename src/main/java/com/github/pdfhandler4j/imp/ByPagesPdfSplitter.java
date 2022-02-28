package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPageRange;

public class ByPagesPdfSplitter extends ByVolumePdfSplitter{

  public ByPagesPdfSplitter(PageRange... ranges) {
    super(ranges);
  }

  @Override
  public long combinedStart(IPageRange range) {
    return range.start();
  }
  
  @Override
  protected boolean mustSplit(long currentCombined, IPageRange range, long max, long totalPages) {
    return currentCombined >= range.end();
  };
  
  @Override
  protected boolean breakOnSplit() {
    return true;
  }
}
