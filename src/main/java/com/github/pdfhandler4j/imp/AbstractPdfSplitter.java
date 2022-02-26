package com.github.pdfhandler4j.imp;

import static com.github.utils4j.imp.Throwables.tryRun;
import static java.lang.String.valueOf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import com.github.filehandler4j.imp.AbstractFileHandler;
import com.github.pdfhandler4j.IPdfEvent;
import com.github.utils4j.imp.Args;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import io.reactivex.Emitter;

public abstract class AbstractPdfSplitter extends AbstractFileHandler<IPdfEvent> {

  private int iterator = 0;
  protected long pageNumber = 0;
  private File currentOutput = null;
  private final PageRange[] pageRanges;  

  public AbstractPdfSplitter() {
    this(new PageRange());
  }
  
  public AbstractPdfSplitter(PageRange... ranges) {
    this.pageRanges = Args.requireNonEmpty(ranges, "pages is empty");
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

  protected final PageRange nextRange() {
    if (iterator == pageRanges.length)
      return null;
    PageRange next = pageRanges[iterator++];
    while(next == null && iterator < pageRanges.length)
      next = pageRanges[iterator++];
    return next;
  }
  
  @Override
  public void reset() {
    pageNumber = 0;
    iterator = 0;
    currentOutput = null;
    super.reset();
  }  
  
  @Override
  protected void handle(File file, Emitter<IPdfEvent> emitter) throws Exception {
      
    emitter.onNext(new PdfEvent("Processando arquivo " + file.getName(), 0));
    
    final PdfReader inputPdf = new PdfReader(file.getAbsolutePath());
    final int totalPages = inputPdf.getNumberOfPages();
    if (totalPages <= 1) {
      currentOutput = resolve("pg_" + valueOf(1));
      try(OutputStream out = new FileOutputStream(currentOutput)) {
        Files.copy(file.toPath(), out);
      }
      emitter.onNext(new PdfEvent("Gerado arquivo " + currentOutput.getName(), 1, currentOutput));
    } else {
      long max = Integer.MIN_VALUE;
      PageRange next = nextRange();
      while(next != null) {
        long start, beginPage = pageNumber = start = next.start();
            
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
            }
            long before = combinedPages;
            copy.addPage(copy.getImportedPage(inputPdf, (int)pageNumber));
            combinedPages = combinedIncrement(combinedPages, copy);
            max = Math.max(combinedPages - before, max);
            if (mustSplit(combinedPages, next, max, totalPages) || isEnd(totalPages)) {
              document.close();
              copy.close();
              combinedPages = 0;
              String fileOutputName = computeFileName(beginPage);
              File resolve = resolve(fileOutputName);
              resolve.delete();
              currentOutput.renameTo(resolve);
              emitter.onNext(new PdfEvent("Gerado arquivo " + currentOutput.getName(), pageNumber, currentOutput));
              if (breakOnSplit())
                break;
            } else {
              emitter.onNext(new PdfEvent("Adicionada página ", pageNumber));
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
