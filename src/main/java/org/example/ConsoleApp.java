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
        System.out.println("Liczba załadowanych wektorów: " + allVectors.size());

        boolean running = true;
        while (running) {
            System.out.println("\n================ MENU GŁÓWNE ================");
            System.out.println("1. Pojedyncza klasyfikacja (parametry ręczne)");
            System.out.println("2. Zadanie 5: Zależność Accuracy od k");
            System.out.println("3. Zadanie 6: Zależność Accuracy od podziału zbioru");
            System.out.println("4. Zadanie 7: Zależność Accuracy od metryki");
            System.out.println("5. Zadanie 8: Wpływ podzbiorów cech na jakość");
            System.out.println("0. Wyjście");
            System.out.print("Wybierz opcję: ");

            int choice = -1;
            if (scanner.hasNextInt()) choice = scanner.nextInt();
            else scanner.next();

            switch (choice) {
                case 1: runSingleExperiment(); break;
                case 2: runTask5_K_Dependency(); break;
                case 3: runTask6_Split_Dependency(); break;
                case 4: runTask7_Metric_Dependency(); break;
                case 5: runTask8_Feature_Subsets(); break;
                case 0: running = false; break;
                default: System.out.println("Niepoprawny wybór.");
            }
        }
    }

    // --- ZADANIE 5: Accuracy vs K ---
    private void runTask5_K_Dependency() {
        System.out.println("\n--- ZADANIE 5: Badanie wpływu parametru k ---");

        // Parametry stałe
        double splitRatio = 0.6;
        MetricType metric = MetricType.EUCLIDEAN;

        // Przygotuj zbiór (deterministycznie)
        ExperimentData data = prepareData(splitRatio);

        System.out.printf("Stałe parametry: Podział=%.0f%%, Metryka=%s\n", splitRatio*100, metric);
        System.out.println("-------------------------");
        System.out.printf("%-5s | %-10s\n", "k", "Accuracy");
        System.out.println("-------------------------");

        int[] kValues = {1, 2, 3, 5, 7, 9, 11, 15, 20, 30}; // 10 różnych wartości

        for (int k : kValues) {
            KNN knn = new KNN(data.trainSet);
            double accuracy = evaluate(knn, data.testSet, k, metric, null, null);
            System.out.printf("%-5d | %-10.4f\n", k, accuracy);
        }
    }

    // --- ZADANIE 6: Accuracy vs Split Ratio ---
    private void runTask6_Split_Dependency() {
        System.out.println("\n--- ZADANIE 6: Badanie wpływu podziału zbioru ---");

        // Parametry stałe
        int k = 5;
        MetricType metric = MetricType.EUCLIDEAN;

        System.out.printf("Stałe parametry: k=%d, Metryka=%s\n", k, metric);
        System.out.println("-------------------------");
        System.out.printf("%-10s | %-10s\n", "Split %", "Accuracy");
        System.out.println("-------------------------");

        double[] splits = {0.5, 0.6, 0.7, 0.8, 0.9}; // 5 wartości

        for (double split : splits) {
            // Uwaga: Dla każdego splitu musimy na nowo podzielić zbiór!
            ExperimentData data = prepareData(split);
            KNN knn = new KNN(data.trainSet);
            double accuracy = evaluate(knn, data.testSet, k, metric, null, null);
            System.out.printf("%-10.0f | %-10.4f\n", split * 100, accuracy);
        }
    }

    // --- ZADANIE 7: Accuracy vs Metryka ---
    private void runTask7_Metric_Dependency() {
        System.out.println("\n--- ZADANIE 7: Badanie wpływu metryki ---");

        // Parametry stałe
        int k = 5;
        double splitRatio = 0.6;
        ExperimentData data = prepareData(splitRatio);

        System.out.printf("Stałe parametry: k=%d, Podział=%.0f%%\n", k, splitRatio*100);
        System.out.println("------------------------------------");
        System.out.printf("%-15s | %-10s\n", "Metryka", "Accuracy");
        System.out.println("------------------------------------");

        for (MetricType metric : MetricType.values()) {
            KNN knn = new KNN(data.trainSet);
            double accuracy = evaluate(knn, data.testSet, k, metric, null, null);
            System.out.printf("%-15s | %-10.4f\n", metric, accuracy);
        }
    }

    // --- ZADANIE 8: Wybór cech ---
    private void runTask8_Feature_Subsets() {
        System.out.println("\n--- ZADANIE 8: Analiza podzbiorów cech ---");

        // Parametry stałe
        int k = 5;
        double splitRatio = 0.6;
        MetricType metric = MetricType.EUCLIDEAN;
        ExperimentData data = prepareData(splitRatio);
        KNN knn = new KNN(data.trainSet);

        // Definiujemy 4 podzbiory cech
        // Indeksy muszą odpowiadać tym z featureExtractor!
        // Numeryczne: 0:Words, 1:AvgLen, 2:Sentences, 3:Richness, 4:Nums, 5:Trade, 6:Dollar, 7:TextLen
        // Tekstowe: 0:FreqWord, 1:Keyword

        // 1. Wszystkie cechy (Baseline)
        System.out.println("\n1. Wszystkie cechy:");
        double acc1 = evaluate(knn, data.testSet, k, metric, null, null);
        System.out.printf("Accuracy: %.4f\n", acc1);

        // 2. Tylko słowa kluczowe (cechy "finansowe": Trade, Dollar, Keyword)
        // Num: 5, 6; Text: 1
        System.out.println("\n2. Tylko słowa kluczowe (Trade, Dollar, Keyword):");
        double acc2 = evaluate(knn, data.testSet, k, metric, List.of(5, 6), List.of(1));
        System.out.printf("Accuracy: %.4f\n", acc2);

        // 3. Tylko cechy "stylometryczne" (długości, bogactwo, bez konkretnych słów)
        // Num: 0, 1, 2, 3, 7; Text: brak
        System.out.println("\n3. Stylometria (Długości, bogactwo językowe):");
        double acc3 = evaluate(knn, data.testSet, k, metric, List.of(0, 1, 2, 3, 7), List.of());
        System.out.printf("Accuracy: %.4f\n", acc3);

        // 4. Tylko cechy tekstowe (Stringi)
        // Num: brak; Text: 0, 1
        System.out.println("\n4. Tylko cechy tekstowe (Najczęstsze słowo + Keyword):");
        double acc4 = evaluate(knn, data.testSet, k, metric, List.of(), List.of(0, 1));
        System.out.printf("Accuracy: %.4f\n", acc4);

        System.out.println("\nAnaliza: Porównaj powyższe wyniki, aby ocenić, która grupa cech jest najważniejsza.");
    }

    // --- Metody pomocnicze ---

    // Struktura pomocnicza
    private static class ExperimentData {
        List<featureVector> trainSet;
        List<featureVector> testSet;
        public ExperimentData(List<featureVector> train, List<featureVector> test) {
            this.trainSet = train;
            this.testSet = test;
        }
    }

    // Deterministyczny podział danych
    private ExperimentData prepareData(double splitRatio) {
        List<featureVector> workingList = new ArrayList<>(allVectors);
        Collections.shuffle(workingList, new Random(123)); // Stałe ziarno!

        int trainSize = (int) (workingList.size() * splitRatio);
        // Zabezpieczenie przed pustymi zbiorami
        if (trainSize == 0) trainSize = 1;
        if (trainSize == workingList.size()) trainSize = workingList.size() - 1;

        List<featureVector> trainSet = workingList.subList(0, trainSize);
        List<featureVector> testSet = workingList.subList(trainSize, workingList.size());
        return new ExperimentData(trainSet, testSet);
    }

    // Szybka ewaluacja zwracająca tylko Accuracy
    private double evaluate(KNN knn, List<featureVector> testSet, int k, MetricType metric,
                            List<Integer> activeNum, List<Integer> activeText) {
        int correct = 0;
        for (featureVector v : testSet) {
            String predicted = knn.classify(v, k, metric, activeNum, activeText);
            if (predicted.equals(v.getLabel())) {
                correct++;
            }
        }
        return (double) correct / testSet.size();
    }

    // Stara metoda z poprzedniego etapu (do ręcznych testów)
    private void runSingleExperiment() {
        System.out.println("Podaj k:");
        int k = scanner.nextInt();
        System.out.println("Podaj split (np. 0.6):");
        double split = scanner.nextDouble();

        ExperimentData data = prepareData(split);
        KNN knn = new KNN(data.trainSet);

        // ... (Reszta logiki wyświetlania pełnych metryk jak wcześniej) ...
        System.out.println("Obliczanie...");
        double acc = evaluate(knn, data.testSet, k, MetricType.EUCLIDEAN, null, null);
        System.out.println("Accuracy: " + acc);
    }
}