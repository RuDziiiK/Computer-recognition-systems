package org.example;

import model.textDocument;
import parser.reutersParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            reutersParser parser = new reutersParser();

            String inputDir = "src/main/resources";
            //String outputFile = "data/filtered_documents.csv";

            List<textDocument> docs = parser.parseAllFiles(inputDir);
            //parser.saveToCsv(docs, outputFile);

            // Statystyka
            System.out.println("Filtered documents: " + docs.size());
            docs.stream()
                    .map(textDocument::getPlace)
                    .distinct()
                    .forEach(p -> {
                        long count = docs.stream().filter(d -> d.getPlace().equals(p)).count();
                        System.out.println(" - " + p + ": " + count);
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
