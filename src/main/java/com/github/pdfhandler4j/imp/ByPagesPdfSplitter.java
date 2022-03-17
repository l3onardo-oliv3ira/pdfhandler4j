package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPagesSlice;

public class ByPagesPdfSplitter extends ByVolumePdfSplitter{

  public ByPagesPdfSplitter(DefaultPagesSlice... ranges) {
    super(ranges);
  }

  @Override
  public long combinedStart(IPagesSlice range) {
    return range.start();
  }
  
  @Override
  protected boolean mustSplit(long currentPageNumber, IPagesSlice slice, long max, long totalPages) {
    return currentPageNumber > slice.end();
  };
  
  @Override
  protected boolean breakOnSplit() {
    return true;
  }
}
