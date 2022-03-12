package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPagesSlice;

public abstract class ByParityPdfSplitter extends AbstractPdfSplitter{
  
  protected static boolean isEven(int value) {
    return value % 2 == 0;
  }

  protected static boolean isOdd(int value) {
    return !isEven(value);
  }

  protected ByParityPdfSplitter(int startPage) {
    super(new DefaultPagesSlice(startPage, Integer.MAX_VALUE));
  }
  
  @Override
  protected long nextPage() {
    long previous = currentPageNumber;
    currentPageNumber += 2;
    return previous;
  }

  @Override
  protected boolean mustSplit(long currentCombined, IPagesSlice slice, long maxIncrement, long totalPages) {
    return false;
  }
}
