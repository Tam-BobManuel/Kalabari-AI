package org.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class IgboWebsiteScraper {

    private static final String OUTPUT_FILE = "igbo_web_corpus_v31.txt";

    private static final int MAX_DEPTH = 3;
    private static final int MAX_PAGES_PER_DOMAIN = 200;
    private static final int REQUEST_TIMEOUT_MS = 30000;
    private static final int REQUEST_DELAY_MS = 1500;
    private static final int MAX_RETRIES = 3;
    private static final int MIN_TEXT_LENGTH = 40;
    private static final int MIN_PAGE_TEXT_LENGTH = 120;

    private static final String CHROME_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    private static final Map<String, List<String>> DOMAIN_SEEDS = new LinkedHashMap<>();

    static {
        DOMAIN_SEEDS.put("bbc", List.of(
                "https://www.bbc.com/igbo"
        ));

        DOMAIN_SEEDS.put("jw", List.of(
                "https://www.jw.org/ig",
                "https://www.jw.org/ig/library"
        ));

        DOMAIN_SEEDS.put("wordproject", List.of(
                "https://www.wordproject.org/bibles/ig/index.htm",
                "https://www.wordproject.org/bibles/ig/01/1.htm",
                "https://www.wordproject.org/bibles/ig/43/1.htm"
        ));
    }

    private static final Set<String> globalVisitedUrls = new HashSet<>();
    private static final Set<String> savedPageFingerprints = new HashSet<>();

    private static int totalPagesVisited = 0;
    private static int totalPagesFailed = 0;
    private static int totalDocumentsSaved = 0;

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(OUTPUT_FILE), StandardCharsets.UTF_8))) {

            for (Map.Entry<String, List<String>> entry : DOMAIN_SEEDS.entrySet()) {
                String domainKey = entry.getKey();
                List<String> seeds = entry.getValue();

                System.out.println("\n================================================");
                System.out.println("Starting domain: " + domainKey);
                System.out.println("================================================");

                crawlDomain(domainKey, seeds, writer);
            }

            System.out.println("\n================ FINAL STATS ================");
            System.out.println("Pages visited: " + totalPagesVisited);
            System.out.println("Pages failed: " + totalPagesFailed);
            System.out.println("Documents saved: " + totalDocumentsSaved);
            System.out.println("Output file: " + OUTPUT_FILE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void crawlDomain(String domainKey, List<String> seeds, BufferedWriter writer) {
        Queue<CrawlNode> queue = new LinkedList<>();
        Set<String> domainVisited = new HashSet<>();
        int pagesProcessedForDomain = 0;

        for (String seed : seeds) {
            queue.offer(new CrawlNode(seed, 0));
        }

        while (!queue.isEmpty() && pagesProcessedForDomain < MAX_PAGES_PER_DOMAIN) {
            CrawlNode node = queue.poll();
            String url = normalizeUrl(node.url);

            if (url.isBlank()) continue;
            if (domainVisited.contains(url) || globalVisitedUrls.contains(url)) continue;
            if (!isAllowedDomain(url, domainKey)) continue;
            if (!isLikelyUsefulPage(url, domainKey)) continue;

            domainVisited.add(url);
            globalVisitedUrls.add(url);

            try {
                Connection.Response response = fetchWithRetry(url);
                if (response == null) {
                    totalPagesFailed++;
                    continue;
                }

                int statusCode = response.statusCode();
                String contentType = response.contentType();

                if (statusCode >= 400) {
                    System.out.println("Skipped HTTP " + statusCode + ": " + url);
                    totalPagesFailed++;
                    continue;
                }

                if (contentType == null || !contentType.toLowerCase().contains("text/html")) {
                    System.out.println("Skipped non-HTML: " + url);
                    continue;
                }

                Document doc = response.parse();
                totalPagesVisited++;
                pagesProcessedForDomain++;

                boolean saved = extractAndSavePage(doc, url, domainKey, writer);

                System.out.println("Visited: " + url);
                System.out.println("Depth: " + node.depth + " | Saved page: " + saved);

                if (node.depth < MAX_DEPTH) {
                    List<String> children = extractCandidateLinks(doc, domainKey);

                    for (String child : children) {
                        String normalizedChild = normalizeUrl(child);
                        if (!normalizedChild.isBlank()
                                && !domainVisited.contains(normalizedChild)
                                && !globalVisitedUrls.contains(normalizedChild)) {
                            queue.offer(new CrawlNode(normalizedChild, node.depth + 1));
                        }
                    }
                }

                sleepQuietly(REQUEST_DELAY_MS);

            } catch (Exception e) {
                totalPagesFailed++;
                System.out.println("Failed: " + url + " -> " + e.getMessage());
            }
        }

        System.out.println("Finished domain: " + domainKey + " | Pages processed: " + pagesProcessedForDomain);
    }

    private static Connection.Response fetchWithRetry(String url) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return Jsoup.connect(url)
                        .userAgent(CHROME_USER_AGENT)
                        .referrer("https://www.google.com")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .header("Cache-Control", "no-cache")
                        .header("Pragma", "no-cache")
                        .timeout(REQUEST_TIMEOUT_MS)
                        .followRedirects(true)
                        .ignoreHttpErrors(true)
                        .maxBodySize(0)
                        .execute();

            } catch (Exception e) {
                System.out.println("Attempt " + attempt + " failed for " + url + " -> " + e.getMessage());
                sleepQuietly(1500L * attempt);
            }
        }
        return null;
    }

    private static boolean extractAndSavePage(Document doc, String url, String domainKey, BufferedWriter writer) throws IOException {
        Elements elements = extractTextElements(doc, domainKey);
        String title = normalizeText(doc.title());

        List<String> chunks = new ArrayList<>();

        for (Element element : elements) {
            String text = normalizeText(element.text());

            if (text.length() < MIN_TEXT_LENGTH) continue;
            if (looksLikeBoilerplate(text)) continue;
            if (!isIgbo(text)) continue;

            chunks.add(text);
        }

        if (chunks.isEmpty()) return false;

        String pageText = String.join(" ", deduplicatePreserveOrder(chunks)).trim();

        if (pageText.length() < MIN_PAGE_TEXT_LENGTH) return false;

        String fingerprint = fingerprint(pageText);
        if (!savedPageFingerprints.add(fingerprint)) return false;

        writer.write(pageText);
        writer.newLine();

        totalDocumentsSaved++;
        return true;
    }

    private static Elements extractTextElements(Document doc, String domainKey) {
        if ("bbc".equals(domainKey)) {
            Elements els = doc.select("main p, article p, [data-component='text-block'] p");
            if (!els.isEmpty()) return els;
        }

        if ("jw".equals(domainKey)) {
            Elements els = doc.select("main p, article p, div[class*=content] p, div[class*=article] p");
            if (!els.isEmpty()) return els;
        }

        if ("wordproject".equals(domainKey)) {
            Elements els = doc.select("td, p, span, font");
            if (!els.isEmpty()) return els;
        }

        return doc.select("p");
    }

    private static List<String> extractCandidateLinks(Document doc, String domainKey) {
        Set<String> links = new LinkedHashSet<>();
        Elements anchors = doc.select("a[href]");

        for (Element a : anchors) {
            String url = normalizeUrl(a.absUrl("href"));

            if (url.isBlank()) continue;
            if (!isAllowedDomain(url, domainKey)) continue;
            if (!isLikelyUsefulPage(url, domainKey)) continue;

            links.add(url);
        }

        return new ArrayList<>(links);
    }

    private static boolean isAllowedDomain(String url, String domainKey) {
        String lower = url.toLowerCase();

        return switch (domainKey) {
            case "bbc" -> lower.startsWith("https://www.bbc.com/igbo");
            case "jw" -> lower.startsWith("https://www.jw.org/ig");
            case "wordproject" -> lower.startsWith("https://www.wordproject.org/bibles/ig/");
            default -> false;
        };
    }

    private static boolean isLikelyUsefulPage(String url, String domainKey) {
        String lower = url.toLowerCase();

        if (lower.contains("mailto:") || lower.contains("tel:") || lower.contains("javascript:")) return false;
        if (lower.matches(".*\\.(jpg|jpeg|png|gif|svg|webp|pdf|zip|mp3|mp4|avi|mov)$")) return false;

        if ("bbc".equals(domainKey)) {
            return !lower.contains("/live")
                    && !lower.contains("/video")
                    && !lower.contains("/sounds")
                    && !lower.contains("/programmes");
        }

        if ("jw".equals(domainKey)) {
            return !lower.contains("/search/")
                    && !lower.contains("finder?")
                    && !lower.contains("/contact-us/")
                    && !lower.contains("/site-map/")
                    && !lower.contains("/downloads/");
        }

        if ("wordproject".equals(domainKey)) {
            return lower.matches("https://www\\.wordproject\\.org/bibles/ig/\\d{2}/\\d+\\.htm")
                    || lower.equals("https://www.wordproject.org/bibles/ig/index.htm");
        }

        return true;
    }

    private static boolean isIgbo(String text) {
        String normalized = " " + text.toLowerCase()
                .replaceAll("[^\\p{L}\\s’']", " ")
                .replaceAll("\\s+", " ")
                .trim() + " ";

        int score = 0;
        String[] markers = {
                " ndị ", " bụ ", " nke ", " anyị ", " gị ", " unu ",
                " ha ", " mmadụ ", " ihe ", " akụkọ ", " dịka ",
                " banyere ", " enweghị ", " n’", " ọch", " ọmụ",
                " ụmụ ", " ụbọchị ", " obodo ", " okwu "
        };

        for (String marker : markers) {
            if (normalized.contains(marker)) score++;
        }

        return score >= 2;
    }

    private static boolean looksLikeBoilerplate(String text) {
        String lower = text.toLowerCase();
        return lower.contains("cookie")
                || lower.contains("privacy policy")
                || lower.contains("terms of use")
                || lower.contains("advertisement")
                || lower.contains("sign up")
                || lower.contains("subscribe")
                || lower.contains("read more")
                || lower.contains("menu")
                || lower.contains("navigation")
                || lower.contains("all rights reserved");
    }

    private static List<String> deduplicatePreserveOrder(List<String> input) {
        return new ArrayList<>(new LinkedHashSet<>(input));
    }

    private static String normalizeUrl(String url) {
        if (url == null) return "";
        String cleaned = url.trim();
        int fragmentIndex = cleaned.indexOf('#');
        if (fragmentIndex >= 0) cleaned = cleaned.substring(0, fragmentIndex);
        if (cleaned.endsWith("/")) cleaned = cleaned.substring(0, cleaned.length() - 1);
        return cleaned;
    }

    private static String normalizeText(String text) {
        if (text == null) return "";
        return text.replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static String fingerprint(String text) {
        return text.toLowerCase()
                .replaceAll("[^\\p{L}\\p{Nd}\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String toJsonLine(String domainKey, String url, String title, String text) {
        return "{"
                + "\"domain\":\"" + escapeJson(domainKey) + "\","
                + "\"url\":\"" + escapeJson(url) + "\","
                + "\"title\":\"" + escapeJson(title) + "\","
                + "\"timestamp\":\"" + Instant.now().toString() + "\","
                + "\"text\":\"" + escapeJson(text) + "\""
                + "}";
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static class CrawlNode {
        String url;
        int depth;

        CrawlNode(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }
    }
}