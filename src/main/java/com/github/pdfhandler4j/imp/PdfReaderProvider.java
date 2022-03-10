package com.github.pdfhandler4j.imp;

import java.io.IOException;

import com.github.filehandler4j.IInputFile;
import com.github.pdfhandler4j.IPdfReaderProvider;
import com.github.utils4j.imp.Args;

public enum PdfReaderProvider implements IPdfReaderProvider {
  DEFAULT() {
    @Override
    public CloseablePdfReader getReader(IInputFile file) throws IOException {
      Args.requireNonNull(file, "file is null");
      return new CloseablePdfReader(file.getAbsolutePath());
    }
  },
  LARGE() {
    @Override
    public CloseablePdfReader getReader(IInputFile file) throws IOException {
      Args.requireNonNull(file, "file is null");
      return new CloseablePdfReader(file.toPath().toFile());
    }
  },
  SMART() {
    @Override
    public CloseablePdfReader getReader(IInputFile file) throws IOException {
      Args.requireNonNull(file, "file is null");
      if (file.length() >= LARGE_PDF_SIZE) {
        try {
          return LARGE.getReader(file);
        }catch(Throwable e) {
          try {
            return DEFAULT.getReader(file);
          }catch(Throwable t) {
            throw new RuntimeException(e); //original
          }
        }
      } else {
        try {
          return DEFAULT.getReader(file);
        }catch(Throwable e) {
          try {
            return LARGE.getReader(file);
          }catch (Throwable t) {
            throw new RuntimeException(e); //original   
          }
        }
      }
    }
  };
  
  private static long LARGE_PDF_SIZE = 150 * 1024 * 1024; 
}
