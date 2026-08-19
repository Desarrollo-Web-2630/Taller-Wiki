package com.taller1.thymeleaf.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.taller1.thymeleaf.service.SearchService.SearchHit;

/**
 * Comportamiento del buscador sobre los datos reales de data.sql.
 */
@SpringBootTest
class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    private List<String> urls(List<SearchHit> hits) {
        return hits.stream().map(hit -> hit.getEntry().getUrl()).toList();
    }

    @Test
    void encuentraPorTitulo() {
        List<SearchHit> hits = searchService.search("gateway", null);

        assertThat(hits).isNotEmpty();
        assertThat(urls(hits)).contains("/wiki/historias-usuario/hu-14");
    }

    @Test
    void encuentraPorContenidoAunqueElTituloNoCoincida() {
        List<SearchHit> hits = searchService.search("NIT", null);

        // "NIT" solo aparece en el cuerpo de HU-01, no en su titulo.
        assertThat(urls(hits)).contains("/wiki/historias-usuario/hu-01");
    }

    @Test
    void ignoraTildesEnAmbosSentidos() {
        List<String> sinTilde = urls(searchService.search("autenticacion", null));
        List<String> conTilde = urls(searchService.search("autenticación", null));

        assertThat(sinTilde).isNotEmpty();
        assertThat(sinTilde).isEqualTo(conTilde);
    }

    @Test
    void ignoraMayusculas() {
        assertThat(urls(searchService.search("PROCESO", null)))
            .isEqualTo(urls(searchService.search("proceso", null)));
    }

    @Test
    void filtraPorCategoria() {
        List<String> todas = urls(searchService.search("crear", null));
        List<String> soloRoles = urls(searchService.search("crear", "roles"));

        assertThat(soloRoles).isNotEmpty();
        assertThat(soloRoles).hasSizeLessThan(todas.size());
        assertThat(soloRoles).allSatisfy(url -> assertThat(todas).contains(url));
        assertThat(soloRoles).contains("/wiki/historias-usuario/hu-17");
    }

    @Test
    void priorizaLasCoincidenciasEnElTitulo() {
        List<SearchHit> hits = searchService.search("gateway", null);

        // La entrada cuyo titulo es "Gateways" debe ir por delante de las que
        // solo mencionan la palabra en el cuerpo.
        assertThat(hits.get(0).getEntry().getTitle()).isEqualTo("Gateways");
    }

    @Test
    void resaltaLosTerminosEncontrados() {
        List<SearchHit> hits = searchService.search("gateway", null);

        assertThat(hits.get(0).getHighlightedTitle()).contains("<mark>");
        assertThat(hits.get(0).getSnippet()).contains("<mark>");
    }

    @Test
    void escapaElHtmlDeLaConsultaAlResaltar() {
        // La consulta llega de la persona usuaria: no debe poder inyectar HTML.
        List<SearchHit> hits = searchService.search("<script>proceso", null);

        assertThat(hits).isNotEmpty();
        assertThat(hits).allSatisfy(hit -> {
            assertThat(hit.getSnippet()).doesNotContain("<script>");
            assertThat(hit.getHighlightedTitle()).doesNotContain("<script>");
        });
    }

    @Test
    void devuelveVacioSiLaConsultaEstaVaciaOEsDemasiadoCorta() {
        assertThat(searchService.search("", null)).isEmpty();
        assertThat(searchService.search("   ", null)).isEmpty();
        assertThat(searchService.search(null, null)).isEmpty();
        assertThat(searchService.search("a", null)).isEmpty();
    }

    @Test
    void devuelveVacioSiNoHayCoincidencias() {
        assertThat(searchService.search("zzzzquenoexiste", null)).isEmpty();
    }

    @Test
    void limitaElNumeroDeSugerencias() {
        assertThat(searchService.suggest("proceso", null)).hasSizeLessThanOrEqualTo(8);
    }
}
