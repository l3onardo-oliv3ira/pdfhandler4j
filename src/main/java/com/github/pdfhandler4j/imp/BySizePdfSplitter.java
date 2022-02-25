package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPdfPageRange;
import com.github.utils4j.imp.Args;
import com.itextpdf.text.pdf.PdfCopy;

public class BySizePdfSplitter extends ByVolumePdfSplitter {

  private final long maxSize;
  
  public BySizePdfSplitter(long maxSize) {
    this.maxSize = Args.requirePositive(maxSize, "maxSize is < 1");
  }
  
  @Override
  public long combinedIncrement(long currentCombined, PdfCopy copy) {
    long size = copy.getCurrentDocumentSize(); 
    return size += 0.03 * size;
  }
  
  @Override
  protected boolean mustSplit(long currentCombinedValue, IPdfPageRange range, long max, int totalPages) {
    return currentCombinedValue + max + 2 * (maxSize / totalPages) > maxSize;
  }
}
