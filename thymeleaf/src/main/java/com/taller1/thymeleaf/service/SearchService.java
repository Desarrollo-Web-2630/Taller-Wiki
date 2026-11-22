package com.taller1.thymeleaf.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.taller1.thymeleaf.model.Category;
import com.taller1.thymeleaf.model.WikiEntry;
import com.taller1.thymeleaf.repository.WikiEntryRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Buscador de la wiki.
 *
 * <p>Indexa el titulo, las etiquetas y el texto plano de cada entrada. La
 * comparacion ignora mayusculas y tildes, y el resultado incluye un fragmento
 * de contexto con los terminos resaltados.</p>
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    private final WikiEntryRepository repository;
    private final WikiEntryService wikiEntryService;

    /** Caracteres antes del primer termino encontrado dentro del fragmento. */
    private static final int SNIPPET_RADIUS = 90;
    /** Longitud maxima del fragmento de contexto. */
    private static final int SNIPPET_LENGTH = 260;
    /** Tope de sugerencias devueltas por el autocompletado. */
    private static final int SUGGESTION_LIMIT = 8;
    /** Longitud minima de un termino para tenerse en cuenta. */
    private static final int MIN_TERM_LENGTH = 2;

    private static final Pattern SCRIPT_OR_STYLE =
        Pattern.compile("(?is)<(script|style)[^>]*>.*?</\\1>");
    private static final Pattern HTML_COMMENT = Pattern.compile("(?s)<!--.*?-->");
    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    /** Texto plano ya extraido de cada fichero, invalidado si el fichero cambia. */
    private final ConcurrentHashMap<Long, CachedText> textCache = new ConcurrentHashMap<>();

    /**
     * Ejecuta una busqueda. Primero exige que aparezcan todos los terminos y, si
     * eso no devuelve nada, repite admitiendo coincidencias parciales.
     *
     * @param query        texto introducido por la persona usuaria
     * @param categorySlug slug de categoria para acotar la busqueda (opcional)
     */
    public List<SearchHit> search(String query, String categorySlug) {
        List<String> terms = tokenize(query);
        if (terms.isEmpty()) {
            return List.of();
        }

        List<WikiEntry> candidates = (categorySlug == null || categorySlug.isBlank())
            ? repository.findIndexable()
            : repository.findByCategorySlug(categorySlug);

        List<SearchHit> hits = collect(candidates, terms, query, true);
        if (hits.isEmpty()) {
            hits = collect(candidates, terms, query, false);
        }
        return hits;
    }

    /** Version reducida usada por el autocompletado del formulario de busqueda. */
    public List<SearchHit> suggest(String query, String categorySlug) {
        List<SearchHit> hits = search(query, categorySlug);
        return hits.size() > SUGGESTION_LIMIT ? hits.subList(0, SUGGESTION_LIMIT) : hits;
    }

    private List<SearchHit> collect(List<WikiEntry> candidates, List<String> terms,
                                    String rawQuery, boolean requireAll) {
        String foldedPhrase = fold(rawQuery.trim());
        List<SearchHit> hits = new ArrayList<>();

        for (WikiEntry entry : candidates) {
            String plain = plainTextOf(entry);
            String foldedBody = fold(plain);
            String foldedTitle = fold(entry.getTitle());
            String foldedTags = fold(categoryNames(entry));

            int score = 0;
            int matchedTerms = 0;

            for (String term : terms) {
                int inTitle = countOccurrences(foldedTitle, term);
                int inTags = countOccurrences(foldedTags, term);
                int inBody = countOccurrences(foldedBody, term);

                if (inTitle + inTags + inBody == 0) {
                    continue;
                }
                matchedTerms++;
                // El titulo y las etiquetas pesan mas que el cuerpo del articulo.
                score += inTitle * 25 + inTags * 12 + Math.min(inBody, 12) * 2;
            }

            if (matchedTerms == 0 || (requireAll && matchedTerms < terms.size())) {
                continue;
            }

            // Bonus por coincidencia literal de toda la consulta en el titulo.
            if (!foldedPhrase.isEmpty() && foldedTitle.contains(foldedPhrase)) {
                score += 40;
            }

            hits.add(new SearchHit(
                entry,
                buildSnippet(plain, terms),
                highlight(entry.getTitle(), terms),
                score,
                matchedTerms));
        }

        hits.sort(Comparator.comparingInt(SearchHit::getScore).reversed()
            .thenComparing(hit -> hit.getEntry().getTitle(), String.CASE_INSENSITIVE_ORDER));
        return hits;
    }

    // ------------------------------------------------------------------
    // Indexado
    // ------------------------------------------------------------------

    private String plainTextOf(WikiEntry entry) {
        Path path = wikiEntryService.resolveContentPath(entry);
        if (path == null || !Files.isReadable(path)) {
            return "";
        }

        long lastModified;
        try {
            lastModified = Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return "";
        }

        CachedText cached = textCache.get(entry.getId());
        if (cached != null && cached.lastModified() == lastModified) {
            return cached.text();
        }

        try {
            String text = toPlainText(Files.readString(path));
            textCache.put(entry.getId(), new CachedText(lastModified, text));
            return text;
        } catch (IOException e) {
            return "";
        }
    }

    /** Convierte el HTML del articulo en texto plano apto para indexar. */
    private String toPlainText(String html) {
        String text = SCRIPT_OR_STYLE.matcher(html).replaceAll(" ");
        text = HTML_COMMENT.matcher(text).replaceAll(" ");
        text = HTML_TAG.matcher(text).replaceAll(" ");
        text = HtmlUtils.htmlUnescape(text);
        return WHITESPACE.matcher(text).replaceAll(" ").trim();
    }

    private String categoryNames(WikiEntry entry) {
        if (!entry.hasCategories()) {
            return "";
        }
        return entry.getCategories().stream()
            .map(Category::getName)
            .collect(Collectors.joining(" "));
    }

    // ------------------------------------------------------------------
    // Fragmentos y resaltado
    // ------------------------------------------------------------------

    /** Fragmento de texto alrededor del primer termino encontrado, ya resaltado. */
    private String buildSnippet(String plain, List<String> terms) {
        if (plain.isEmpty()) {
            return "";
        }

        String folded = fold(plain);
        int position = -1;
        for (String term : terms) {
            int index = folded.indexOf(term);
            if (index >= 0 && (position < 0 || index < position)) {
                position = index;
            }
        }
        if (position < 0) {
            position = 0;
        }

        int start = Math.max(0, position - SNIPPET_RADIUS);
        int end = Math.min(plain.length(), start + SNIPPET_LENGTH);

        // Recortar por limites de palabra para no partir terminos por la mitad.
        if (start > 0) {
            int space = plain.indexOf(' ', start);
            if (space > 0 && space < position) {
                start = space + 1;
            }
        }
        if (end < plain.length()) {
            int space = plain.lastIndexOf(' ', end);
            if (space > start) {
                end = space;
            }
        }

        String fragment = plain.substring(start, end);
        return (start > 0 ? "… " : "") + highlight(fragment, terms) + (end < plain.length() ? " …" : "");
    }

    /**
     * Devuelve el texto escapado como HTML con los terminos envueltos en
     * {@code <mark>}. Se escapa segmento a segmento, de modo que la unica
     * etiqueta que llega a la vista es la del resaltado.
     */
    private String highlight(String text, List<String> terms) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String folded = fold(text);
        boolean[] marked = new boolean[text.length()];
        for (String term : terms) {
            int from = 0;
            int index;
            while ((index = folded.indexOf(term, from)) >= 0) {
                Arrays.fill(marked, index, index + term.length(), true);
                from = index + term.length();
            }
        }

        StringBuilder out = new StringBuilder(text.length() + 32);
        int i = 0;
        while (i < text.length()) {
            int j = i;
            while (j < text.length() && marked[j] == marked[i]) {
                j++;
            }
            String segment = HtmlUtils.htmlEscape(text.substring(i, j), "UTF-8");
            if (marked[i]) {
                out.append("<mark>").append(segment).append("</mark>");
            } else {
                out.append(segment);
            }
            i = j;
        }
        return out.toString();
    }

    // ------------------------------------------------------------------
    // Normalizacion
    // ------------------------------------------------------------------

    /** Separa la consulta en terminos normalizados y sin duplicados. */
    private List<String> tokenize(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return Arrays.stream(fold(query).split("[^\\p{Alnum}]+"))
            .filter(term -> term.length() >= MIN_TERM_LENGTH)
            .distinct()
            .toList();
    }

    /**
     * Normaliza a minusculas y sin tildes manteniendo un mapeo 1:1 entre
     * caracteres, de forma que los indices del texto normalizado siguen siendo
     * validos sobre el texto original (necesario para resaltar).
     */
    private static String fold(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char lower = Character.toLowerCase(text.charAt(i));
            String decomposed = Normalizer.normalize(String.valueOf(lower), Normalizer.Form.NFD);
            out.append(decomposed.isEmpty() ? lower : decomposed.charAt(0));
        }
        return out.toString().toLowerCase(Locale.ROOT);
    }

    private static int countOccurrences(String haystack, String needle) {
        if (haystack.isEmpty() || needle.isEmpty()) {
            return 0;
        }
        int count = 0;
        int from = 0;
        int index;
        while ((index = haystack.indexOf(needle, from)) >= 0) {
            count++;
            from = index + needle.length();
        }
        return count;
    }

    private record CachedText(long lastModified, String text) {
    }

    /** Una entrada que coincide con la busqueda, con su fragmento resaltado. */
    @Data
    public static class SearchHit {
        private final WikiEntry entry;
        /** HTML seguro: texto escapado con los terminos envueltos en {@code <mark>}. */
        private final String snippet;
        /** HTML seguro con el titulo resaltado. */
        private final String highlightedTitle;
        private final int score;
        private final int matchedTerms;
    }
}
