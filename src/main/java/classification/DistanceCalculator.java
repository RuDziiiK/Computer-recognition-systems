package classification;

import model.featureVector;
import java.util.List;

public class DistanceCalculator {

    public double calculate(featureVector v1, featureVector v2, MetricType metric) {
        List<Double> n1 = v1.getNumericFeatures();
        List<Double> n2 = v2.getNumericFeatures();
        List<String> t1 = v1.getTextFeatures();
        List<String> t2 = v2.getTextFeatures();

        if (n1.size() != n2.size() || t1.size() != t2.size()) {
            throw new IllegalArgumentException("Wektory mają różne wymiary!");
        }

        double dist = 0.0;
        double maxDist = 0.0; // Używane tylko dla metryki Czebyszewa

        // 1. Obliczanie dystansu dla cech liczbowych
        for (int i = 0; i < n1.size(); i++) {
            double val1 = n1.get(i);
            double val2 = n2.get(i);
            double diff = Math.abs(val1 - val2);

            // Logika dla konkretnych metryk
            if (metric == MetricType.CHEBYSHEV) {
                maxDist = Math.max(maxDist, diff);
            } else if (metric == MetricType.MANHATTAN) {
                dist += diff;
            } else { // EUCLIDEAN
                dist += Math.pow(diff, 2);
            }
        }

        // 2. Obliczanie dystansu dla cech tekstowych (Miara podobieństwa)
        for (int i = 0; i < t1.size(); i++) {
            String s1 = t1.get(i);
            String s2 = t2.get(i);
            // Używamy znormalizowanego Levenshteina (0.0 = identyczne, 1.0 = różne)
            double diff = normalizedLevenshtein(s1, s2);

            if (metric == MetricType.CHEBYSHEV) {
                maxDist = Math.max(maxDist, diff);
            } else if (metric == MetricType.MANHATTAN) {
                dist += diff;
            } else { // EUCLIDEAN
                dist += Math.pow(diff, 2);
            }
        }

        // Zwracanie wyniku końcowego
        switch (metric) {
            case EUCLIDEAN: return Math.sqrt(dist);
            case MANHATTAN: return dist;
            case CHEBYSHEV: return maxDist;
            default: return 0.0;
        }
    }

    // Metoda pomocnicza: Znormalizowany dystans Levenshteina (wynik 0-1)
    private double normalizedLevenshtein(String s1, String s2) {
        if (s1 == null || s2 == null) return 1.0;
        if (s1.equals(s2)) return 0.0;

        int len1 = s1.length();
        int len2 = s2.length();
        int maxLen = Math.max(len1, len2);
        if (maxLen == 0) return 0.0;

        // Algorytm Levenshteina (programowanie dynamiczne)
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) dp[i][0] = i;
        for (int j = 0; j <= len2; j++) dp[0][j] = j;

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                                dp[i - 1][j] + 1,      // usunięcie
                                dp[i][j - 1] + 1),     // wstawienie
                        dp[i - 1][j - 1] + cost); // zamiana
            }
        }

        return (double) dp[len1][len2] / maxLen;
    }
}
