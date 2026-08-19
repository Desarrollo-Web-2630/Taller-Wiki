package com.taller1.thymeleaf.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.taller1.thymeleaf.model.Category;
import com.taller1.thymeleaf.model.WikiEntry;
import com.taller1.thymeleaf.repository.CategoryRepository;
import com.taller1.thymeleaf.repository.WikiEntryRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Consulta del sistema de etiquetas/categorias de la wiki.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final WikiEntryRepository wikiEntryRepository;

    /** Todas las categorias con el numero de entradas que tiene cada una. */
    public List<CategorySummary> getAllWithCounts() {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : categoryRepository.countEntriesByCategory()) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        return categoryRepository.findAllByOrderByOrderIndexAscNameAsc().stream()
            .map(category -> new CategorySummary(category, counts.getOrDefault(category.getId(), 0L)))
            .toList();
    }

    /** Categorias disponibles para el desplegable de filtro del buscador. */
    public List<Category> getAll() {
        return categoryRepository.findAllByOrderByOrderIndexAscNameAsc();
    }

    public Optional<Category> findBySlug(String slug) {
        return slug == null || slug.isBlank()
            ? Optional.empty()
            : categoryRepository.findBySlug(slug);
    }

    /** Entradas etiquetadas con la categoria indicada. */
    public List<WikiEntry> getEntries(String slug) {
        return slug == null || slug.isBlank()
            ? List.of()
            : wikiEntryRepository.findByCategorySlug(slug);
    }

    @Data
    public static class CategorySummary {
        private final Category category;
        private final long entryCount;
    }
}
