package org.example;

import classification.*;
import model.featureVector;

import java.util.*;

public class ConsoleApp {
    private final Scanner scanner = new Scanner(System.in);
    private final List<featureVector> allVectors;

    public ConsoleApp(List<featureVector> allVectors) {
        this.allVectors = allVectors;
    }

    public void run() {
        System.out.println("==========================================");
        System.out.println("   KLASYFIKATOR TEKSTÓW REUTERS (k-NN)   ");
        System.out.println("==========================================");
        System.out.println("Dane załadowane. Liczba wektorów: " + allVectors.size());

        boolean running = true;
        while (running) {
            try {
                System.out.println("\n--- Konfiguracja eksperymentu ---");

                // 1. Pobranie parametru K
                int k = getIntInput("Podaj wartość k (np. 3, 5, 10): ", 1, 100);

                // 2. Pobranie proporcji podziału
                int trainPercent = getIntInput("Podaj procent zbioru uczącego (np. 60 dla 60/40): ", 1, 99);
                double splitRatio = trainPercent / 100.0;

                // 3. Wybór metryki
                MetricType metric = getMetricInput();

                // 4. Podział zbioru (Deterministyczny!)
                System.out.println("\nPrzygotowywanie danych...");
                // Kopiujemy listę, żeby nie mieszać oryginału przy kolejnych próbach
                List<featureVector> workingList = new ArrayList<>(allVectors);
                // Używamy stałego ziarna (seed = 123) dla powtarzalności wyników
                Collections.shuffle(workingList, new Random(123));

                int trainSize = (int) (workingList.size() * splitRatio);
                List<featureVector> trainSet = workingList.subList(0, trainSize);
                List<featureVector> testSet = workingList.subList(trainSize, workingList.size());

                System.out.println("Zbiór treningowy: " + trainSet.size() + " elementów");
                System.out.println("Zbiór testowy:    " + testSet.size() + " elementów");

                // 5. Klasyfikacja
                System.out.println("Rozpoczynanie klasyfikacji...");
                long startTime = System.currentTimeMillis();

                KNN knn = new KNN(trainSet);
                List<String> trueLabels = new ArrayList<>();
                List<String> predictedLabels = new ArrayList<>();

                int progressCounter = 0;
                for (featureVector v : testSet) {
                    String predicted = knn.classify(v, k, metric);
                    trueLabels.add(v.getLabel());
                    predictedLabels.add(predicted);

                    // Prosty pasek postępu co 100 elementów
                    progressCounter++;
                    if (progressCounter % 100 == 0) {
                        System.out.print(".");
                    }
                }
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("\nCzas obliczeń: " + duration + " ms");

                // 6. Wyniki
                QualityEvaluator.printMetrics(trueLabels, predictedLabels);

                // Pytanie o kontynuację
                System.out.print("\nCzy chcesz wykonać kolejne badanie? (t/n): ");
                String answer = scanner.next();
                if (!answer.equalsIgnoreCase("t")) {
                    running = false;
                }

            } catch (Exception e) {
                System.out.println("Wystąpił błąd: " + e.getMessage());
                scanner.nextLine(); // czyszczenie bufora
            }
        }
        System.out.println("Zakończono działanie aplikacji.");
    }

    // --- Metody pomocnicze do wczytywania danych ---

    private int getIntInput(String message, int min, int max) {
        int val = 0;
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                val = scanner.nextInt();
                if (val >= min && val <= max) break;
            } else {
                scanner.next(); // usuń błędne dane
            }
            System.out.println("Błąd: Wprowadź liczbę całkowitą z zakresu " + min + "-" + max);
        }
        return val;
    }

    private MetricType getMetricInput() {
        System.out.println("Wybierz metrykę:");
        System.out.println("  1. Euklidesowa");
        System.out.println("  2. Uliczna (Manhattan)");
        System.out.println("  3. Czebyszewa");
        int choice = getIntInput("Twój wybór (1-3): ", 1, 3);

        switch (choice) {
            case 1: return MetricType.EUCLIDEAN;
            case 2: return MetricType.MANHATTAN;
            case 3: return MetricType.CHEBYSHEV;
            default: return MetricType.EUCLIDEAN;
        }
    }
}
