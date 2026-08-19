package com.taller1.thymeleaf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taller1.thymeleaf.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByOrderIndexAscNameAsc();

    Optional<Category> findBySlug(String slug);

    /**
     * Numero de entradas asociadas a cada categoria, devuelto como pares
     * [idCategoria, total] para pintar el contador en el listado. Las categorias
     * sin entradas no aparecen en el resultado; el servicio les asigna cero.
     */
    @Query("SELECT c.id, COUNT(e.id) FROM WikiEntry e JOIN e.categories c GROUP BY c.id")
    List<Object[]> countEntriesByCategory();

    @Query("SELECT COUNT(e) FROM WikiEntry e JOIN e.categories c WHERE c.slug = :slug")
    long countEntriesBySlug(@Param("slug") String slug);
}
