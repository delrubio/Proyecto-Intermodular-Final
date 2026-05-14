package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.enums.ResultadoVerificacion;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "verificacion_tecnica")
@Getter
@Setter
@NoArgsConstructor
public class VerificacionTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "El vehículo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula", nullable = false,
                foreignKey = @ForeignKey(name = "fk_verif_vehiculo"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Vehiculo vehiculo;

    @NotNull(message = "El técnico principal es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico1_licencia", nullable = false,
                foreignKey = @ForeignKey(name = "fk_verif_tecnico1"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Tecnico tecnico1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico2_licencia",
                foreignKey = @ForeignKey(name = "fk_verif_tecnico2"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Tecnico tecnico2;

    @NotNull(message = "La prueba es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prueba", nullable = false,
                foreignKey = @ForeignKey(name = "fk_verif_prueba"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Prueba prueba;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", length = 7, nullable = false)
    private ResultadoVerificacion resultado = ResultadoVerificacion.NO_APTO;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "verificacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Incidencia> incidencias = new ArrayList<>();
}
