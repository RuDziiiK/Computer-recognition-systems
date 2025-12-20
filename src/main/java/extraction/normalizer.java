package extraction;

import model.featureVector;
import java.util.List;

public class normalizer {
    public static void normalize(List<featureVector> vectors) {
        if (vectors.isEmpty()) return;

        int numFeaturesCount = vectors.get(0).getNumericFeatures().size();

        for (int i = 0; i < numFeaturesCount; i++) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            // 1. Znajdź min i max dla i-tej cechy
            for (featureVector v : vectors) {
                double val = v.getNumericFeatures().get(i);
                if (val < min) min = val;
                if (val > max) max = val;
            }

            // Zabezpieczenie przed dzieleniem przez zero (gdy wszystkie wartości takie same)
            double range = max - min;
            if (range == 0) range = 1;

            // 2. Przeskaluj wartości: (x - min) / (max - min)
            for (featureVector v : vectors) {
                double original = v.getNumericFeatures().get(i);
                double normalized = (original - min) / range;
                v.getNumericFeatures().set(i, normalized);
            }
        }
    }
}
