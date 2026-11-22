package com.taller1.thymeleaf.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "wiki_entry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WikiEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private WikiEntry parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<WikiEntry> children = new ArrayList<>();

    /**
     * Etiquetas/categorias de la entrada. Se carga en EAGER porque las vistas
     * (articulo, buscador y listado de categorias) siempre pintan las etiquetas
     * fuera de la transaccion. Es un Set para no chocar con el JOIN FETCH de
     * children (Hibernate solo admite una coleccion tipo bag por consulta).
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "wiki_entry_category",
        joinColumns = @JoinColumn(name = "entry_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    @OrderBy("orderIndex ASC")
    private Set<Category> categories = new LinkedHashSet<>();

    @Column(name = "content_path")
    private String contentPath;

    @Column(name = "order_index")
    private int orderIndex;

    private boolean active;

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
    }
}
