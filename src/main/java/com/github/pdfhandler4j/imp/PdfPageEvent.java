package com.github.pdfhandler4j.imp;

import com.github.filehandler4j.imp.event.FileInfoEvent;
import com.github.pdfhandler4j.IPdfPageEvent;

public class PdfPageEvent extends FileInfoEvent implements IPdfPageEvent {

  private final long currentPage;
  private final long totalPages;

  public PdfPageEvent(String message, long currentPage, long totalPages) {
    super(message);
    this.currentPage = currentPage;
    this.totalPages = totalPages;
  }
  
  @Override
  public final long geCurrentPage() {
    return currentPage;
  }
  
  @Override
  public final long getTotalPages() {
    return totalPages;
  }

  @Override
  public final String toString() {
    return getMessage() + " pg: " + currentPage + " of " + totalPages;
  }
}
