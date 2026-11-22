package com.taller1.thymeleaf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taller1.thymeleaf.model.WikiEntry;

public interface WikiEntryRepository extends JpaRepository<WikiEntry, Long> {

    List<WikiEntry> findByParentIsNullOrderByOrderIndexAsc();

    @Query("SELECT e FROM WikiEntry e LEFT JOIN FETCH e.children WHERE e.parent IS NULL ORDER BY e.orderIndex")
    List<WikiEntry> findRootsWithChildren();

    Optional<WikiEntry> findByUrl(String url);

    /** Entradas indexables por el buscador (las que tienen contenido en disco). */
    @Query("SELECT e FROM WikiEntry e WHERE e.contentPath IS NOT NULL ORDER BY e.orderIndex")
    List<WikiEntry> findIndexable();

    /** Entradas etiquetadas con una categoria concreta. */
    @Query("SELECT e FROM WikiEntry e JOIN e.categories c WHERE c.slug = :slug ORDER BY e.orderIndex, e.title")
    List<WikiEntry> findByCategorySlug(@Param("slug") String slug);
}
