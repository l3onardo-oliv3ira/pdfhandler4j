package com.github.pdfhandler4j.imp;

import com.github.pdfhandler4j.IPagesSlice;
import com.github.utils4j.imp.Args;

public class PagesSliceWrapper implements IPagesSlice {

  private IPagesSlice slice;
  
  protected PagesSliceWrapper(IPagesSlice slice) {
    this.slice = Args.requireNonNull(slice, "slice is null");
  }
  
  @Override
  public long start() {
    return slice.start();
  }

  @Override
  public long end() {
    return slice.end();
  }
}
