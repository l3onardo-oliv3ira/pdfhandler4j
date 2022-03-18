package com.github.pdfhandler4j.imp;

public class ByOddPagesPdfSplitter extends ByParityPdfSplitter {
  public ByOddPagesPdfSplitter() {
    super(1);
  }
  
  @Override
  protected final String computeFileName(String originalName, long beginPage) {
    return originalName + " (PÁGINAS ÍMPARES)";
  }
  
  @Override
  protected final int getEndReference(int totalPages) {
    return isEven(totalPages) ? --totalPages : totalPages;
  }
}
