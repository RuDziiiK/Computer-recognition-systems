package classification;

import model.featureVector;
import java.util.List;

public class DistanceCalculator {

    // Stara metoda (dla kompatybilności) - bierze wszystkie cechy
    public double calculate(featureVector v1, featureVector v2, MetricType metric) {
        return calculate(v1, v2, metric, null, null);
    }

    // NOWA METODA: Przyjmuje listy indeksów cech do uwzględnienia
    // Jeśli listy są null, bierze wszystkie.
    public double calculate(featureVector v1, featureVector v2, MetricType metric,
                            List<Integer> activeNumeric, List<Integer> activeText) {

        List<Double> n1 = v1.getNumericFeatures();
        List<Double> n2 = v2.getNumericFeatures();
        List<String> t1 = v1.getTextFeatures();
        List<String> t2 = v2.getTextFeatures();

        double dist = 0.0;
        double maxDist = 0.0;

        // 1. Cechy liczbowe
        int numCount = n1.size();
        for (int i = 0; i < numCount; i++) {
            // Sprawdź czy cecha jest aktywna (jeśli lista zdefiniowana)
            if (activeNumeric != null && !activeNumeric.contains(i)) continue;

            double diff = Math.abs(n1.get(i) - n2.get(i));

            if (metric == MetricType.CHEBYSHEV) {
                maxDist = Math.max(maxDist, diff);
            } else if (metric == MetricType.MANHATTAN) {
                dist += diff;
            } else { // EUCLIDEAN
                dist += Math.pow(diff, 2);
            }
        }

        // 2. Cechy tekstowe
        int textCount = t1.size();
        for (int i = 0; i < textCount; i++) {
            // Sprawdź czy cecha jest aktywna
            if (activeText != null && !activeText.contains(i)) continue;

            double diff = normalizedLevenshtein(t1.get(i), t2.get(i));

            if (metric == MetricType.CHEBYSHEV) {
                maxDist = Math.max(maxDist, diff);
            } else if (metric == MetricType.MANHATTAN) {
                dist += diff;
            } else { // EUCLIDEAN
                dist += Math.pow(diff, 2);
            }
        }

        switch (metric) {
            case EUCLIDEAN: return Math.sqrt(dist);
            case MANHATTAN: return dist;
            case CHEBYSHEV: return maxDist;
            default: return 0.0;
        }
    }

    // (Metoda normalizedLevenshtein pozostaje bez zmian - skopiuj ją z poprzedniej wersji)
    private double normalizedLevenshtein(String s1, String s2) {
        if (s1 == null || s2 == null) return 1.0;
        if (s1.equals(s2)) return 0.0;
        int len1 = s1.length();
        int len2 = s2.length();
        int maxLen = Math.max(len1, len2);
        if (maxLen == 0) return 0.0;
        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) dp[i][0] = i;
        for (int j = 0; j <= len2; j++) dp[0][j] = j;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return (double) dp[len1][len2] / maxLen;
    }
}