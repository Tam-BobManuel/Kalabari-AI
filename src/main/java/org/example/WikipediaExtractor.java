package org.example;

import javax.xml.stream.*;
import java.io.*;

public class WikipediaExtractor {

    public static void main(String[] args) {

        String inputFile = "igwiki-latest-pages-articles-multistream.xml";
        String outputFile = "igbo_wikipedia_corpus.txt";

        try (
                FileInputStream fis = new FileInputStream(inputFile);
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))
        ) {

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(fis);

            boolean insideText = false;

            while (reader.hasNext()) {

                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT &&
                        reader.getLocalName().equals("text")) {

                    insideText = true;
                }

                if (event == XMLStreamConstants.CHARACTERS && insideText) {

                    String content = reader.getText();
                    String cleaned = cleanText(content);

                    if (cleaned.length() > 30) {
                        writer.write(cleaned);
                        writer.newLine();
                    }
                }

                if (event == XMLStreamConstants.END_ELEMENT &&
                        reader.getLocalName().equals("text")) {

                    insideText = false;
                }
            }

            System.out.println("Extraction complete.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String cleanText(String text) {
        if (text == null || text.isBlank()) return "";

        text = text.replaceAll("(?s)<!--.*?-->", " ");
        text = text.replaceAll("(?s)<ref[^>]*>.*?</ref>", " ");
        text = text.replaceAll("<ref[^/]*/>", " ");
        text = text.replaceAll("(?s)\\{\\|.*?\\|\\}", " ");
        text = text.replaceAll("(?s)\\{\\{[^{}]*\\}\\}", " ");
        text = text.replaceAll("\\[\\[(File|Image):[^\\]]*\\]\\]", " ");
        text = text.replaceAll("\\[\\[Category:[^\\]]*\\]\\]", " ");
        text = text.replaceAll("\\[\\[[^\\]|]+\\|([^\\]]+)\\]\\]", "$1");
        text = text.replaceAll("\\[\\[([^\\]]+)\\]\\]", "$1");
        text = text.replaceAll("\\[(https?://[^\\s\\]]+)\\s+([^\\]]+)\\]", "$2");
        text = text.replaceAll("\\[(https?://[^\\]]+)\\]", " ");
        text = text.replaceAll("={2,}\\s*([^=]+?)\\s*={2,}", " ");
        text = text.replaceAll("<[^>]+>", " ");
        text = text.replaceAll("'{2,}", "");
        text = text.replaceAll("[^\\p{L}\\p{N}\\s]", " ");
        text = text.replaceAll("\\s+", " ").trim();

        return text;
    }
}