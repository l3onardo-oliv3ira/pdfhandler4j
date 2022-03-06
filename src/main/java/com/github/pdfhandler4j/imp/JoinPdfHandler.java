package com.github.pdfhandler4j.imp;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.File;
import java.io.FileOutputStream;

import com.github.filehandler4j.IInputFile;
import com.github.filehandler4j.imp.AbstractFileHandler;
import com.github.pdfhandler4j.IPdfInfoEvent;
import com.github.pdfhandler4j.imp.event.PdfInfoEvent;
import com.github.pdfhandler4j.imp.event.PdfOutputEvent;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.StopWatch;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import io.reactivex.Emitter;

public class JoinPdfHandler extends AbstractFileHandler<IPdfInfoEvent> {

  private File output;
  private final String mergeFileName;
  private Document document;
  private PdfCopy copy;
  private int totalPages;
  private StopWatch stopWatch = new StopWatch();
  
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
    this.totalPages = 0;
    this.stopWatch.reset();
    super.reset();
  }
  
  @Override
  protected void beforeHandle(Emitter<IPdfInfoEvent> emitter) throws Exception {
    document = new Document();
    copy = new PdfCopy(document, new FileOutputStream(output = resolve(mergeFileName)));
    document.open();   
    totalPages = 0;
    stopWatch.reset();
    stopWatch.start();
  }

  @Override
  protected void handle(IInputFile file, Emitter<IPdfInfoEvent> emitter) throws Exception {
    StopWatch handleWatch = new StopWatch();
    emitter.onNext(new PdfInfoEvent("Lendo arquivo " + file.getName()));
    
    handleWatch.start();
    PdfReader reader = new PdfReader(file.toPath().toUri().toURL());
    long time = handleWatch.stop();
    
    emitter.onNext(new PdfInfoEvent("Lidos " + (file.length() / 1024f) + "KB em " + (time / 1000f) + " segundos"));
    totalPages += reader.getNumberOfPages();

    try {
      handleWatch.start();
      copy.addDocument(reader);
      time = handleWatch.stop();
      emitter.onNext(new PdfInfoEvent("Mescladas " + totalPages + " páginas em " + (time / 1000f) + " segundos"));
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
  protected void afterHandle(Emitter<IPdfInfoEvent> emitter) {
    close();
    long time = stopWatch.stop();
    emitter.onNext(new PdfOutputEvent("Gerado arquivo " + output.getName() + " em " + (time / 1000f) + " segundos ", output, totalPages));
  }  
}
