package com.taller1.thymeleaf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Etiqueta o categoria tematica que se puede asociar a una o varias entradas
 * de la wiki. La relacion se declara solo desde {@link WikiEntry} para mantener
 * la asociacion unidireccional y evitar ciclos en equals/hashCode/toString.
 */
@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /** Identificador legible usado en las URLs: /wiki/categorias/{slug}. */
    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    /** Color hexadecimal con el que se pinta la etiqueta en las vistas. */
    private String color;

    @Column(name = "order_index")
    private int orderIndex;
}
