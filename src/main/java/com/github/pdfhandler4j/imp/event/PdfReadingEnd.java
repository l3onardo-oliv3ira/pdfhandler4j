package com.github.pdfhandler4j.imp.event;

import com.github.filehandler4j.imp.event.FileInfoEvent;
import com.github.pdfhandler4j.IPdfInfoEvent;

public class PdfReadingEnd extends FileInfoEvent implements IPdfInfoEvent {
  public PdfReadingEnd(String message) {
    super(message);
  }
}
