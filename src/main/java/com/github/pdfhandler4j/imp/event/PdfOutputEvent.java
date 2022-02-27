package com.github.pdfhandler4j.imp.event;

import java.io.File;

import com.github.filehandler4j.imp.event.FileOutputEvent;
import com.github.pdfhandler4j.IPdfOutputEvent;

public class PdfOutputEvent extends FileOutputEvent implements IPdfOutputEvent {

  private final long totalPages;

  public PdfOutputEvent(String message, File output, long totalPages) {
    super(message, output);
    this.totalPages = totalPages;
  }
  
  @Override
  public final long getTotalPages() {
    return totalPages;
  }

  @Override
  public final String toString() {
    return super.toString() + " totalPages: " + totalPages;
  }
}
