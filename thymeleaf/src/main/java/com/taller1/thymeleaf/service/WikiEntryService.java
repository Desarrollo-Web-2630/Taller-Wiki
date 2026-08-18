package com.taller1.thymeleaf.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.taller1.thymeleaf.model.WikiEntry;
import com.taller1.thymeleaf.repository.WikiEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.Data;

@Service
@RequiredArgsConstructor
public class WikiEntryService {

    private final WikiEntryRepository repository;

    private static final Pattern HEADING_PATTERN =
        Pattern.compile("<(h[23])(?:\\s[^>]*)?>(.*?)</\\1>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public List<WikiEntry> getNavigationTree() {
        List<WikiEntry> roots = repository.findRootsWithChildren();
        for (WikiEntry root : roots) {
            loadChildrenRecursive(root);
        }
        return roots;
    }

    public Optional<ArticleResult> getArticle(String url) {
        Optional<WikiEntry> entryOpt = repository.findByUrl(url);
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
            List<TocItem> toc = parseToc(html);
            return Optional.of(new ArticleResult(entry, html, toc));
        } catch (IOException e) {
            return Optional.of(new ArticleResult(entry, "<p>Contenido no disponible.</p>", new ArrayList<>()));
        }
    }

    public List<TocItem> parseToc(String html) {
        List<TocItem> toc = new ArrayList<>();
        Matcher matcher = HEADING_PATTERN.matcher(html);

        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();
            String rawText = matcher.group(2).replaceAll("<[^>]+>", "").trim();
            int level = tag.equals("h2") ? 2 : 3;
            String id = slugify(rawText);
            toc.add(new TocItem(id, rawText, level));
        }

        return toc;
    }

    private String slugify(String text) {
        return text.toLowerCase()
            .replaceAll("[^a-z0-9áéíóúñü\\s-]", "")
            .replaceAll("[\\s]+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }

    private void loadChildrenRecursive(WikiEntry entry) {
        if (entry.hasChildren()) {
            for (WikiEntry child : entry.getChildren()) {
                loadChildrenRecursive(child);
            }
        }
    }

    @Data
    public static class TocItem {
        private final String id;
        private final String text;
        private final int level;
    }

    @Data
    public static class ArticleResult {
        private final WikiEntry entry;
        private final String content;
        private final List<TocItem> toc;
    }
}
