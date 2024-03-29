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
    emitter.onNext(new PdfReadingStart("Lendo arquivo " + file.getName() + " (seja paciente...)"));
    handleWatch.start();
    try(CloseablePdfReader reader = new CloseablePdfReader(file.toPath())) {
      long time = handleWatch.stop();    
      emitter.onNext(new PdfReadingEnd("Lidos " + file.length() + " bytes em " + (time / 1000f) + " segundos"));
      checkInterrupted();
      totalPages += reader.getNumberOfPages();
      try {
        handleWatch.start();
        emitter.onNext(new PdfInfoEvent("Mesclando " + totalPages + " páginas (seja paciente...)"));
        outputDocument.addDocument(reader);
        time = handleWatch.stop();
        emitter.onNext(new PdfInfoEvent("Mesclagem concluida em " + (time / 1000f) + " segundos"));
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
