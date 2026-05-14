package com.example.demo.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "asistencia_prueba")
@Getter
@Setter
@NoArgsConstructor
public class AsistenciaPrueba {

    @EmbeddedId
    private AsistenciaPruebaId id = new AsistenciaPruebaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioLicencia")
    @JoinColumn(name = "usuario_licencia", foreignKey = @ForeignKey(name = "fk_asistencia_usuario"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPrueba")
    @JoinColumn(name = "id_prueba", foreignKey = @ForeignKey(name = "fk_asistencia_prueba"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Prueba prueba;

    @NotNull(message = "La fecha de inscripción es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;
}
