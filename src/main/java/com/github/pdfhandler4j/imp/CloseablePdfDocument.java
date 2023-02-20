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

import static com.github.utils4j.imp.Throwables.runQuietly;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.github.utils4j.imp.Args;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfSmartCopy;

final class CloseablePdfDocument implements Closeable {
  
  private Document document;
  private OutputStream outputStream;
  private PdfCopy copy;
  
  public CloseablePdfDocument(File outputFile) throws Exception {
    Args.requireNonNull(outputFile, "current output is null");
    try {
      outputStream = new FileOutputStream(outputFile);
      document = new Document();      
      copy = new PdfSmartCopy(document, outputStream);
      copy.setFullCompression();
      copy.setCompressionLevel(9);
      document.open();      
    }catch(Exception e) {
      close();
      throw e;
    }
  }
  
  public final long getCurrentDocumentSize() {
    Args.requireNonNull(copy, "copy is null");
    return copy.getCurrentDocumentSize();
  }
  
  public final void addDocument(CloseablePdfReader reader) throws DocumentException, IOException {
    Args.requireNonNull(reader, "reader is null");
    reader.addDocument(copy);
  }

  public final void addPage(CloseablePdfReader reader, long pageNumber) throws BadPdfFormatException, IOException {
    Args.requireNonNull(reader, "reader is null");
    reader.addPage(copy, (int)pageNumber);
  }

  public final void freeReader(CloseablePdfReader reader) throws IOException {
    Args.requireNonNull(reader, "reader is null");
    reader.freeReader(copy);          
  }

  @Override
  public final void close() {
    if (copy != null) {
      runQuietly(copy::close);
      copy = null;
    }
    if (outputStream != null) {
      runQuietly(outputStream::close);
      outputStream = null;
    }
    if (document != null) {
      runQuietly(document::close);
      document = null;
    }
  }
}
