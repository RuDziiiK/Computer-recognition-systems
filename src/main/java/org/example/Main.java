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
            System.out.println("Inicjalizacja aplikacji...");

            // 1. Parsowanie (jednorazowe)
            reutersParser parser = new reutersParser();
            String inputDir = "src/main/resources";

            // Sprawdzenie czy katalog istnieje (opcjonalne, dla bezpieczeństwa)
            // if (!java.nio.file.Files.exists(java.nio.file.Paths.get(inputDir))) ...

            List<textDocument> docs = parser.parseAllFiles(inputDir);

            if (docs.isEmpty()) {
                System.err.println("Błąd: Nie znaleziono dokumentów w " + inputDir);
                System.err.println("Upewnij się, że pliki .sgm znajdują się w katalogu resources.");
                return;
            }

            // 2. Ekstrakcja cech (jednorazowa)
            System.out.println("Ekstrakcja cech z " + docs.size() + " dokumentów...");
            featureExtractor extractor = new featureExtractor();
            List<featureVector> vectors = new ArrayList<>();

            for (textDocument doc : docs) {
                vectors.add(extractor.extract(doc));
            }

            // 3. Normalizacja (jednorazowa)
            System.out.println("Normalizacja wektorów...");
            normalizer.normalize(vectors);

            // 4. Uruchomienie interfejsu użytkownika
            ConsoleApp app = new ConsoleApp(vectors);
            app.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}