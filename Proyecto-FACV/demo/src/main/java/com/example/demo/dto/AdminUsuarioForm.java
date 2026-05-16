package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

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
