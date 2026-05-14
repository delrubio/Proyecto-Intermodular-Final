package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UsuarioRepository usuarioRepository;

    public GlobalModelAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    @ModelAttribute("userRol")
    public String userRol(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return "";
        return authentication.getAuthorities().stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .map(a -> a.getAuthority().substring(5))
                .findFirst().orElse("");
    }

    @ModelAttribute("usuarioNombre")
    public String usuarioNombre(Authentication authentication) {
        if (authentication == null) return "";
        return authentication.getName();
    }

    @ModelAttribute("currentUsuario")
    public Usuario currentUsuario(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        return usuarioRepository.findByNombre(authentication.getName());
    }
}
