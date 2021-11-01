import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import dictionary.Dictionary;
import levenshtein.Levenshtein;
import levenshtein.WagnerFischerLevenshtein;

public class FautesTxt {
  public static void main(String[] args) throws IOException, InterruptedException {
    String[] misspelledWords = Files.lines(new File("assets/fautes.txt").toPath()).toArray(String[]::new);

    Levenshtein levenshtein = new WagnerFischerLevenshtein();
    Scanner scanner = new Scanner(new File("assets/dico.txt"));

    Dictionary dictionary = new Dictionary(scanner, levenshtein);

    @SuppressWarnings("unchecked")
    List<String>[] correctedWords = new List[misspelledWords.length];
    Thread[] threads = new Thread[20];

    long t = System.nanoTime();

    for (int I = 0; I < threads.length; I++) {
      final int i = I;
      final double n = (double) misspelledWords.length / threads.length;
      threads[i] = new Thread(() -> {
        for (int j = (int) (n * i); j < n * (i + 1); j++)
          correctedWords[j] = dictionary.closestWords(misspelledWords[j]);
      });
      threads[i].start();
    }
    for (Thread thread : threads)
      thread.join();

    t = System.nanoTime() - t;

    for (int i = 0; i < misspelledWords.length; ++i) {
      var corrected = correctedWords[i].stream().limit(5).collect(Collectors.toList());
      System.out.println(misspelledWords[i] + " -> " + corrected);
    }

    System.out.println("time: " + t / 1000000 + "ms");
  }
}
