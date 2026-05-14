package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "pruebas")
@Getter
@Setter
@NoArgsConstructor
public class Prueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prueba")
    private Integer idPrueba;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "localidad", length = 100)
    private String localidad;

    @Column(name = "campeonato", length = 100)
    private String campeonato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_licencia", nullable = false,
                foreignKey = @ForeignKey(name = "fk_prueba_organizador"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Organizador organizador;

    @Column(name = "n_inscritos", nullable = false)
    private Integer nInscritos = 0;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "prueba", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AsistenciaPrueba> asistentes = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "prueba", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InscripcionPrueba> inscripciones = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "prueba", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VerificacionTecnica> verificaciones = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "prueba", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Informe> informes = new ArrayList<>();
}
