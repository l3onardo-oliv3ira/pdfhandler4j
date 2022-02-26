package com.github.pdfhandler4j.imp;

import java.io.File;

import com.github.filehandler4j.imp.FileEvent;
import com.github.pdfhandler4j.IPdfEvent;
import com.github.utils4j.imp.Args;

public class PdfEvent extends FileEvent implements IPdfEvent {

  private final long currentPage;

  public PdfEvent(String message, long currentPage) {
    this(message, currentPage, null);
  }

  public PdfEvent(String message, long currentPage, File output) {
    super(message, output);
    this.currentPage = Args.requirePositive(currentPage, "currentPage < 1");
  }
  
  @Override
  public final long geCurrentPage() {
    return currentPage;
  }

  @Override
  public final String toString() {
    return getMessage() + " pg: " + currentPage;
  }
}
