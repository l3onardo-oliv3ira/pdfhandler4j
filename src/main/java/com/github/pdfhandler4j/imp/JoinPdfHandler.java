package com.github.pdfhandler4j.imp;

import java.io.File;

import com.github.filehandler4j.IInputFile;
import com.github.filehandler4j.imp.AbstractFileHandler;
import com.github.pdfhandler4j.IPdfInfoEvent;
import com.github.pdfhandler4j.imp.event.PdfInfoEvent;
import com.github.pdfhandler4j.imp.event.PdfOutputEvent;
import com.github.pdfhandler4j.imp.event.PdfReadingEnd;
import com.github.pdfhandler4j.imp.event.PdfReadingStart;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.StopWatch;

import io.reactivex.Emitter;

public class JoinPdfHandler extends AbstractFileHandler<IPdfInfoEvent> {

  private final String mergedFileName;
  private File output;
  private CloseablePdfDocument outputDocument;
  
  private int totalPages;
  private StopWatch stopWatch = new StopWatch();
  
  public JoinPdfHandler(String mergeFileName) {
    this.mergedFileName = Args.requireText(mergeFileName, "mergeFileName is empty");
  }
  
  private void close() {
    if (outputDocument != null) {
      outputDocument.close();
      outputDocument = null;
    }
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
    outputDocument = new CloseablePdfDocument(output = resolveOutput(mergedFileName));
    totalPages = 0;
    stopWatch.reset();
    stopWatch.start();
  }

  @Override
  protected void handle(IInputFile file, Emitter<IPdfInfoEvent> emitter) throws Exception {
    StopWatch handleWatch = new StopWatch();
    emitter.onNext(new PdfReadingStart("Lendo arquivo " + file.getName()));
    handleWatch.start();
    try(CloseablePdfReader reader = new CloseablePdfReader(file.toPath())) {
      long time = handleWatch.stop();    
      emitter.onNext(new PdfReadingEnd("Lidos " + file.length() + " bytes em " + (time / 1000f) + " segundos"));
      checkInterrupted();
      totalPages += reader.getNumberOfPages();
      try {
        handleWatch.start();
        outputDocument.addDocument(reader);
        time = handleWatch.stop();
        emitter.onNext(new PdfInfoEvent("Mescladas " + totalPages + " páginas em " + (time / 1000f) + " segundos"));
      }finally {
        outputDocument.freeReader(reader);
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
    this.close();
    long time = stopWatch.stop();
    emitter.onNext(new PdfOutputEvent("Gerado arquivo " + output.getName() + " em " + (time / 1000f) + " segundos ", output, totalPages));
  }  
}
