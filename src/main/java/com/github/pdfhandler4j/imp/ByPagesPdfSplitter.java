package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPagesSlice;

public class ByPagesPdfSplitter extends ByVolumePdfSplitter{

  public ByPagesPdfSplitter(PagesSlice... ranges) {
    super(ranges);
  }

  @Override
  public long combinedStart(IPagesSlice range) {
    return range.start();
  }
  
  @Override
  protected boolean mustSplit(long currentCombined, IPagesSlice range, long max, long totalPages) {
    return currentCombined >= range.end();
  };
  
  @Override
  protected boolean breakOnSplit() {
    return true;
  }
}
