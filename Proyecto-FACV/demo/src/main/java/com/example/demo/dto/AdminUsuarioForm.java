package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form DTO para la creación y edición de usuarios desde el panel de administración.
 * <p>
 * Agrupa todos los campos que puede necesitar cualquier subtipo de {@link com.example.demo.model.Usuario}.
 * Los campos específicos de cada rol ({@code federacion}, {@code club}, etc.) son opcionales
 * y se aplican en {@link com.example.demo.service.UsuarioService} según el rol seleccionado.
 * La contraseña se recibe en claro ({@code rawPassword}) y se codifica con BCrypt en el servicio.
 * </p>
 */
@Data
@NoArgsConstructor
public class AdminUsuarioForm {
    private String licencia;
    private String nombre;
    private String apellidos;
    private String email;
    private String fechaNacimiento;
    private String telefono;
    private String localidad;
    private String rol;
    private String rawPassword;
    // Campos específicos por rol
    private String federacion;
    private Boolean presidenteFacv;
    private Byte experiencia;
    private String club;
    private Integer carrerasGanadas;
    private Byte nivelTecnico;
    private String descripcion;
}
