package com.github.pdfhandler4j.imp;

import java.io.File;

import com.github.pdfhandler4j.IInputDescriptor;
import com.github.pdfhandler4j.IOutputResolver;
import com.github.pdfhandler4j.IPdfHandler;
import com.github.pdfhandler4j.IPdfStatus;

import io.reactivex.Emitter;
import io.reactivex.Observable;

public abstract class AbstractPdfHandler implements IPdfHandler {

  private IOutputResolver resolver;
  
  public AbstractPdfHandler() {
  }  
  
  @Override
  public final Observable<IPdfStatus> apply(IInputDescriptor desc) {
    this.resolver = desc;
    return Observable.create((emitter) -> {
      try {
        beforeHandle(emitter);
        for(File file: desc.getInputPdfs()) {
          handle(file, emitter);
        };        
        afterHandle(emitter);
        emitter.onComplete();
      }catch(Throwable e) {
        handleError(e);
        emitter.onError(e);
      }finally {
        reset();
      }
    });
  }
  
  @Override
  public void reset() {
    resolver = null;
  }
  
  protected final File resolve(String fileName) {
    return resolver.resolveOutput(fileName);
  }

  protected void beforeHandle(Emitter<IPdfStatus> emitter) throws Exception { }
  
  protected void afterHandle(Emitter<IPdfStatus> emitter) throws Exception { }

  protected void handleError(Throwable e) { }
  
  protected abstract void handle(File file, Emitter<IPdfStatus> emitter) throws Exception;    
}
