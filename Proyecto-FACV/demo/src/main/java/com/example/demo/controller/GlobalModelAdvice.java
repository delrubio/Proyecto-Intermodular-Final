package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.dto.UsuarioDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired UsuarioRepository usuarioRepository;

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
    public UsuarioDTO currentUsuario(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        Usuario usuario = usuarioRepository.findByNombre(authentication.getName());
        return usuario != null ? UsuarioDTO.from(usuario) : null;
    }
}
