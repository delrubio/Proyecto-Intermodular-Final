package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.dto.UsuarioDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

/**
 * Componente {@code @ControllerAdvice} que inyecta atributos comunes en el modelo
 * de todas las vistas Thymeleaf antes de renderizarlas.
 * <p>
 * Expone los siguientes atributos globales:
 * <ul>
 *   <li>{@code isAdmin} – {@code boolean}: indica si el usuario autenticado es administrador.</li>
 *   <li>{@code userRol} – {@code String}: nombre del rol sin el prefijo {@code ROLE_}.</li>
 *   <li>{@code usuarioNombre} – {@code String}: nombre de usuario autenticado.</li>
 *   <li>{@code currentUsuario} – {@link com.example.demo.dto.UsuarioDTO}: DTO público del usuario.</li>
 * </ul>
 * </p>
 */
@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired UsuarioRepository usuarioRepository;

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    @ModelAttribute("userRol")
    public String userRol(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return "";
        return authentication.getAuthorities().stream().filter(a -> a.getAuthority().startsWith("ROLE_")).map(a -> a.getAuthority().substring(5)).findFirst().orElse("");
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
