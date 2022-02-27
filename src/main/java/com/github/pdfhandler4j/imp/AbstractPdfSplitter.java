package com.github.pdfhandler4j.imp;

import static com.github.utils4j.imp.Throwables.tryRun;
import static java.lang.String.valueOf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import com.github.filehandler4j.imp.AbstractFileRageHandler;
import com.github.pdfhandler4j.IPdfInfoEvent;
import com.github.pdfhandler4j.imp.event.PdfInfoEvent;
import com.github.pdfhandler4j.imp.event.PdfOutputEvent;
import com.github.pdfhandler4j.imp.event.PdfPageEvent;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import io.reactivex.Emitter;

abstract class AbstractPdfSplitter extends AbstractFileRageHandler<IPdfInfoEvent> {

  protected long pageNumber = 0;
  private File currentOutput = null;

  public AbstractPdfSplitter() {
    this(new PageRange());
  }
  
  public AbstractPdfSplitter(PageRange... ranges) {
    super(ranges);
    this.reset();
  }
  
  protected long combinedStart(PageRange range) {
    return 0;
  }
  
  protected long combinedIncrement(long currentCombined, PdfCopy copy) {
    return currentCombined + 1;
  }
  
  protected String computeFileName(long beginPage) {
    return "pg_" + (beginPage == pageNumber ? beginPage : beginPage + "_ate_" + pageNumber);
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
  protected void handle(File file, Emitter<IPdfInfoEvent> emitter) throws Exception {
      
    emitter.onNext(new PdfInfoEvent("Processando arquivo " + file.getName()));
    
    final PdfReader inputPdf = new PdfReader(file.getAbsolutePath());
    final int totalPages = inputPdf.getNumberOfPages();
    if (totalPages <= 1) {
      currentOutput = resolve("pg_" + valueOf(1));
      try(OutputStream out = new FileOutputStream(currentOutput)) {
        Files.copy(file.toPath(), out);
      }
      emitter.onNext(new PdfOutputEvent("Gerado arquivo " + currentOutput.getName(), currentOutput, totalPages));
    } else {
      long max = Integer.MIN_VALUE;
      PageRange next = nextRange();
      while(next != null) {
        long start, beginPage = pageNumber = start = next.start();
        long currentTotalPages = 0;    
        Document document = new Document();
        currentOutput = resolve(valueOf("pg_" + beginPage));
        PdfCopy copy = new PdfCopy(document , new FileOutputStream(currentOutput));
        try {
          document.open();
          long combinedPages = combinedStart(next);
          do {
            if (pageNumber > start && combinedPages == 0) {
              beginPage = pageNumber;
              document = new Document();
              currentOutput = resolve(valueOf(pageNumber));
              copy = new PdfCopy(document , new FileOutputStream(currentOutput));
              document.open();
              currentTotalPages = 0;
            }
            
            long before = combinedPages;
            copy.addPage(copy.getImportedPage(inputPdf, (int)pageNumber));
            currentTotalPages++;
            combinedPages = combinedIncrement(combinedPages, copy);
            max = Math.max(combinedPages - before, max);
            
            if (mustSplit(combinedPages, next, max, totalPages) || isEnd(totalPages)) {
              document.close();
              copy.close();
              combinedPages = 0;
              File resolve = resolve(computeFileName(beginPage));
              resolve.delete();
              currentOutput.renameTo(resolve);
              emitter.onNext(new PdfOutputEvent("Gerado arquivo " + resolve.getName(), resolve, currentTotalPages));
              if (breakOnSplit())
                break;
            } else {
              emitter.onNext(new PdfPageEvent("Adicionada página ", pageNumber, getEndReference(totalPages)));
            }
          }while(hasNext(totalPages));
          next = nextRange();
        }catch(Exception e) {
          tryRun(document::close);
          tryRun(copy::close);
          throw e;
        }
      }
    };
  }  

  protected abstract boolean mustSplit(long currentCombined, PageRange range, long max, long totalPages);
}
