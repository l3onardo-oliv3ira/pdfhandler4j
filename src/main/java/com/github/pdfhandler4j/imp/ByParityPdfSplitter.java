package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPdfPageRange;

public abstract class ByParityPdfSplitter extends AbstractPdfSplitter{
  
  protected static boolean isEven(int value) {
    return value % 2 == 0;
  }

  protected static boolean isOdd(int value) {
    return !isEven(value);
  }

  protected ByParityPdfSplitter(int startPage) {
    super(new PageRange(startPage, Integer.MAX_VALUE));
  }
  
  @Override
  protected int nextPage() {
    int previous = pageNumber;
    pageNumber += 2;
    return previous;
  }

  @Override
  protected boolean mustSplit(long currentCombined, IPdfPageRange range, long max, int totalPages) {
    return false;
  }
}
