package parser;

import model.textDocument;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class reutersParser {

    // Dozwolone miejsca
    private static final Set<String> ALLOWED_PLACES = Set.of(
            "west-germany", "usa", "france", "uk", "canada", "japan"
    );

    // Główna metoda parsująca wszystkie pliki
    public List<textDocument> parseAllFiles(String directoryPath) throws IOException {
        List<textDocument> documents = new ArrayList<>();

        try (var files = Files.list(Paths.get(directoryPath))) {
            for (Path path : files.collect(Collectors.toList())) {
                if (path.toString().endsWith(".sgm")) {
                    System.out.println("Parsing file: " + path.getFileName());
                    String content = Files.readString(path, StandardCharsets.ISO_8859_1);
                    documents.addAll(parseSgmFile(content));
                }
            }
        }

        System.out.println("Parsed total: " + documents.size() + " documents");
        return documents;
    }

    // Parsowanie pojedynczego pliku .sgm
    private List<textDocument> parseSgmFile(String fileContent) {
        List<textDocument> docs = new ArrayList<>();
        Pattern reutersPattern = Pattern.compile("<REUTERS(.*?)</REUTERS>", Pattern.DOTALL);
        Matcher matcher = reutersPattern.matcher(fileContent);

        while (matcher.find()) {
            String reutersBlock = matcher.group();
            Optional<textDocument> doc = parseSingleDocument(reutersBlock);
            doc.ifPresent(docs::add);
        }

        return docs;
    }

    // Parsowanie pojedynczego artykułu
    private Optional<textDocument> parseSingleDocument(String block) {
        int id = extractNewId(block);
        String place = extractSingleTag(block, "PLACES");
        if (place == null || !ALLOWED_PLACES.contains(place)) {
            return Optional.empty();
        }

        String title = extractTag(block, "TITLE");
        String body = extractTag(block, "BODY");
        return Optional.of(new textDocument(id, place, clean(title), clean(body)));
    }

    // Pomocnicze metody ekstrakcji

    private int extractNewId(String block) {
        Pattern p = Pattern.compile("NEWID=\"(\\d+)\"");
        Matcher m = p.matcher(block);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    private String extractTag(String text, String tag) {
        Pattern p = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">", Pattern.DOTALL);
        Matcher m = p.matcher(text);
        return m.find() ? m.group(1).trim() : null;
    }

    // Ekstrakcja pojedynczego miejsca z <PLACES><D>...</D></PLACES>
    private String extractSingleTag(String text, String tag) {
        Pattern p = Pattern.compile("<" + tag + ">\\s*<D>(.*?)</D>\\s*</" + tag + ">", Pattern.DOTALL);
        Matcher m = p.matcher(text);
        if (m.find()) {
            // Jeśli więcej niż jedno <D> — odrzucamy
            Pattern multiple = Pattern.compile("<D>.*?</D>", Pattern.DOTALL);
            Matcher multi = multiple.matcher(m.group(0));
            int count = 0;
            while (multi.find()) count++;
            if (count == 1) {
                return m.group(1).trim();
            }
        }
        return null;
    }

    private String clean(String text) {
        if (text == null) return "";
        return text.replaceAll("\\s+", " ")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .trim();
    }

    // Zapis do CSV
    public void saveToCsv(List<textDocument> docs, String outputPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath), StandardCharsets.UTF_8)) {
            writer.write("id,place,title,body\n");
            for (textDocument doc : docs) {
                String safeTitle = doc.getTitle().replaceAll("\"", "'");
                String safeBody = doc.getBody().replaceAll("\"", "'").replaceAll("\n", " ");
                writer.write(String.format("%d,%s,\"%s\",\"%s\"\n",
                        doc.getId(), doc.getPlace(), safeTitle, safeBody));
            }
        }
        System.out.println("Saved to " + outputPath);
    }
}
