package com.github.pdfhandler4j;

import com.github.filehandler4j.IFileOutputEvent;

public interface IPdfOutputEvent extends IFileOutputEvent, IPdfInfoEvent {
  long getTotalPages();
}
