package com.github.pdfhandler4j.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.github.utils4j.imp.Args;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;

class CloseablePdfDocument implements Closeable {
  
  private Document document;
  private OutputStream outputStream;
  private PdfCopy copy;
  
  public CloseablePdfDocument(File outputFile) throws Exception {
    Args.requireNonNull(outputFile, "current output is null");
    try {
      outputStream = new FileOutputStream(outputFile);
      document = new Document();
      copy = new PdfCopy(document, outputStream);
      document.open();
    }catch(Exception e) {
      close();
      throw e;
    }
  }
  
  public long getCurrentDocumentSize() {
    return copy.getCurrentDocumentSize();
  }
  
  public void addDocument(CloseablePdfReader reader) throws DocumentException, IOException {
    Args.requireNonNull(reader, "reader is null");
    reader.addDocument(copy);
  }

  public void addPage(CloseablePdfReader reader, long pageNumber) throws BadPdfFormatException, IOException {
    Args.requireNonNull(reader, "reader is null");
    reader.addPage(copy, (int)pageNumber);      
  }

  public void freeReader(CloseablePdfReader reader) throws IOException {
    Args.requireNonNull(reader, "reader is null");
    reader.freeReader(copy);          
  }

  @Override
  public void close() {
    if (copy != null) {
      tryRun(copy::close);
      copy = null;
    }
    if (outputStream != null) {
      tryRun(outputStream::close);
      outputStream = null;
    }
    if (document != null) {
      tryRun(document::close);
      document = null;
    }
  }
}