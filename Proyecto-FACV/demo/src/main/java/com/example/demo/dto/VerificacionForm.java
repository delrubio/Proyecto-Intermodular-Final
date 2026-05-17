package com.example.demo.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form DTO para la creación y edición de una {@link com.example.demo.model.VerificacionTecnica}.
 * <p>
 * Recoge los campos del formulario HTML y los pasa a
 * {@link com.example.demo.service.VerificacionService}. El campo {@code fromPruebaId}
 * permite redirigir al listado de pendientes de esa prueba tras guardar.
 * </p>
 */
@Data
@NoArgsConstructor
public class VerificacionForm {
    private String matricula;
    private Integer pruebaId;
    private String resultado;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;
    private String tecnico1Licencia;
    private String tecnico2Licencia;
    private Integer fromPruebaId;
}
