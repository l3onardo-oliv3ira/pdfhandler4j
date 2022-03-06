package com.github.pdfhandler4j.imp.event;

import com.github.pdfhandler4j.IPdfStartEvent;

public class PdfStartEvent extends PdfInfoEvent implements IPdfStartEvent {
  private int totalPages;
  
  public PdfStartEvent(String message, int totalPages) {
    super(message);
    this.totalPages = totalPages;
  }
  
  @Override
  public int getTotalPages() {
    return totalPages;
  }
}
