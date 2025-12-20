package model;

import java.util.List;

public class featureVector {
    private final String label; // Np. "usa", "japan"
    private final List<Double> numericFeatures; // Cechy liczbowe (np. 1-8)
    private final List<String> textFeatures;    // Cechy tekstowe (np. 9-10)

    public featureVector(String label, List<Double> numericFeatures, List<String> textFeatures) {
        this.label = label;
        this.numericFeatures = numericFeatures;
        this.textFeatures = textFeatures;
    }

    public String getLabel() {
        return label;
    }

    public List<Double> getNumericFeatures() {
        return numericFeatures;
    }

    public List<String> getTextFeatures() {
        return textFeatures;
    }

    // Metoda pomocnicza do podglądu
    @Override
    public String toString() {
        return String.format("Label: %s, Num: %s, Text: %s", label, numericFeatures, textFeatures);
    }
}
