package com.example.demo.dto;

import com.example.demo.enums.Estado;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form DTO para la edición de una {@link com.example.demo.model.Incidencia}.
 * <p>
 * Contiene únicamente los campos editables: descripción y estado.
 * Si el estado recibido es {@link com.example.demo.enums.Estado#RESUELTA},
 * {@link com.example.demo.service.IncidenciaService} lo convierte a {@code OCULTA}
 * y actualiza la verificación asociada a {@code APTO}.
 * </p>
 */
@Data
@NoArgsConstructor
public class IncidenciaForm {
    private String descripcionIncidencia;
    private Estado estado;
}
