package classification;

import java.util.*;

public class QualityEvaluator {

    // Klasa pomocnicza na liczniki TP, FP, FN
    private static class ClassMetrics {
        int tp = 0;
        int fp = 0;
        int fn = 0;
    }

    public static void printMetrics(List<String> trueLabels, List<String> predictedLabels) {
        if (trueLabels.size() != predictedLabels.size()) return;

        int correct = 0;
        int total = trueLabels.size();

        // Mapa przechowująca metryki dla każdej etykiety (kraju)
        Map<String, ClassMetrics> metricsMap = new HashMap<>();
        // Zbierz wszystkie unikalne etykiety
        Set<String> allLabels = new HashSet<>(trueLabels);
        allLabels.addAll(predictedLabels);

        for (String label : allLabels) {
            metricsMap.put(label, new ClassMetrics());
        }

        // Analiza pomyłek
        for (int i = 0; i < total; i++) {
            String actual = trueLabels.get(i);
            String predicted = predictedLabels.get(i);

            if (actual.equals(predicted)) {
                correct++;
                metricsMap.get(actual).tp++;
            } else {
                metricsMap.get(actual).fn++;      // Powinien być 'actual', ale nie wykryto
                metricsMap.get(predicted).fp++;   // Wykryto 'predicted', a to błąd
            }
        }

        // --- PREZENTACJA WYNIKÓW ---
        System.out.println("\n=== WYNIKI KLASYFIKACJI ===");
        double accuracy = (double) correct / total;
        System.out.printf("Accuracy (cały zbiór): %.2f%%\n", accuracy * 100);
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-15s | %-10s | %-10s | %-10s\n", "Klasa", "Precision", "Recall", "F1-Score");
        System.out.println("---------------------------------------------------------------");

        for (String label : allLabels) {
            ClassMetrics m = metricsMap.get(label);

            double precision = (m.tp + m.fp) == 0 ? 0 : (double) m.tp / (m.tp + m.fp);
            double recall = (m.tp + m.fn) == 0 ? 0 : (double) m.tp / (m.tp + m.fn);
            double f1 = (precision + recall) == 0 ? 0 : 2 * (precision * recall) / (precision + recall);

            System.out.printf("%-15s | %-10.2f | %-10.2f | %-10.2f\n",
                    label, precision, recall, f1);
        }
        System.out.println("---------------------------------------------------------------");
    }
}
