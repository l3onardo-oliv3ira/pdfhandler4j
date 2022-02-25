package com.github.pdfhandler4j;

import java.util.function.Function;

import io.reactivex.Observable;

public interface IPdfHandler extends Function<IInputDescriptor, Observable<IPdfStatus>> {

  void reset();
}
