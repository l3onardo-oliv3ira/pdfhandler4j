package com.github.pdfhandler4j.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import com.github.filehandler4j.IInputFile;
import com.github.filehandler4j.imp.AbstractFileRageHandler;
import com.github.pdfhandler4j.IPagesSlice;
import com.github.pdfhandler4j.IPdfInfoEvent;
import com.github.pdfhandler4j.imp.event.PdfEndEvent;
import com.github.pdfhandler4j.imp.event.PdfOutputEvent;
import com.github.pdfhandler4j.imp.event.PdfPageEvent;
import com.github.pdfhandler4j.imp.event.PdfReadingEnd;
import com.github.pdfhandler4j.imp.event.PdfReadingStart;
import com.github.pdfhandler4j.imp.event.PdfStartEvent;
import com.github.utils4j.IResetableIterator;
import com.github.utils4j.imp.ArrayIterator;

import io.reactivex.Emitter;

abstract class AbstractPdfSplitter extends AbstractFileRageHandler<IPdfInfoEvent, IPagesSlice> {

  protected long pageNumber = 0;
  private File currentOutput = null;

  public AbstractPdfSplitter() {
    this(new DefaultPagesSlice());
  }
  
  public AbstractPdfSplitter(IPagesSlice... ranges) {
    this(new ArrayIterator<IPagesSlice>(ranges));
  }
  
  public AbstractPdfSplitter(IResetableIterator<IPagesSlice> iterator) {
    super(iterator);
    this.reset();
  }
  
  protected long combinedStart(IPagesSlice range) {
    return 0;
  }
  
  protected long combinedIncrement(long currentCombined, long currentDocumentSize) {
    return currentCombined + 1;
  }
  
  protected String computeFileName(String originalName, long beginPage) {
    return originalName + " pg_" + (beginPage == pageNumber ? beginPage : beginPage + "_ate_" + pageNumber);
  }
  
  protected int getEndReference(int totalPages) {
    return totalPages;
  }

  protected final boolean isEnd(final int totalPages) {
    return pageNumber >= getEndReference(totalPages);
  }

  protected final boolean hasNext(final int totalPages) {
    return nextPage() < getEndReference(totalPages);
  }

  protected long nextPage() {
    return pageNumber++;
  }

  protected boolean breakOnSplit() {
    return false;
  }
  
  @Override
  protected void handleError(Throwable e) {    
    if (currentOutput != null) {
      currentOutput.delete();
      currentOutput = null;
    }
    super.handleError(e);
  }

  @Override
  public void reset() {
    pageNumber = 0;
    currentOutput = null;
    super.reset();
  }  
  
  @Override
  protected void handle(IInputFile file, Emitter<IPdfInfoEvent> emitter) throws Exception {
    
    emitter.onNext(new PdfReadingStart("Lendo arquivo " + file.getName() + " (seja paciente...)"));
    
    try(CloseablePdfReader inputPdf = new CloseablePdfReader(file.toPath())) {
      
      emitter.onNext(new PdfReadingEnd("Lidos " + file.length() + " bytes "));
      
      final int totalPages = inputPdf.getNumberOfPages();
      
      emitter.onNext(new PdfStartEvent(totalPages));
      
      try {
        final String originalName = file.getShortName();
        
        if (totalPages <= 1 || forceCopy(file)) {
          currentOutput =  resolveOutput(originalName + " (PÁGINA ÚNICA)"); 
          try(OutputStream out = new FileOutputStream(currentOutput)) {
            Files.copy(file.toPath(), out);
          }
          emitter.onNext(new PdfOutputEvent("Gerado arquivo " + currentOutput.getName(), currentOutput, totalPages));
        } else {
          IPagesSlice next = next();
  
          if (next != null) {
            long max = Integer.MIN_VALUE;
            do {
              
              checkInterrupted();
              
              long start, beginPage = start = pageNumber = next.start();
              currentOutput = resolveOutput(originalName + "-pg_" + beginPage);
              CloseablePdfDocument outputDocument = new CloseablePdfDocument(currentOutput);
              
              try {
                long currentTotalPages = 0; 
                long combinedPages = combinedStart(next);
              
                do {
                  if (pageNumber > start && combinedPages == 0) {
                    beginPage = pageNumber;
                    currentOutput = resolveOutput(originalName + "-pg_" + beginPage);
                    outputDocument = new CloseablePdfDocument(currentOutput);
                    currentTotalPages = 0;
                  }
                  
                  long before = combinedPages;
                  outputDocument.addPage(inputPdf, pageNumber);
                  currentTotalPages++;
                  combinedPages = combinedIncrement(combinedPages, outputDocument.getCurrentDocumentSize());
                  
                  max = Math.max(combinedPages - before, max);
                  
                  if (mustSplit(combinedPages, next, max, totalPages) || isEnd(totalPages)) {
                    outputDocument.close();
                    combinedPages = 0;
                    File resolve = resolveOutput(computeFileName(originalName, beginPage));
                    resolve.delete();
                    currentOutput.renameTo(resolve);
                    emitter.onNext(new PdfOutputEvent("Gerado arquivo " + resolve.getName(), resolve, currentTotalPages));
                    if (breakOnSplit())
                      break;
                  } else {
                    emitter.onNext(new PdfPageEvent("Adicionada página " + pageNumber, pageNumber, getEndReference(totalPages)));
                  }
                  
                  checkInterrupted();
                  
                }while(hasNext(totalPages));
                
              }finally {
                outputDocument.close();
              }
              
            } while((next = next()) != null);
          }
        };
      } finally {
        emitter.onNext(PdfEndEvent.INSTANCE);
      }
    } 
  }

  protected boolean forceCopy(IInputFile file) {
    return false;
  }
  
  protected abstract boolean mustSplit(long currentCombined, IPagesSlice range, long max, long totalPages);
}
