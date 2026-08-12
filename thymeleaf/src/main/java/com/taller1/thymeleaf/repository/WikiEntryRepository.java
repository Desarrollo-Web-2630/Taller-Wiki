package com.taller1.thymeleaf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.taller1.thymeleaf.model.WikiEntry;

public interface WikiEntryRepository extends JpaRepository<WikiEntry, Long> {

    List<WikiEntry> findByParentIsNullOrderByOrderIndexAsc();

    @Query("SELECT e FROM WikiEntry e LEFT JOIN FETCH e.children WHERE e.parent IS NULL ORDER BY e.orderIndex")
    List<WikiEntry> findRootsWithChildren();

    Optional<WikiEntry> findByUrl(String url);
}
