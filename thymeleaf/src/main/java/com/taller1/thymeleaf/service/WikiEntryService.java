package com.taller1.thymeleaf.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.taller1.thymeleaf.model.WikiEntry;
import com.taller1.thymeleaf.repository.WikiEntryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WikiEntryService {

    private final WikiEntryRepository repository;

    private static final Pattern HEADING_PATTERN =
        Pattern.compile("<(h[23])(?:\\s[^>]*)?>(.*?)</\\1>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public List<WikiEntry> getNavigationTree(String currentUrl) {
        List<WikiEntry> roots = repository.findRootsWithChildren();

        for (WikiEntry root : roots) {
            resetActiveState(root);
        }

        markActivePath(roots, currentUrl);
        return roots;
    }

    public Optional<ArticleResult> getArticle(String uri, String fullUrl) {
        Optional<WikiEntry> entryOpt = findEntryByUrl(uri, fullUrl);
        if (entryOpt.isEmpty()) {
            return Optional.empty();
        }

        WikiEntry entry = entryOpt.get();
        if (entry.getContentPath() == null || entry.getContentPath().isBlank()) {
            return Optional.of(new ArticleResult(entry, "", new ArrayList<>()));
        }

        try {
            Path filePath = Paths.get("src/main/resources/templates", entry.getContentPath());
            String html = Files.readString(filePath);
            String htmlWithAnchors = addIdsToHeadings(html);
            List<TocItem> toc = parseToc(htmlWithAnchors, entry);
            return Optional.of(new ArticleResult(entry, htmlWithAnchors, toc));
        } catch (IOException e) {
            return Optional.of(new ArticleResult(entry, "<p>Contenido no disponible.</p>", new ArrayList<>()));
        }
    }

    public String addIdsToHeadings(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }

        Matcher matcher = Pattern.compile("<(h[23])\\b([^>]*)>(.*?)</\\1>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
            .matcher(html);

        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase(Locale.ROOT);
            String attrs = matcher.group(2) == null ? "" : matcher.group(2);
            String innerHtml = matcher.group(3);
            String text = innerHtml.replaceAll("<[^>]+>", "").trim();

            if (text.isBlank()) {
                matcher.appendReplacement(buffer, Matcher.quoteReplacement(matcher.group(0)));
                continue;
            }

            String id = slugify(text);
            if (id.isBlank()) {
                matcher.appendReplacement(buffer, Matcher.quoteReplacement(matcher.group(0)));
                continue;
            }

            String cleanedAttrs = attrs.replaceFirst("(?i)\\s+id=\\\"[^\\\"]*\\\"", "");
            String replacement = "<" + tag + " id=\"" + id + "\"" + cleanedAttrs + ">" + innerHtml + "</" + tag + ">";
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private Optional<WikiEntry> findEntryByUrl(String uri, String fullUrl) {
        return repository.findByUrl(uri)
            .or(() -> repository.findByUrl(fullUrl))
            .or(() -> repository.findByUrl(normalizeUrl(fullUrl)))
            .or(() -> repository.findByUrl(normalizeUrl(uri)))
            .or(() -> repository.findByUrl("http://localhost:8080" + uri));
    }

    public List<TocItem> parseToc(String html, WikiEntry currentEntry) {
        List<TocItem> toc = new ArrayList<>();
        Matcher matcher = HEADING_PATTERN.matcher(html);

        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();
            String rawText = matcher.group(2).replaceAll("<[^>]+>", "").trim();
            int level = tag.equals("h2") ? 2 : 3;
            String id = slugify(rawText);
            String href = resolveTocHref(currentEntry, rawText, id);
            toc.add(new TocItem(id, rawText, level, href));
        }

        return toc;
    }

    private String resolveTocHref(WikiEntry currentEntry, String headingText, String id) {
        if (currentEntry == null) {
            return "#" + id;
        }

        String headingCode = extractHuCode(headingText);
        if (headingCode != null) {
            for (WikiEntry candidate : collectCandidateEntries(currentEntry)) {
                String candidateCode = extractHuCode(candidate.getTitle());
                if (headingCode.equals(candidateCode) && candidate.getUrl() != null) {
                    return candidate.getUrl();
                }
            }
        }

        List<WikiEntry> candidates = collectCandidateEntries(currentEntry);
        String normalizedHeading = normalizeHeadingText(headingText);
        for (WikiEntry candidate : candidates) {
            String candidateTitle = normalizeHeadingText(candidate.getTitle());
            if (candidateTitle.equals(normalizedHeading)
                    || candidateTitle.contains(normalizedHeading)
                    || normalizedHeading.contains(candidateTitle)) {
                return candidate.getUrl() != null ? candidate.getUrl() : "#" + id;
            }
        }

        return "#" + id;
    }

    private List<WikiEntry> collectCandidateEntries(WikiEntry currentEntry) {
        List<WikiEntry> candidates = new ArrayList<>();
        if (currentEntry.hasChildren()) {
            candidates.addAll(currentEntry.getChildren());
        }
        if (currentEntry.getParent() != null && currentEntry.getParent().hasChildren()) {
            candidates.addAll(currentEntry.getParent().getChildren());
        }
        if (currentEntry.getParent() != null && currentEntry.getParent().getParent() != null) {
            WikiEntry grandParent = currentEntry.getParent().getParent();
            if (grandParent.hasChildren()) {
                candidates.addAll(grandParent.getChildren());
            }
        }
        return candidates;
    }

    private String extractHuCode(String text) {
        if (text == null) {
            return null;
        }

        Matcher matcher = Pattern.compile("(?i)hu[- ]?\\d{1,2}").matcher(text);
        if (matcher.find()) {
            return matcher.group(0).toLowerCase(Locale.ROOT).replace(" ", "-");
        }
        return null;
    }

    private String normalizeHeadingText(String text) {
        if (text == null) {
            return "";
        }

        return text.toLowerCase(Locale.ROOT)
            .replaceAll("<[^>]+>", "")
            .replaceAll("&nbsp;", " ")
            .replaceAll("[^a-z0-9áéíóúñü\\s-]", "")
            .replaceAll("\\s+", " ")
            .trim();
    }

    private void resetActiveState(WikiEntry entry) {
        entry.setActive(false);
        if (entry.hasChildren()) {
            for (WikiEntry child : entry.getChildren()) {
                resetActiveState(child);
            }
        }
    }

    private void markActivePath(List<WikiEntry> entries, String currentUrl) {
        if (currentUrl == null || currentUrl.isBlank()) {
            return;
        }

        for (WikiEntry entry : entries) {
            if (markActivePath(entry, currentUrl)) {
                return;
            }
        }
    }

    private boolean markActivePath(WikiEntry entry, String currentUrl) {
        String normalizedCurrent = normalizeUrl(currentUrl);
        String normalizedEntryUrl = normalizeUrl(entry.getUrl());
        boolean matchesCurrent = normalizedEntryUrl.equals(normalizedCurrent);

        if (entry.hasChildren()) {
            for (WikiEntry child : entry.getChildren()) {
                if (markActivePath(child, currentUrl)) {
                    entry.setActive(true);
                    return true;
                }
            }
        }

        if (matchesCurrent) {
            entry.setActive(true);
            return true;
        }

        return false;
    }

    private String normalizeUrl(String url) {
        if (url == null || url.isBlank()) {
            return "";
        }

        return url
            .replaceFirst("(?i)^https?://[^/]+", "")
            .replaceAll("/+$", "");
    }

    private String slugify(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");

        return normalized.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("[\\s]+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }

    @Data
    public static class TocItem {
        private final String id;
        private final String text;
        private final int level;
        private final String href;
    }

    @Data
    public static class ArticleResult {
        private final WikiEntry entry;
        private final String content;
        private final List<TocItem> toc;
    }
}
