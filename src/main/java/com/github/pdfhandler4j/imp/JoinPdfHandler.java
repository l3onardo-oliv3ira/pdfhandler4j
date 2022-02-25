package com.github.pdfhandler4j.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.File;
import java.io.FileOutputStream;

import com.github.pdfhandler4j.IPdfStatus;
import com.github.utils4j.imp.Args;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import io.reactivex.Emitter;

public class JoinPdfHandler extends AbstractPdfHandler {

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
  protected void beforeHandle(Emitter<IPdfStatus> emitter) throws Exception {
    document = new Document();
    copy = new PdfCopy(document, new FileOutputStream(output = resolve(mergeFileName)));
    document.open();    
  }

  @Override
  protected void handle(File file, Emitter<IPdfStatus> emitter) throws Exception {
    emitter.onNext(new PdfStatus("Processando arquivo " + file.getName(), 0));
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
  
  protected void handleError(Throwable e) {    
    this.close();
    if (output != null) {
      output.delete();
    }
    super.handleError(e);
  }
  
  @Override
  protected void afterHandle(Emitter<IPdfStatus> emitter) {
    close();
    emitter.onNext(new PdfStatus("Gerado arquivo " + output.getName(), 0, output));
  }  
}
