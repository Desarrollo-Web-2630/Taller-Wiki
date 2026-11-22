package com.taller1.thymeleaf.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taller1.thymeleaf.model.Category;
import com.taller1.thymeleaf.service.CategoryService;
import com.taller1.thymeleaf.service.SearchService;
import com.taller1.thymeleaf.service.SearchService.SearchHit;
import com.taller1.thymeleaf.service.WikiEntryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Buscador de la wiki: pagina de resultados y endpoint de autocompletado.
 *
 * <p>Las rutas son literales, asi que Spring las prioriza sobre el comodin
 * {@code /wiki/**} declarado en {@link WikiController}.</p>
 */
@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final CategoryService categoryService;
    private final WikiEntryService wikiEntryService;

    @GetMapping("/wiki/buscar")
    public String buscar(@RequestParam(name = "q", required = false) String query,
                         @RequestParam(name = "categoria", required = false) String categorySlug,
                         HttpServletRequest request,
                         Model model) {

        String cleanQuery = query == null ? "" : query.trim();
        String cleanCategory = categorySlug == null || categorySlug.isBlank() ? null : categorySlug.trim();

        List<SearchHit> results = cleanQuery.isEmpty()
            ? List.of()
            : searchService.search(cleanQuery, cleanCategory);

        model.addAttribute("navigation",
            wikiEntryService.getNavigationTree(request.getRequestURL().toString()));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("query", cleanQuery);
        model.addAttribute("selectedCategory", cleanCategory);
        model.addAttribute("activeCategory", categoryService.findBySlug(cleanCategory).orElse(null));
        model.addAttribute("results", results);
        model.addAttribute("resultCount", results.size());

        return "busqueda";
    }

    /** Sugerencias en vivo mientras se escribe en la caja de busqueda. */
    @GetMapping("/api/wiki/sugerencias")
    @ResponseBody
    public List<Suggestion> sugerencias(@RequestParam(name = "q", required = false) String query,
                                        @RequestParam(name = "categoria", required = false) String categorySlug) {

        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        String cleanCategory = categorySlug == null || categorySlug.isBlank() ? null : categorySlug.trim();

        return searchService.suggest(query.trim(), cleanCategory).stream()
            .map(hit -> new Suggestion(
                hit.getEntry().getTitle(),
                hit.getEntry().getUrl(),
                hit.getHighlightedTitle(),
                hit.getSnippet(),
                hit.getEntry().getCategories().stream().map(Category::getName).toList()))
            .toList();
    }

    /**
     * Proyeccion plana de un resultado para el JSON del autocompletado; evita
     * serializar la entidad y sus asociaciones perezosas.
     */
    public record Suggestion(String title, String url, String highlightedTitle,
                             String snippet, List<String> categories) {
    }
}
