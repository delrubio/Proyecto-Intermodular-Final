package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "informe")
@Getter
@Setter
@NoArgsConstructor
public class Informe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "El observador es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licencia_observador", nullable = false,
                foreignKey = @ForeignKey(name = "fk_informe_observador"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Observador observador;

    @NotNull(message = "La prueba es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prueba", nullable = false,
                foreignKey = @ForeignKey(name = "fk_informe_prueba"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Prueba prueba;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "puntuacion_final", precision = 2, scale = 1)
    private BigDecimal puntuacionFinal = BigDecimal.ZERO;

    @Column(name = "contenido", columnDefinition = "TEXT")
    private String contenido;
}
