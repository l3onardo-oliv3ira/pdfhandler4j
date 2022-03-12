package com.github.pdfhandler4j.imp;

import com.github.filehandler4j.IInputFile;
import com.github.pdfhandler4j.IPagesSlice;
import com.github.utils4j.imp.Args;

public class BySizePdfSplitter extends ByVolumePdfSplitter {

  private final long maxFileSize;
  
  public BySizePdfSplitter(long maxFileSize) {
    this.maxFileSize = Args.requirePositive(maxFileSize, "maxFileSize is < 1");
  }
  
  protected boolean forceCopy(IInputFile file) {
    return file.length() <= maxFileSize;
  }
  
  @Override
  public long combinedIncrement(long currentCombined, long currentDocumentSize) {
    long size = currentDocumentSize; 
    return size += 0.03 * size;
  }
  
  @Override
  protected boolean mustSplit(long currentFileSize, IPagesSlice slice, long currentMaxPageSize, long totalPages) {
    long pageSizeAverage = (maxFileSize / totalPages);
    long twoPagesSize = 2 * pageSizeAverage;
    //three page sizes margin of error considering one of them the currently max page size
    long marginOfError = currentMaxPageSize + twoPagesSize; 
    return currentFileSize + marginOfError > maxFileSize;
  }
}
