package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form DTO para crear o eliminar una {@link com.example.demo.model.InscripcionPrueba}.
 * <p>
 * Los dos campos ({@code matricula} e {@code pruebaId}) forman la clave primaria compuesta
 * {@link com.example.demo.model.InscripcionPruebaId} que el servicio construye internamente.
 * </p>
 */
@Data
@NoArgsConstructor
public class InscripcionForm {
    private String matricula;
    private Integer pruebaId;
}
