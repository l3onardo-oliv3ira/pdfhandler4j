package com.github.pdfhandler4j.imp;

public class ByPagesPdfSplitter extends ByVolumePdfSplitter{

  public ByPagesPdfSplitter(PageRange... ranges) {
    super(ranges);
  }

  @Override
  public long combinedStart(PageRange range) {
    return range.start();
  }
  
  @Override
  protected boolean mustSplit(long currentCombined, PageRange range, long max, long totalPages) {
    return currentCombined >= range.end();
  };
  
  @Override
  protected boolean breakOnSplit() {
    return true;
  }
}
