package com.taller1.thymeleaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.taller1.thymeleaf.model.Contacto;
import com.taller1.thymeleaf.model.ContactoDTO;
import com.taller1.thymeleaf.model.WikiEntry;
import com.taller1.thymeleaf.service.ContactoService;
import com.taller1.thymeleaf.service.WikiEntryService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest
class ContactoPersistenceTest {

    @Autowired
    private ContactoService contactoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Validator validator;

    @Test
    void shouldRejectEmptyContactoDto() {
        ContactoDTO dto = new ContactoDTO();
        Set<ConstraintViolation<ContactoDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Un formulario vacío debe ser rechazado por validación");
    }

    @Test
    void shouldPersistContacto() {
        Contacto contacto = new Contacto();
        contacto.setNombre("Ana García");
        contacto.setCorreo("ana@correo.com");
        contacto.setTelefono("1234567");
        contacto.setAsunto("Consulta");
        contacto.setMensaje("Necesito ayuda con la aplicación y quiero dejar el registro en base de datos.");

        Contacto guardado = contactoService.guardar(contacto);

        assertNotNull(guardado.getId());
        assertEquals("Ana García", guardado.getNombre());
    }

    @Test
    void shouldPersistContactoFromDatabaseLayer() {
        String correo = "jdbc@correo.com";
        long before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM contactos", Long.class);

        Contacto contacto = new Contacto();
        contacto.setNombre("Luis Test");
        contacto.setCorreo(correo);
        contacto.setTelefono("12345678");
        contacto.setAsunto("documentacion");
        contacto.setMensaje("Necesito ayuda con la documentación del proyecto y dejo una prueba real de envío.");

        contactoService.guardar(contacto);

        long after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM contactos", Long.class);
        assertTrue(after > before, "Debe insertarse al menos un registro en contactos tras guardar");

        Map<String, Object> row = jdbcTemplate.queryForMap(
            "SELECT nombre, correo, telefono, asunto, mensaje FROM contactos WHERE correo = ?",
            correo
        );

        assertEquals("Luis Test", row.get("nombre"));
        assertEquals("documentacion", row.get("asunto"));
    }

    @Test
    void shouldGenerateAnchorIdsForHeadings() {
        WikiEntryService service = new WikiEntryService(null);
        String html = "<h2>Autenticación</h2><h3>HU-01 Registro de empresa</h3>";

        String withIds = service.addIdsToHeadings(html);

        assertTrue(withIds.contains("id=\"autenticacion\""));
        assertTrue(withIds.contains("id=\"hu-01-registro-de-empresa\""));
    }

    @Test
    void shouldLinkHuSectionsToTheirRealPages() {
        WikiEntryService service = new WikiEntryService(null);
        WikiEntry autenticacion = new WikiEntry();
        autenticacion.setTitle("Autenticacion");
        autenticacion.setUrl("/wiki/historias-usuario/autenticacion");
        autenticacion.setChildren(List.of());

        WikiEntry hu01 = new WikiEntry();
        hu01.setTitle("HU-01 Registro empresa");
        hu01.setUrl("/wiki/historias-usuario/hu-01");

        autenticacion.setChildren(List.of(hu01));

        String html = "<h2>Autenticación</h2><h3>HU-01 Registro de empresa</h3>";
        List<WikiEntryService.TocItem> toc = service.parseToc(html, autenticacion);

        assertEquals("/wiki/historias-usuario/hu-01", toc.get(1).getHref());
    }
}
