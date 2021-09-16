import levenshtein.GithubCopilotLevenshtein;
import levenshtein.Levenshtein;
import levenshtein.NaiveLevenshtein;
import levenshtein.TwoRowsLevenshtein;
import levenshtein.WagnerFischerLevenshtein;

public class Tests {
  public static void main(String[] args) {
    testLevenshtein(new NaiveLevenshtein());
    testLevenshtein(new WagnerFischerLevenshtein());
    testLevenshtein(new TwoRowsLevenshtein());
    testLevenshtein(new GithubCopilotLevenshtein());
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
}
