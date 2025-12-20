package org.example;

import extraction.featureExtractor;
import extraction.normalizer;
import model.featureVector;
import model.textDocument;
import parser.reutersParser;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Parsowanie
            reutersParser parser = new reutersParser();
            String inputDir = "src/main/resources"; // Upewnij się, że pliki .sgm tu są
            List<textDocument> docs = parser.parseAllFiles(inputDir);
            System.out.println("Załadowano dokumentów: " + docs.size());

            // 2. Ekstrakcja cech
            System.out.println("Ekstrakcja cech...");
            featureExtractor extractor = new featureExtractor();
            List<featureVector> vectors = new ArrayList<>();

            for (textDocument doc : docs) {
                vectors.add(extractor.extract(doc));
            }

            // 3. Normalizacja (wymagana dla k-NN!)
            System.out.println("Normalizacja wektorów...");
            normalizer.normalize(vectors);

            // Podgląd pierwszego wektora po normalizacji
            if (!vectors.isEmpty()) {
                System.out.println("Przykładowy wektor (znormalizowany):");
                System.out.println(vectors.get(0));
            }

            // --- TU BĘDZIE KOLEJNY ETAP: Podział na Train/Test i k-NN ---

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}