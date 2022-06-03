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

import static com.github.utils4j.imp.Throwables.tryRun;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.States;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.ReaderProperties;

public class CloseablePdfReader implements AutoCloseable {

  private static long LARGE_PDF_SIZE = 50 * 1024 * 1024; 

  private static long SMALL_PDF_SIZE = LARGE_PDF_SIZE / 8;

  private RandomAccessFile raf;
  private PdfReader reader;
  
  public CloseablePdfReader(String file) throws IOException {
    this(new File(Args.requireText(file, "file is empty")));
  }
  
  public CloseablePdfReader(Path file) throws IOException {
    this(Args.requireNonNull(file, "file is null").toFile());
  }
  
  public CloseablePdfReader(File file) throws IOException {
    Args.requireNonNull(file, "file is null");
    raf = new RandomAccessFile(file, "rw");
    try {
      this.reader = new PdfReader(
        new ReaderProperties()
          .setCloseSourceOnconstructorError(true), 
        new RandomAccessFileOrArray(
          new RandomAccessSourceFactory()
            .setForceRead(file.length() <= SMALL_PDF_SIZE)
            .createBestSource(raf)
        )
      );
      this.reader.removeUnusedObjects();
      this.reader.consolidateNamedDestinations();      
    }catch(IOException e) {
      close();
      throw e;
    }
  }
  
  protected void checkState() {
    States.requireTrue(reader != null, "reader is closed");
  }
  
  public final int getNumberOfPages() {
    checkState();
    return reader.getNumberOfPages();
  }
  
  public final void addPage(PdfCopy copy, int pageNumber) throws BadPdfFormatException, IOException {
    checkState();
    Args.requireNonNull(copy, "copy is null");
    Args.requirePositive(pageNumber, "pageNumber <= 0");
    copy.addPage(copy.getImportedPage(reader, pageNumber));
  }
  
  @Override
  public void close() {
    if (reader != null) {
      tryRun(reader::close);
      reader = null;
    }
    if (raf != null) {
      tryRun(raf::close);
      raf = null;
    }
  }

  public final void addDocument(PdfCopy copy) throws DocumentException, IOException {
    checkState();
    Args.requireNonNull(copy, "copy is null");
    copy.addDocument(reader);    
  }

  public final void freeReader(PdfCopy copy) throws IOException {
    checkState();
    Args.requireNonNull(copy, "copy is null");
    copy.freeReader(reader);    
  }  
}
