package classification;

import model.featureVector;
import java.util.*;
import java.util.stream.Collectors;

public class KNN {
    private final List<featureVector> trainingSet;
    private final DistanceCalculator calculator;

    public KNN(List<featureVector> trainingSet) {
        this.trainingSet = trainingSet;
        this.calculator = new DistanceCalculator();
    }

    public String classify(featureVector sample, int k, MetricType metric) {
        // Mapa przechowująca pary: <Wektor, Dystans>
        List<Map.Entry<featureVector, Double>> distances = new ArrayList<>();

        // 1. Oblicz odległość od każdego punktu w zbiorze treningowym
        for (featureVector trainVec : trainingSet) {
            double dist = calculator.calculate(sample, trainVec, metric);
            distances.add(new AbstractMap.SimpleEntry<>(trainVec, dist));
        }

        // 2. Sortuj rosnąco po dystansie i wybierz k pierwszych
        distances.sort(Map.Entry.comparingByValue());

        // 3. Głosowanie
        Map<String, Integer> votes = new HashMap<>();
        for (int i = 0; i < Math.min(k, distances.size()); i++) {
            String label = distances.get(i).getKey().getLabel();
            votes.put(label, votes.getOrDefault(label, 0) + 1);
        }

        // 4. Zwróć klasę z największą liczbą głosów
        return votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }
}
