package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainController {

    @GetMapping({"/", "/inicio"})
    public String indexView() {
        log.info("MainController - Página de inicio");
        return "index";
    }

    @GetMapping("/login")
    public String loginView() {
        log.info("MainController - Página de login");
        return "login";
    }
}
