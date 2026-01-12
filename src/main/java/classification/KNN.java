package classification;

import model.featureVector;
import java.util.*;

public class KNN {
    private final List<featureVector> trainingSet;
    private final DistanceCalculator calculator;

    public KNN(List<featureVector> trainingSet) {
        this.trainingSet = trainingSet;
        this.calculator = new DistanceCalculator();
    }

    // Wersja podstawowa (wszystkie cechy)
    public String classify(featureVector sample, int k, MetricType metric) {
        return classify(sample, k, metric, null, null);
    }

    // Wersja rozszerzona (wybrane cechy)
    public String classify(featureVector sample, int k, MetricType metric,
                           List<Integer> activeNumeric, List<Integer> activeText) {

        List<Map.Entry<featureVector, Double>> distances = new ArrayList<>();

        for (featureVector trainVec : trainingSet) {
            double dist = calculator.calculate(sample, trainVec, metric, activeNumeric, activeText);
            distances.add(new AbstractMap.SimpleEntry<>(trainVec, dist));
        }

        distances.sort(Map.Entry.comparingByValue());

        Map<String, Integer> votes = new HashMap<>();
        for (int i = 0; i < Math.min(k, distances.size()); i++) {
            String label = distances.get(i).getKey().getLabel();
            votes.put(label, votes.getOrDefault(label, 0) + 1);
        }

        return votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }
}