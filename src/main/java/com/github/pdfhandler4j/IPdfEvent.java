package com.github.pdfhandler4j;

import com.github.filehandler4j.IFileEvent;

public interface IPdfEvent extends IFileEvent {
  long geCurrentPage();
}
