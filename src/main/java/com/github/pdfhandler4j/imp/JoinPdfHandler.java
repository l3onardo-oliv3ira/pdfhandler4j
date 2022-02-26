package com.github.pdfhandler4j.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.File;
import java.io.FileOutputStream;

import com.github.filehandler4j.imp.AbstractFileHandler;
import com.github.pdfhandler4j.IPdfEvent;
import com.github.utils4j.imp.Args;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import io.reactivex.Emitter;

public class JoinPdfHandler extends AbstractFileHandler<IPdfEvent> {

  private File output;
  private final String mergeFileName;
  private Document document;
  private PdfCopy copy;

  public JoinPdfHandler(String mergeFileName) {
    this.mergeFileName = Args.requireText(mergeFileName, "mergeFileName is empty");
  }
  
  private void closeDocument() {
    if (document != null) {
      tryRun(document::close);
      document = null;
    }
  }

  private void closeCopy() {
    if (copy != null) {      
      tryRun(copy::close);
      copy = null;      
    }
  }
  
  private void close() {
    this.closeDocument();
    this.closeCopy();
  }

  @Override
  public void reset() {
    this.close();
    this.output = null;
    super.reset();
  }
  
  @Override
  protected void beforeHandle(Emitter<IPdfEvent> emitter) throws Exception {
    document = new Document();
    copy = new PdfCopy(document, new FileOutputStream(output = resolve(mergeFileName)));
    document.open();    
  }

  @Override
  protected void handle(File file, Emitter<IPdfEvent> emitter) throws Exception {
    emitter.onNext(new PdfEvent("Processando arquivo " + file.getName(), 0));
    PdfReader reader = new PdfReader(file.toURI().toURL());
    try {
      copy.addDocument(reader);
    }finally {
      try {
        copy.freeReader(reader);
      } finally {      
        reader.close();
      }
    }
  }
  
  @Override
  protected void handleError(Throwable e) {    
    this.close();
    this.cleanOutput();
    super.handleError(e);
  }

  private void cleanOutput() {
    if (output != null) {
      output.delete();
      output = null;
    }
  }
  
  @Override
  protected void afterHandle(Emitter<IPdfEvent> emitter) {
    close();
    emitter.onNext(new PdfEvent("Gerado arquivo " + output.getName(), 0, output));
  }  
}
