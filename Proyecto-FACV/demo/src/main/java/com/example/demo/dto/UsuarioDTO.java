package com.example.demo.dto;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Usuario;

/**
 * DTO de usuario que expone únicamente los datos públicos necesarios.
 * Usar en cualquier contexto no administrativo para evitar filtrar datos sensibles
 * (contraseña, email, teléfono, fecha de nacimiento, etc.).
 */
public record UsuarioDTO(String licencia, String nombre, RolUsuario rol) {

    public static UsuarioDTO from(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getLicencia(),
                usuario.getNombre(),
                usuario.getRol()
        );
    }
}
