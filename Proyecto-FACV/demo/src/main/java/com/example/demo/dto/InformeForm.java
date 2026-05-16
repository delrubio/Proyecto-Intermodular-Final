package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InformeForm {
    private Integer pruebaId;
    private String contenido;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;
    private BigDecimal puntuacionFinal;
}
