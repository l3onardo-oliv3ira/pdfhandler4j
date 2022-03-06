package com.github.pdfhandler4j.imp;

public class ByEvenPagesPdfSplitter extends ByParityPdfSplitter {
  public ByEvenPagesPdfSplitter() {
    super(2);
  }
  
  @Override
  protected String computeFileName(String originalName, long beginPage) {
    return originalName + " - (páginas PARES)";
  }
  
  @Override
  protected int getEndReference(int totalPages) {
    return isOdd(totalPages) ? --totalPages : totalPages;
  }
}
