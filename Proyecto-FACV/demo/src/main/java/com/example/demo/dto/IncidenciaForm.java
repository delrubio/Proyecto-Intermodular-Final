package com.example.demo.dto;

import com.example.demo.enums.Estado;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IncidenciaForm {
    private String descripcionIncidencia;
    private Estado estado;
}
