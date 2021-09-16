package levenshtein;

import static tools.MathTools.min;

public class GithubCopilotLevenshtein implements Levenshtein {
  @Override
  public int distance(CharSequence str1, CharSequence str2) {
    int n = str1.length();
    int m = str2.length();
    int[][] d = new int[n + 1][m + 1];
    for (int i = 0; i <= n; i++)
      d[i][0] = i;
    for (int j = 0; j <= m; j++)
      d[0][j] = j;
    for (int j = 1; j <= m; j++)
      for (int i = 1; i <= n; i++)
        d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1,
            d[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1));
    return d[n][m];
  }
}
