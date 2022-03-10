package com.github.pdfhandler4j.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.File;
import java.io.IOException;

import com.github.utils4j.imp.States;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;

public class CloseablePdfReader implements AutoCloseable {

  private static final RandomAccessSourceFactory RASF = new RandomAccessSourceFactory();

  private RandomAccessSource ras;
  private RandomAccessFileOrArray rafa;
  
  private PdfReader reader;
  
  public CloseablePdfReader(String file) throws IOException {
    this.reader = new PdfReader(file);
  }
  
  CloseablePdfReader(File file) throws IOException {
    try {
      this.ras = RASF.createBestSource(file.getAbsolutePath());
      this.rafa = new RandomAccessFileOrArray(ras);
      this.reader = new PdfReader(rafa, null);
    } catch(Throwable e){      
      close();
      throw new IOException(e);
    }
  }
  
  protected void checkAvailable() {
    States.requireNonNull(reader, "reader already disposed");
  }
  
  public final int getNumberOfPages() {
    checkAvailable();
    return reader.getNumberOfPages();
  }
  
  public final void addPage(PdfCopy copy, int pageNumber) throws BadPdfFormatException, IOException {
    checkAvailable();
    copy.addPage(copy.getImportedPage(reader, pageNumber));    
  }
  
  @Override
  public void close() {
    if (reader != null) {
      tryRun(reader::close);
      reader = null;
    }
    if (ras != null) {
      tryRun(ras::close);
      ras = null;
    }
    if (rafa != null) {
      tryRun(rafa::close);
      rafa = null;
    }    
  }

  public void addDocument(PdfCopy copy) throws DocumentException, IOException {
    checkAvailable();
    copy.addDocument(reader);    
  }

  public void freeReader(PdfCopy copy) throws IOException {
    copy.freeReader(reader);    
  }  
}
