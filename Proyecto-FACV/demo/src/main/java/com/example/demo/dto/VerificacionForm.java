package com.example.demo.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

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
