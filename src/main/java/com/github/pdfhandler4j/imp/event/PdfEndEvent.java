package com.github.pdfhandler4j.imp.event;

import com.github.pdfhandler4j.IPdfInfoEvent;

public enum PdfEndEvent implements IPdfInfoEvent {
  INSTANCE;

  @Override
  public String getMessage() {
    return "end";
  }
}
