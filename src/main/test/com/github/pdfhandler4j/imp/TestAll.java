package com.github.pdfhandler4j.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.filehandler4j.IFileHandler;
import com.github.filehandler4j.IInputDescriptor;
import com.github.filehandler4j.imp.InputDescriptor;

public class TestAll {
  
  public static void main(String[] args) throws IOException {
    IFileHandler<?>[] handlers = new IFileHandler[] {
      new BySizePdfSplitter(50 * 1024 * 1024),
      new ByCountPdfSplitter(300),
      new ByPagesPdfSplitter(
        new PageRange(1, 1),
        new PageRange(3, 6),
        new PageRange(10, 20),
        new PageRange(25, 25),
        new PageRange(100, 200),
        new PageRange(400, 600),
        new PageRange(3000, 4500)
      ),
      new ByOddPagesPdfSplitter(),
      new ByEvenPagesPdfSplitter(),
      new BySinglePagePdfSplitter()
    };
    
    final String[] outputPath = new String[] {
      "bysize",
      "bycount",
      "bypages",
      "byodd",
      "byeven",
      "bysingle"
    };
    
    Path baseInput = Paths.get("D:/temp/");
    int i = 0;
    for(IFileHandler<?> handler: handlers) {
      IInputDescriptor desc = new InputDescriptor.Builder(".pdf")
        .add(baseInput.resolve("600MB.pdf").toFile())
        .output(baseInput.resolve(outputPath[i++]))
        .build();
      handler.apply(desc).subscribe((s) -> {
        System.out.println(s.toString());
      });
    }
    
    IInputDescriptor desc = new InputDescriptor.Builder(".pdf")
        .add(baseInput.resolve("1.pdf").toFile())
        .add(baseInput.resolve("2.pdf").toFile())        
        .add(baseInput.resolve("3.pdf").toFile())
        .add(baseInput.resolve("4.pdf").toFile())
        .add(baseInput.resolve("5.pdf").toFile())
        .output(baseInput.resolve("merge"))
        .build();
    new JoinPdfHandler("final-merge-file").apply(desc).subscribe((s) -> {
      System.out.println(s.toString());
    });
  }

}
