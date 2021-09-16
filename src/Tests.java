import levenshtein.*;

public class Tests {
  public static void main(String[] args) {
    timer(10000, () -> testLevenshtein(new WagnerFischerLevenshtein()), "WagnerFischerLevenshtein");
    timer(10000, () -> testLevenshtein(new GithubCopilotLevenshtein()), "GithubCopilotLevenshtein");
    timer(10000, () -> testLevenshtein(new TwoRowsLevenshtein()), "TwoRowsLevenshtein");
    timer(10000, () -> testLevenshtein(new GithubCopilot2Levenshtein()), "GithubCopilot2Levenshtein");
    timer(10000, () -> testLevenshtein(new NaiveLevenshtein()), "NaiveLevenshtein");
  }

  public static void testLevenshtein(Levenshtein levenshtein) {
    assertEquals(levenshtein.distance("kitten", "sitting"), 3);
    assertEquals(levenshtein.distance("rabbit", "cabbages"), 5);
    assertEquals(levenshtein.distance("logarytmique", "algorithmique"), 5);
    assertEquals(levenshtein.distance("algorithmique", "logarytmique"), 5);
    assertEquals(levenshtein.distance("gily", "geely"), 2);
    assertEquals(levenshtein.distance("honda", "hyundai"), 3);
  }

  public static void assertEquals(Object o1, Object o2) {
    if (!o1.equals(o2))
      throw new AssertionError(o1 + " != " + o2);
  }

  public static void timer(int repetitions, Runnable r, String name) {
    long time = System.nanoTime();
    for (int i = 0; i < repetitions; ++i)
      r.run();
    time = System.nanoTime() - time;
    System.out.println(name + " ran in " + time / 1000000 + "ms");
  }
}
