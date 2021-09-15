package tools;

public class MathTools {
  public static int min(int x, int... xs) {
    int min = x;
    for (int val : xs)
      min = Math.min(min, val);
    return min;
  }
}
