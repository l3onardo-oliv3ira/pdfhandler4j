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

import com.github.filehandler4j.IInputFile;
import com.github.pdfhandler4j.IPagesSlice;
import com.github.utils4j.imp.Args;

public class BySizePdfSplitter extends ByVolumePdfSplitter {

  private final long maxFileSize;
  
  public BySizePdfSplitter(long maxFileSize) {
    this.maxFileSize = Args.requirePositive(maxFileSize, "maxFileSize is < 1");
  }
  
  @Override
  protected boolean forceCopy(IInputFile file) {
    return file.length() <= maxFileSize;
  }
  
  @Override
  public long combinedIncrement(long currentCombined, long currentDocumentSize) {
    long size = currentDocumentSize; 
    return size += 0.03 * size;
  }
  
  @Override
  protected boolean mustSplit(long currentFileSize, IPagesSlice slice, long currentMaxPageSize, long totalPages) {
    long pageSizeAverage = (maxFileSize / totalPages);
    long twoPagesSize = 2 * pageSizeAverage;
    //three page sizes margin of error considering one of them the currently max page size
    long marginOfError = (currentMaxPageSize + twoPagesSize) / 3; 
    return currentFileSize + marginOfError > maxFileSize;
  }
}
