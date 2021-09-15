package levenshtein;

public interface Levenshtein {
  /**
   * A function that computes levenshtein distance between two strings
   * 
   * @param str1 the first string
   * @param str2 the second string
   * @return The levenshtein distance between s and t
   */
  int distance(CharSequence str1, CharSequence str2);
}
