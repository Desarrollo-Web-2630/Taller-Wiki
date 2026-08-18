package com.taller1.thymeleaf.controller;

import com.taller1.thymeleaf.model.ContactoDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactoController {

    @GetMapping("/contacto")
    public String mostrarFormularioContacto(Model model) {
        model.addAttribute("contacto", new ContactoDTO());
        return "formulario_contacto";
    }

    @PostMapping("/contacto")
    public String enviarFormularioContacto(@ModelAttribute ContactoDTO contacto, Model model) {
        model.addAttribute("contacto", contacto);
        model.addAttribute("formEnviado", true);
        model.addAttribute("successText", "¡El formulario fue recibido correctamente y está listo para su envío futuro!");
        return "formulario_contacto";
    }
}
