package com.github.pdfhandler4j;

import java.io.File;

public interface IInputDescriptor {
  Iterable<File> getInputPdfs();
  File resolveOutput(String fileName);
}
