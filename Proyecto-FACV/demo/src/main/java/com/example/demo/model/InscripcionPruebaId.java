package com.example.demo.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Clave primaria compuesta para {@link InscripcionPrueba}.
 * <p>
 * JPA exige que las claves embebidas implementen {@link java.io.Serializable}.
 * Se definen {@code equals} y {@code hashCode} manualmente sobre los campos
 * {@code matricula} e {@code idPrueba} para garantizar la identidad correcta
 * en colecciones y caché de segundo nivel.
 * </p>
 */
@Embeddable
public class InscripcionPruebaId implements Serializable {

    @Column(name = "matricula")
    private String matricula;

    @Column(name = "id_prueba")
    private Integer idPrueba;

    public InscripcionPruebaId() {}

    public InscripcionPruebaId(String matricula, Integer idPrueba) {
        this.matricula = matricula;
        this.idPrueba = idPrueba;
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public Integer getIdPrueba() { return idPrueba; }
    public void setIdPrueba(Integer idPrueba) { this.idPrueba = idPrueba; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InscripcionPruebaId)) return false;
        InscripcionPruebaId that = (InscripcionPruebaId) o;
        return Objects.equals(matricula, that.matricula) && Objects.equals(idPrueba, that.idPrueba);
    }

    @Override
    public int hashCode() { return Objects.hash(matricula, idPrueba); }
}
