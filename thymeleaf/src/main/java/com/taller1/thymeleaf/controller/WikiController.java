package com.taller1.thymeleaf.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.taller1.thymeleaf.service.WikiEntryService;
import com.taller1.thymeleaf.service.WikiEntryService.ArticleResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WikiController {

    private final WikiEntryService wikiEntryService;

    @GetMapping({"/wiki", "/wiki/**"})
    public String wiki(HttpServletRequest request, Model model) {
        String url = request.getRequestURI();

        model.addAttribute("navigation", wikiEntryService.getNavigationTree());

        wikiEntryService.getArticle(url).ifPresentOrElse(
            article -> {
                model.addAttribute("content", article.getContent());
                model.addAttribute("toc", article.getToc());
                model.addAttribute("entry", article.getEntry());
            },
            () -> {
                model.addAttribute("content", "<h2>Página no encontrada</h2><p>La entrada solicitada no existe.</p>");
                model.addAttribute("toc", new ArrayList<>());
            }
        );

        return "wiki";
    }
}
