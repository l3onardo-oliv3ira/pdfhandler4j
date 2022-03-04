package com.github.pdfhandler4j.imp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.filehandler4j.IFileHandler;
import com.github.filehandler4j.IInputDescriptor;

public class TestAll {
  
  public static void main(String[] args) throws IOException {
    IFileHandler<?>[] handlers = new IFileHandler[] {
      new BySizePdfSplitter(50 * 1024 * 1024), //50MB max file size
      new ByCountPdfSplitter(300), //300 is pdf  max number of pages
      new ByPagesPdfSplitter(
        new DefaultPagesSlice(1, 1),
        new DefaultPagesSlice(3, 6),
        new DefaultPagesSlice(10, 20),
        new DefaultPagesSlice(25, 25),
        new DefaultPagesSlice(100, 200),
        new DefaultPagesSlice(400, 600),
        new DefaultPagesSlice(3000, 4500)
      ),
      new ByOddPagesPdfSplitter(),
      new ByEvenPagesPdfSplitter(), 
      new BySinglePagePdfSplitter() //single page per file
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
      IInputDescriptor desc = new PdfInputDescriptor.Builder()
        .add(baseInput.resolve("600MB.pdf").toFile())
        .output(baseInput.resolve(outputPath[i++]))
        .build();
      handler.apply(desc).subscribe((s) -> {
        System.out.println(s.toString());
      });
    }
    
    IInputDescriptor desc = new PdfInputDescriptor.Builder()
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
