package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form DTO para la creación y edición de un {@link com.example.demo.model.Informe}.
 * <p>
 * {@code @DateTimeFormat} convierte la cadena ISO de fecha del formulario HTML a
 * {@link java.time.LocalDate} antes de que Spring MVC llame al controlador.
 * </p>
 */
@Data
@NoArgsConstructor
public class InformeForm {
    private Integer pruebaId;
    private String contenido;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;
    private BigDecimal puntuacionFinal;
}
