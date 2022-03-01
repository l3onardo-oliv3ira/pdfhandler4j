package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPagesSlice;
import com.github.utils4j.imp.Args;
import com.itextpdf.text.pdf.PdfCopy;

public class BySizePdfSplitter extends ByVolumePdfSplitter {

  private final long maxFileSize;
  
  public BySizePdfSplitter(long maxFileSize) {
    this.maxFileSize = Args.requirePositive(maxFileSize, "maxFileSize is < 1");
  }
  
  @Override
  public long combinedIncrement(long currentCombined, PdfCopy copy) {
    long size = copy.getCurrentDocumentSize(); 
    return size += 0.03 * size;
  }
  
  @Override
  protected boolean mustSplit(long currentCombinedValue, IPagesSlice range, long max, long totalPages) {
    return currentCombinedValue + max + 2 * (maxFileSize / totalPages) > maxFileSize;
  }
}
