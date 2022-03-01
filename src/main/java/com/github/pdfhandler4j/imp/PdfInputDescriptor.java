package com.github.pdfhandler4j.imp;

import java.io.IOException;

import com.github.filehandler4j.imp.InputDescriptor;

public class PdfInputDescriptor extends InputDescriptor {
  
  private PdfInputDescriptor() {}
  
  public static class Builder extends InputDescriptor.Builder {
    
    public Builder() {
      super(".pdf");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public PdfInputDescriptor build() throws IOException {
      return (PdfInputDescriptor)super.build();
    }
    
    @Override
    protected InputDescriptor createDescriptor() {
      return new PdfInputDescriptor();
    }
  }
}
