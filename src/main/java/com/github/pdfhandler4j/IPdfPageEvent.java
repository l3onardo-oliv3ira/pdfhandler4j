package com.github.pdfhandler4j;

public interface IPdfPageEvent extends IPdfInfoEvent {
  long geCurrentPage();

  long getTotalPages();
}
