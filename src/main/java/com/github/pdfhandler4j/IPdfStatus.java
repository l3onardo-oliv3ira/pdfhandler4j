package com.github.pdfhandler4j;

import java.io.File;
import java.util.Optional;

public interface IPdfStatus {
  String getMessage();
  int geCurrentPage();
  Optional<File> getOutput();
}
