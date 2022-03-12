package com.github.pdfhandler4j.imp.event;

import com.github.filehandler4j.imp.event.FileInfoEvent;
import com.github.pdfhandler4j.IPdfInfoEvent;

public class PdfReadingStart extends FileInfoEvent implements IPdfInfoEvent{
  public PdfReadingStart(String message) {
    super(message);
  }
}
