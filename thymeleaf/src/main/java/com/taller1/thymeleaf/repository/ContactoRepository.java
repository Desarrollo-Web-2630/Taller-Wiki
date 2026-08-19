package com.taller1.thymeleaf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller1.thymeleaf.model.Contacto;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {
}
