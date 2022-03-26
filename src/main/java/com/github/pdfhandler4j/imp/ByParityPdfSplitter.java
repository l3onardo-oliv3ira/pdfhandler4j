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

import com.github.pdfhandler4j.IPagesSlice;

public abstract class ByParityPdfSplitter extends AbstractPdfSplitter{
  
  protected static boolean isEven(int value) {
    return value % 2 == 0;
  }

  protected static boolean isOdd(int value) {
    return !isEven(value);
  }

  protected ByParityPdfSplitter(int startPage) {
    super(new DefaultPagesSlice(startPage, Integer.MAX_VALUE));
  }
  
  @Override
  protected final long nextIncrement() {
    return 2;
  }

  @Override
  protected final boolean mustSplit(long currentCombined, IPagesSlice slice, long maxIncrement, long totalPages) {
    return false;
  }
}
