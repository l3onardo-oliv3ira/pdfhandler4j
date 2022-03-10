package com.github.pdfhandler4j;

import java.io.IOException;

import com.github.filehandler4j.IInputFile;
import com.github.pdfhandler4j.imp.CloseablePdfReader;

public interface IPdfReaderProvider {
  CloseablePdfReader getReader(IInputFile file) throws IOException;
}
