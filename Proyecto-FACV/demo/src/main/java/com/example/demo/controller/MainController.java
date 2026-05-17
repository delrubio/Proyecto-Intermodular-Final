package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador que gestiona las rutas de entrada principales de la aplicación.
 * <p>
 * Mapea la página de inicio ({@code /} y {@code /inicio}) y la página de login ({@code /login}).
 * El procesamiento del formulario de login y el logout los gestiona Spring Security
 * directamente (configurado en {@link com.example.demo.security.SecurityConfig}).
 * </p>
 */
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
