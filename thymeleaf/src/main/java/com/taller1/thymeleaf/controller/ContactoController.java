package com.taller1.thymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.taller1.thymeleaf.model.Contacto;
import com.taller1.thymeleaf.model.ContactoDTO;
import com.taller1.thymeleaf.service.ContactoService;
import com.taller1.thymeleaf.service.WikiEntryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class ContactoController {

    private final ContactoService contactoService;
    private final WikiEntryService wikiEntryService;

    public ContactoController(ContactoService contactoService, WikiEntryService wikiEntryService) {
        this.contactoService = contactoService;
        this.wikiEntryService = wikiEntryService;
    }

    @GetMapping("/contacto")
    public String mostrarFormularioContacto(Model model, HttpServletRequest request) {
        model.addAttribute("contacto", new ContactoDTO());
        model.addAttribute("navigation", wikiEntryService.getNavigationTree(request.getRequestURL().toString()));
        return "formulario_contacto";
    }

    @PostMapping("/contacto")
    public String enviarFormularioContacto(@Valid @ModelAttribute ContactoDTO contactoDto,
                                          BindingResult bindingResult,
                                          Model model,
                                          HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("contacto", contactoDto);
            model.addAttribute("navigation", wikiEntryService.getNavigationTree(request.getRequestURL().toString()));
            return "formulario_contacto";
        }

        Contacto contacto = new Contacto();
        contacto.setNombre(contactoDto.getNombre());
        contacto.setCorreo(contactoDto.getCorreo());
        contacto.setTelefono(contactoDto.getTelefono());
        contacto.setAsunto(contactoDto.getAsunto());
        contacto.setMensaje(contactoDto.getMensaje());

        contactoService.guardar(contacto);

        model.addAttribute("contacto", new ContactoDTO());
        model.addAttribute("formEnviado", true);
        model.addAttribute("successText", "¡El formulario fue recibido correctamente y guardado en la base de datos!");
        model.addAttribute("navigation", wikiEntryService.getNavigationTree(request.getRequestURL().toString()));
        return "formulario_contacto";
    }
}
