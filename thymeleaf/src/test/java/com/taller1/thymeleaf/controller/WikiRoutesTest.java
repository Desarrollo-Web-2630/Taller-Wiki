package com.taller1.thymeleaf.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Rutas del buscador y del sistema de categorias.
 *
 * <p>Comprueba, entre otras cosas, que estas rutas literales no las absorbe el
 * comodin {@code /wiki/**} de {@link WikiController}.</p>
 */
@SpringBootTest
class WikiRoutesTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Spring Boot 4 sirve @AutoConfigureMockMvc desde un modulo aparte;
        // aqui basta con construir MockMvc sobre el contexto web de la prueba.
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void laRutaDelBuscadorNoLaAbsorbeElComodinDeLaWiki() throws Exception {
        mockMvc.perform(get("/wiki/buscar"))
            .andExpect(status().isOk())
            .andExpect(view().name("busqueda"));
    }

    @Test
    void elBuscadorSinConsultaMuestraLaPaginaVacia() throws Exception {
        mockMvc.perform(get("/wiki/buscar"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("resultCount", 0));
    }

    @Test
    void elBuscadorDevuelveResultados() throws Exception {
        mockMvc.perform(get("/wiki/buscar").param("q", "gateway"))
            .andExpect(status().isOk())
            .andExpect(view().name("busqueda"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("<mark>")))
            .andExpect(content().string(
                org.hamcrest.Matchers.containsString("/wiki/historias-usuario/hu-14")));
    }

    @Test
    void elBuscadorEscapaLaConsultaEnLaRespuesta() throws Exception {
        mockMvc.perform(get("/wiki/buscar").param("q", "<script>alert(1)</script>"))
            .andExpect(status().isOk())
            .andExpect(content().string(
                org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("<script>alert(1)</script>"))));
    }

    @Test
    void elIndiceDeCategoriasNoLoAbsorbeElComodinDeLaWiki() throws Exception {
        mockMvc.perform(get("/wiki/categorias"))
            .andExpect(status().isOk())
            .andExpect(view().name("categorias"))
            .andExpect(model().attributeExists("summaries"));
    }

    @Test
    void elDetalleDeCategoriaListaSusEntradas() throws Exception {
        mockMvc.perform(get("/wiki/categorias/gateways"))
            .andExpect(status().isOk())
            .andExpect(view().name("categoria"))
            .andExpect(model().attributeExists("category"))
            .andExpect(content().string(
                org.hamcrest.Matchers.containsString("/wiki/historias-usuario/hu-14")));
    }

    @Test
    void unaCategoriaInexistenteMuestraUnMensajeYNoUnError() throws Exception {
        mockMvc.perform(get("/wiki/categorias/no-existe"))
            .andExpect(status().isOk())
            .andExpect(view().name("categoria"))
            .andExpect(model().attribute("category", org.hamcrest.Matchers.nullValue()))
            .andExpect(content().string(
                org.hamcrest.Matchers.containsString("Categoría no encontrada")));
    }

    @Test
    void elEndpointDeSugerenciasDevuelveJson() throws Exception {
        mockMvc.perform(get("/api/wiki/sugerencias").param("q", "gateway"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(content().string(
                org.hamcrest.Matchers.containsString("/wiki/historias-usuario/hu-14")));
    }

    @Test
    void elEndpointDeSugerenciasSinConsultaDevuelveListaVacia() throws Exception {
        mockMvc.perform(get("/api/wiki/sugerencias"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    void unArticuloSigueSirviendoseYMuestraSusEtiquetas() throws Exception {
        mockMvc.perform(get("/wiki/historias-usuario/hu-04"))
            .andExpect(status().isOk())
            .andExpect(view().name("wiki"))
            .andExpect(content().string(
                org.hamcrest.Matchers.containsString("/wiki/categorias/procesos")));
    }
}
