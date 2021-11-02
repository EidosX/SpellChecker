import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import dictionary.Dictionary;
import levenshtein.*;

public class Tests {
  public static void main(String[] args) throws IOException {
    timer(60000, () -> testLevenshtein(new WagnerFischerLevenshtein()), "WagnerFischerLevenshtein");
    timer(60000, () -> testLevenshtein(new GithubCopilotLevenshtein()), "GithubCopilotLevenshtein");
    timer(60000, () -> testLevenshtein(new TwoRowsLevenshtein()), "TwoRowsLevenshtein");
    timer(60000, () -> testLevenshtein(new GithubCopilot2Levenshtein()), "GithubCopilot2Levenshtein");
    timer(1000, () -> testLevenshtein(new NaiveLevenshtein()), "NaiveLevenshtein");

    String[] misspelledWords = Files.lines(new File("assets/fautes.txt").toPath()).toArray(String[]::new);

    Levenshtein levenshtein = new WagnerFischerLevenshtein();
    Scanner scanner = new Scanner(new File("assets/dico.txt"));
    Dictionary dictionary = new Dictionary(scanner, levenshtein);

    assertEquals(dictionary.closestWords("rémmencherais").get(0), "remmancherais");
    assertEquals(dictionary.closestWords("symetrisserait").get(0), "symétriserait");
    assertEquals(dictionary.closestWords("shasser-croisé").get(0), "chassé-croisé");

    timer(1, () -> {
      for (String word : misspelledWords) {
        var xs = dictionary.closestWords(word);
        // System.out.println(word + " -> " + xs.subList(0, Math.min(5, xs.size())));
      }
    }, "fautes.txt Correction");
  }

  public static void testLevenshtein(Levenshtein levenshtein) {
    assertEquals(levenshtein.distance("kitten", "sitting"), 3);
    assertEquals(levenshtein.distance("rabbit", "cabbages"), 5);
    assertEquals(levenshtein.distance("logarytmique", "algorithmique"), 5);
    assertEquals(levenshtein.distance("algorithmique", "logarytmique"), 5);
    assertEquals(levenshtein.distance("gily", "geely"), 2);
    assertEquals(levenshtein.distance("honda", "hyundai"), 3);
    assertEquals(levenshtein.distance("", "hyundai"), 7);
    assertEquals(levenshtein.distance("a", "hyundai"), 6);
    assertEquals(levenshtein.distance("ab", "hyundai"), 6);
  }

  public static <T> void assertEquals(T o1, T o2) {
    if (!o1.equals(o2))
      throw new AssertionError(o1 + " != " + o2);
  }

  public static void timer(int repetitions, Runnable r, String name) {
    long time = System.nanoTime();
    for (int i = 0; i < repetitions; ++i)
      r.run();
    time = System.nanoTime() - time;
    System.out.println(time / repetitions / 1000000. + "ms ("
        + (repetitions > 1 ? "average on " + repetitions + ", " : "") + name + ")");
  }
}
