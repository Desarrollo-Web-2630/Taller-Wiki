package com.taller1.thymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WikiController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }
}