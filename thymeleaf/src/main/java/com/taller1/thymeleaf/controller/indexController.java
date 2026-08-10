package com.taller1.thymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexController {

    // http://localhost:8080/home
    @GetMapping("/")
    public String inicio() {
        return "index";
    }
}
