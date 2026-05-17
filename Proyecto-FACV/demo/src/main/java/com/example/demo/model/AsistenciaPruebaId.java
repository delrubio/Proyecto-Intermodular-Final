package com.example.demo.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Clave primaria compuesta para {@link AsistenciaPrueba}.
 * <p>
 * JPA exige que las claves embebidas implementen {@link java.io.Serializable}.
 * {@code equals} y {@code hashCode} se definen sobre {@code usuarioLicencia} e
 * {@code idPrueba} para garantizar identidad correcta.
 * </p>
 */
@Embeddable
public class AsistenciaPruebaId implements Serializable {

    @Column(name = "usuario_licencia")
    private String usuarioLicencia;

    @Column(name = "id_prueba")
    private Integer idPrueba;

    public AsistenciaPruebaId() {}

    public AsistenciaPruebaId(String usuarioLicencia, Integer idPrueba) {
        this.usuarioLicencia = usuarioLicencia;
        this.idPrueba = idPrueba;
    }

    public String getUsuarioLicencia() { return usuarioLicencia; }
    public void setUsuarioLicencia(String usuarioLicencia) { this.usuarioLicencia = usuarioLicencia; }
    public Integer getIdPrueba() { return idPrueba; }
    public void setIdPrueba(Integer idPrueba) { this.idPrueba = idPrueba; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AsistenciaPruebaId)) return false;
        AsistenciaPruebaId that = (AsistenciaPruebaId) o;
        return Objects.equals(usuarioLicencia, that.usuarioLicencia) && Objects.equals(idPrueba, that.idPrueba);
    }

    @Override
    public int hashCode() { return Objects.hash(usuarioLicencia, idPrueba); }
}
