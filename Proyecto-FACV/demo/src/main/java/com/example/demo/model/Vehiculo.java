package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad que representa un vehículo de competición registrado en el sistema.
 * <p>
 * La matrícula actúa como clave primaria ({@code @Id} sin generación automática).
 * Cada vehículo pertenece a exactamente un {@link Piloto}. Las colecciones
 * {@code inscripciones}, {@code verificaciones} e {@code incidencias} usan
 * {@code CascadeType.ALL}: eliminar un vehículo elimina todos sus registros dependientes.
 * </p>
 */
@Entity
@Table(name = "vehiculo")
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

    @Id
    @Column(name = "matricula", length = 15)
    private String matricula;

    @NotBlank(message = "La marca es obligatoria")
    @Column(name = "marca", length = 50, nullable = false)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Column(name = "modelo", length = 80, nullable = false)
    private String modelo;

    @Column(name = "categoria", length = 50)
    private String categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piloto_licencia", nullable = false, foreignKey = @ForeignKey(name = "fk_vehiculo_piloto"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Piloto piloto;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InscripcionPrueba> inscripciones = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VerificacionTecnica> verificaciones = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Incidencia> incidencias = new ArrayList<>();
}
