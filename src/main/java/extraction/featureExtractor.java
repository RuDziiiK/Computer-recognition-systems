package extraction;

import model.featureVector;
import model.textDocument;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class featureExtractor {
    // Lista słów stopu (do ignorowania przy szukaniu słów kluczowych)
    private static final Set<String> STOP_WORDS = Set.of(
            "the", "a", "an", "in", "on", "at", "to", "for", "of", "and", "or", "is", "was", "are", "were", "it", "that", "this"
    );

    public featureVector extract(textDocument doc) {
        String fullText = doc.getFullText().toLowerCase();
        List<String> words = tokenize(fullText);

        List<Double> numericFeatures = new ArrayList<>();
        List<String> textFeatures = new ArrayList<>();

        // --- CECHY LICZBOWE (8 cech) ---

        // 1. Liczba słów
        numericFeatures.add((double) words.size());

        // 2. Średnia długość słowa
        double avgWordLength = words.stream()
                .mapToInt(String::length)
                .average().orElse(0.0);
        numericFeatures.add(avgWordLength);

        // 3. Liczba zdań (przybliżona kropkami)
        long sentenceCount = fullText.chars().filter(ch -> ch == '.').count();
        numericFeatures.add((double) Math.max(1, sentenceCount)); // min 1 zdanie

        // 4. Stosunek unikalnych słów do wszystkich (bogactwo językowe)
        long uniqueWords = words.stream().distinct().count();
        double vocabularyRichness = words.isEmpty() ? 0 : (double) uniqueWords / words.size();
        numericFeatures.add(vocabularyRichness);

        // 5. Wystąpienie liczbowych wartości (częste w raportach finansowych)
        long numberCount = countRegexMatches(fullText, "\\d+");
        numericFeatures.add((double) numberCount);

        // 6. Częstość słowa "trade" (ważne dla Reuters)
        numericFeatures.add((double) Collections.frequency(words, "trade"));

        // 7. Częstość słowa "dollar" lub symbolu "$"
        numericFeatures.add((double) (Collections.frequency(words, "dollar") + countRegexMatches(fullText, "\\$")));

        // 8. Długość tekstu (liczba znaków)
        numericFeatures.add((double) fullText.length());


        // --- CECHY TEKSTOWE (2 cechy) ---

        // 9. Najczęstsze słowo (niebędące stop-wordem)
        String mostFreqWord = getMostFrequentWord(words);
        textFeatures.add(mostFreqWord);

        // 10. Pierwsze znaczące słowo (często miasto lub temat w depeszach)
        String firstKeyword = getFirstKeyword(words);
        textFeatures.add(firstKeyword);

        return new featureVector(doc.getPlace(), numericFeatures, textFeatures);
    }

    // --- Metody pomocnicze ---

    private List<String> tokenize(String text) {
        // Rozbija tekst na słowa, usuwa interpunkcję
        return Arrays.stream(text.split("[^a-zA-Z0-9]+"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    private long countRegexMatches(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        long count = 0;
        while (m.find()) count++;
        return count;
    }

    private String getMostFrequentWord(List<String> words) {
        return words.stream()
                .filter(w -> !STOP_WORDS.contains(w) && w.length() > 2)
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(""); // Pusty string jeśli brak słów
    }

    private String getFirstKeyword(List<String> words) {
        return words.stream()
                .filter(w -> !STOP_WORDS.contains(w) && w.length() > 2)
                .findFirst()
                .orElse("");
    }
}
