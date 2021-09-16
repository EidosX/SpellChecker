package levenshtein;

import static tools.MathTools.*;

public class WagnerFischerLevenshtein implements Levenshtein {
  @Override
  public int distance(CharSequence str1, CharSequence str2) {
    int[][] distances = new int[str2.length()][str1.length()];
    for (int j = 0; j < str2.length(); ++j) {
      for (int i = 0; i < str1.length(); ++i) {
        int topLeft = i > 0 && j > 0 ? distances[j - 1][i - 1] : max(i, j);
        if (str1.charAt(i) == str2.charAt(j)) {
          distances[j][i] = topLeft;
        } else {
          int top = j > 0 ? distances[j - 1][i] : i;
          int left = i > 0 ? distances[j][i - 1] : j;
          distances[j][i] = 1 + min(top, left, topLeft);
        }
      }
    }
    return distances[str2.length() - 1][str1.length() - 1];
  }
}
