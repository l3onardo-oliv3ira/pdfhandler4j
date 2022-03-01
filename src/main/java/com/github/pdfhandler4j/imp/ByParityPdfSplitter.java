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
    super(new PagesSlice(startPage, Integer.MAX_VALUE));
  }
  
  @Override
  protected long nextPage() {
    long previous = pageNumber;
    pageNumber += 2;
    return previous;
  }

  @Override
  protected boolean mustSplit(long currentCombined, IPagesSlice range, long max, long totalPages) {
    return false;
  }
}
