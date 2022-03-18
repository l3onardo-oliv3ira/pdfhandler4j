package com.github.pdfhandler4j.imp;

public class ByEvenPagesPdfSplitter extends ByParityPdfSplitter {
  public ByEvenPagesPdfSplitter() {
    super(2);
  }
  
  @Override
  protected final String computeFileName(String originalName, long beginPage) {
    return originalName + " (PÁGINAS PARES)";
  }
  
  @Override
  protected final int getEndReference(int totalPages) {
    return isOdd(totalPages) ? --totalPages : totalPages;
  }
}
