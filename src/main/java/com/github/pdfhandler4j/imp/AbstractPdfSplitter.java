/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


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

  private long currentPageNumber = 0;
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
    return originalName + " pg_" + (beginPage == currentPageNumber ? beginPage : beginPage + "_ate_" + currentPageNumber);
  }
  
  protected int getEndReference(int totalPages) {
    return totalPages;
  }

  protected final boolean isEnd(final int totalPages) {
    return currentPageNumber >= getEndReference(totalPages);
  }

  protected final boolean hasNext(final int totalPages) {
    return currentPageNumber < getEndReference(totalPages);
  }
  
  protected long nextIncrement() {
    return 1;
  }

  protected boolean breakOnSplit() {
    return false;
  }
  
  protected final IPagesSlice nextPagesSlice(long totalPages) {
    IPagesSlice s = super.nextSlice();
    return s == null ? null : new BoundedPagesSlice(s, totalPages);
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
    currentPageNumber = 0;
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
          currentOutput =  resolveOutput(originalName + " (P??GINA ??NICA)"); 
          try(OutputStream out = new FileOutputStream(currentOutput)) {
            Files.copy(file.toPath(), out);
          }
          emitter.onNext(new PdfOutputEvent("Gerado arquivo " + currentOutput.getName(), currentOutput, totalPages));
        } else {
          IPagesSlice currentSlice = nextPagesSlice(totalPages);
  
          if (currentSlice != null) {
            long maxIncrement = Integer.MIN_VALUE;
            do {
              
              checkInterrupted();
              currentPageNumber = currentSlice.start();
              CloseablePdfDocument outputDocument = newDocument(originalName);
              
              try {
                long currentTotalPages = 0; 
                long currentCombinedPages = combinedStart(currentSlice);
              
                do {
                  long combinedBefore = currentCombinedPages;
                  outputDocument.addPage(inputPdf, currentPageNumber);
                  currentTotalPages++;
                  currentCombinedPages = combinedIncrement(currentCombinedPages, outputDocument.getCurrentDocumentSize());
                  maxIncrement = Math.max(currentCombinedPages - combinedBefore, maxIncrement);
                  
                  if (mustSplit(currentCombinedPages, currentSlice, maxIncrement, totalPages) || isEnd(totalPages)) {
                    outputDocument.close();
                    currentCombinedPages = 0;
                    emitter.onNext(new PdfOutputEvent(
                      "Gerado arquivo " + currentOutput.getName(), 
                      currentOutput, 
                      currentTotalPages
                    ));
                    if (breakOnSplit())
                      break;
                  } else {
                    emitter.onNext(new PdfPageEvent(
                      "Adicionada p??gina " + currentPageNumber, 
                      currentPageNumber, 
                      getEndReference(totalPages)
                    ));
                  }
                  
                  checkInterrupted();
                  if (!hasNext(totalPages))
                    break;
                  
                  currentPageNumber += nextIncrement();
                  if (currentCombinedPages == 0) {
                    outputDocument = newDocument(originalName);
                    currentTotalPages = 0;
                  }
                  
                } while(true);
                
              } finally {
                outputDocument.close();
              }
              
            } while((currentSlice = nextPagesSlice(totalPages)) != null);
          }
        };
      } finally {
        emitter.onNext(PdfEndEvent.INSTANCE);
      }
    } 
  }

  private CloseablePdfDocument newDocument(final String originalName) throws Exception {
    currentOutput = resolveOutput(computeFileName(originalName, currentPageNumber));
    currentOutput.delete();              
    return new CloseablePdfDocument(currentOutput);
  }

  protected boolean forceCopy(IInputFile file) {
    return false;
  }
  
  protected abstract boolean mustSplit(long currentCombined, IPagesSlice slice, long maxIncrement, long totalPages);
}
