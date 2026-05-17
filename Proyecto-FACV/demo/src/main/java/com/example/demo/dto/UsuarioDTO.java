package com.example.demo.dto;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Usuario;

/**
 * DTO inmutable (Java record) que expone únicamente los datos públicos de un usuario.
 * <p>
 * Se utiliza en todos los contextos excepto en el panel de administración, donde
 * se trabaja directamente con la entidad {@link Usuario} y sus subclases.
 * Evita filtrar datos sensibles (contraseña, email, teléfono, fecha de nacimiento).
 * Se inyecta en el modelo de todas las vistas mediante
 * {@link com.example.demo.controller.GlobalModelAdvice} bajo la clave {@code currentUsuario}.
 * </p>
 *
 * @param licencia identificador único del usuario
 * @param nombre   nombre de usuario (también username en Spring Security)
 * @param rol      rol asignado al usuario
 */
public record UsuarioDTO(String licencia, String nombre, RolUsuario rol) {

    /**
     * Crea un {@code UsuarioDTO} a partir de una entidad {@link Usuario} completa.
     *
     * @param usuario entidad fuente
     * @return DTO con solo los tres campos públicos
     */
    public static UsuarioDTO from(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getLicencia(),
                usuario.getNombre(),
                usuario.getRol()
        );
    }
}
