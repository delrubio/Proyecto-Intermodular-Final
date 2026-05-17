package com.example.demo.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.enums.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad que representa una incidencia técnica vinculada a una {@link VerificacionTecnica}.
 * <p>
 * Se crea automáticamente cuando una verificación da resultado {@code NO_APTO}.
 * Al marcarse como {@code RESUELTA}, {@link com.example.demo.service.IncidenciaService}
 * cambia su estado a {@code OCULTA} y actualiza la verificación asociada a {@code APTO}.
 * </p>
 */
@Entity
@Table(name = "incidencia")
@Getter
@Setter
@NoArgsConstructor
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "La verificación es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_verificacion", nullable = false, foreignKey = @ForeignKey(name = "fk_incidencia_verificacion"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private VerificacionTecnica verificacion;

    @NotNull(message = "El vehículo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula", nullable = false, foreignKey = @ForeignKey(name = "fk_incidencia_vehiculo"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Vehiculo vehiculo;

    @NotNull(message = "El técnico es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico1_licencia", nullable = false, foreignKey = @ForeignKey(name = "fk_incidencia_tecnico"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Tecnico tecnico1;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(name = "descripcion_incidencia", columnDefinition = "TEXT", nullable = false)
    private String descripcionIncidencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 12, nullable = false)
    private Estado estado = Estado.ABIERTA;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
}
