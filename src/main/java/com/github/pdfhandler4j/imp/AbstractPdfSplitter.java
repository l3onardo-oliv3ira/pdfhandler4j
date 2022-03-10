package com.github.pdfhandler4j.imp;

import static com.github.pdfhandler4j.imp.PdfReaderProvider.SMART;
import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import com.github.filehandler4j.IInputFile;
import com.github.filehandler4j.imp.AbstractFileRageHandler;
import com.github.pdfhandler4j.IPagesSlice;
import com.github.pdfhandler4j.IPdfInfoEvent;
import com.github.pdfhandler4j.imp.event.PdfInfoEvent;
import com.github.pdfhandler4j.imp.event.PdfOutputEvent;
import com.github.pdfhandler4j.imp.event.PdfPageEvent;
import com.github.pdfhandler4j.imp.event.PdfStartEvent;
import com.github.utils4j.IResetableIterator;
import com.github.utils4j.imp.ArrayIterator;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;

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
  
  protected long combinedIncrement(long currentCombined, PdfCopy copy) {
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
    
    emitter.onNext(new PdfInfoEvent("Lendo arquivo " + file.getName() + " (seja paciente...)"));
    
    try(final CloseablePdfReader inputPdf = SMART.getReader(file)) {
      
      final int totalPages = inputPdf.getNumberOfPages();
      final String originalName = file.getShortName();
      
      emitter.onNext(new PdfStartEvent("Processando arquivo " + file.getName(), totalPages));
      if (totalPages <= 1 || forceCopy(file)) {
        currentOutput =  resolve(originalName + " (PÁGINA ÚNICA)"); 
        try(OutputStream out = new FileOutputStream(currentOutput)) {
          Files.copy(file.toPath(), out);
        }
        emitter.onNext(new PdfOutputEvent("Gerado arquivo " + currentOutput.getName(), currentOutput, totalPages));
      } else {
        long max = Integer.MIN_VALUE;
        IPagesSlice next = next();
        
        Thread currentThread = Thread.currentThread();
        
        while(next != null) {
          long start, beginPage = pageNumber = start = next.start();
          long currentTotalPages = 0;    
          Document document = new Document();
          currentOutput = resolve(originalName + "-pg_" + beginPage);
          OutputStream outputStream = new FileOutputStream(currentOutput);
          PdfCopy copy = new PdfCopy(document, outputStream);
        
          try {
            document.open();
            long combinedPages = combinedStart(next);
          
            do {
              if (pageNumber > start && combinedPages == 0) {
                beginPage = pageNumber;
                document = new Document();
                currentOutput = resolve(originalName + "-pg_" + pageNumber);
                copy = new PdfCopy(document , new FileOutputStream(currentOutput));
                document.open();
                currentTotalPages = 0;
              }
              
              long before = combinedPages;
              
              inputPdf.addPage(copy, (int)pageNumber);
              
              currentTotalPages++;
              combinedPages = combinedIncrement(combinedPages, copy);
              max = Math.max(combinedPages - before, max);
              
              if (mustSplit(combinedPages, next, max, totalPages) || isEnd(totalPages)) {
                safeClose(document, outputStream, copy);
                combinedPages = 0;
                File resolve = resolve(computeFileName(originalName, beginPage));
                resolve.delete();
                currentOutput.renameTo(resolve);
                emitter.onNext(new PdfOutputEvent("Gerado arquivo " + resolve.getName(), resolve, currentTotalPages));
                if (breakOnSplit())
                  break;
              } else {
                emitter.onNext(new PdfPageEvent("Adicionada página " + pageNumber, pageNumber, getEndReference(totalPages)));
              }
              
              if (currentThread.isInterrupted()) {
                throw new InterruptedException();
              }
              
            }while(hasNext(totalPages));
            next = next();
          }catch(Exception e) {
            safeClose(document, outputStream, copy);
            throw e;
          }
        }
      };
    }
  }

  private void safeClose(Document document, OutputStream outputStream, PdfCopy copy) {
    tryRun(copy::close);
    tryRun(outputStream::close);
    tryRun(document::close);
  }  

  protected boolean forceCopy(IInputFile file) {
    return false;
  }
  
  protected abstract boolean mustSplit(long currentCombined, IPagesSlice range, long max, long totalPages);
}
