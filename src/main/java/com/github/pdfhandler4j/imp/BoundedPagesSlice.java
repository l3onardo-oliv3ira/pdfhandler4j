package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPagesSlice;
import com.github.utils4j.imp.Args;

public class BoundedPagesSlice implements IPagesSlice {

  private IPagesSlice slice;
  private long totalPages;
  
  public BoundedPagesSlice(IPagesSlice slice, long totalPages) {
    this.slice = Args.requireNonNull(slice, "slice is null");
    this.totalPages = Args.requirePositive(totalPages, "totalPags <= 0");
  }
  
  @Override
  public long start() {    
    return Math.min(Math.max(1, slice.start()), totalPages);
  }

  @Override
  public long end() {
    return Math.min(Math.max(1,  slice.end()), totalPages);
  }
}
