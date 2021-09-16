package levenshtein;

import static tools.MathTools.*;

public class TwoRowsLevenshtein implements Levenshtein {
  @Override
  public int distance(CharSequence str1, CharSequence str2) {
    if (str1.length() == 0)
      return str2.length();
    if (str2.length() == 0)
      return str1.length();

    int[] distances = new int[str1.length()];
    int[] temp = new int[str1.length()];

    for (int j = 0; j < str2.length(); ++j) {
      for (int i = 0; i < str1.length(); ++i) {
        int topLeft = i > 0 && j > 0 ? distances[i - 1] : max(i, j);
        if (str1.charAt(i) == str2.charAt(j)) {
          temp[i] = topLeft;
        } else {
          int top = j > 0 ? distances[i] : i;
          int left = i > 0 ? temp[i - 1] : j;
          temp[i] = 1 + min(top, left, topLeft);
        }
      }
      int[] temp2 = temp;
      temp = distances;
      distances = temp2;
    }
    return distances[str1.length() - 1];
  }
}
