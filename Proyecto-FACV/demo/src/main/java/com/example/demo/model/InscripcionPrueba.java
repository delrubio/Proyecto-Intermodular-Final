package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "inscripcion_prueba")
@Getter
@Setter
@NoArgsConstructor
public class InscripcionPrueba {

    @EmbeddedId
    private InscripcionPruebaId id = new InscripcionPruebaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("matricula")
    @JoinColumn(name = "matricula", foreignKey = @ForeignKey(name = "fk_inscripcion_vehiculo"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPrueba")
    @JoinColumn(name = "id_prueba", foreignKey = @ForeignKey(name = "fk_inscripcion_prueba"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Prueba prueba;

    @Column(name = "apto", nullable = false)
    private Boolean apto = false;

    @Column(name = "verificado", nullable = false)
    private Boolean verificado = false;

    @Column(name = "ganador", nullable = false)
    private Boolean ganador = false;
}
