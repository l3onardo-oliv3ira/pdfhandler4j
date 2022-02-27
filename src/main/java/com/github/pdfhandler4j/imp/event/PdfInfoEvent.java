package com.github.pdfhandler4j.imp.event;

import com.github.filehandler4j.imp.event.FileInfoEvent;
import com.github.pdfhandler4j.IPdfInfoEvent;

public class PdfInfoEvent extends FileInfoEvent implements IPdfInfoEvent{
  public PdfInfoEvent(String message) {
    super(message);
  }
}
