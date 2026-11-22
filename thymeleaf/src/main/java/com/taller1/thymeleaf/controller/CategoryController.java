package com.taller1.thymeleaf.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.taller1.thymeleaf.model.Category;
import com.taller1.thymeleaf.model.WikiEntry;
import com.taller1.thymeleaf.service.CategoryService;
import com.taller1.thymeleaf.service.WikiEntryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Sistema de etiquetas/categorias: indice general y detalle por categoria.
 */
@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final WikiEntryService wikiEntryService;

    /** Indice con todas las categorias y su numero de entradas. */
    @GetMapping("/wiki/categorias")
    public String categorias(HttpServletRequest request, Model model) {
        model.addAttribute("navigation",
            wikiEntryService.getNavigationTree(request.getRequestURL().toString()));
        model.addAttribute("summaries", categoryService.getAllWithCounts());
        model.addAttribute("categories", categoryService.getAll());
        return "categorias";
    }

    /** Entradas etiquetadas con una categoria concreta. */
    @GetMapping("/wiki/categorias/{slug}")
    public String categoria(@PathVariable String slug, HttpServletRequest request, Model model) {
        model.addAttribute("navigation",
            wikiEntryService.getNavigationTree(request.getRequestURL().toString()));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("slug", slug);

        Category category = categoryService.findBySlug(slug).orElse(null);
        model.addAttribute("category", category);

        List<WikiEntry> entries = category == null ? List.of() : categoryService.getEntries(slug);
        model.addAttribute("entries", entries);

        return "categoria";
    }
}
