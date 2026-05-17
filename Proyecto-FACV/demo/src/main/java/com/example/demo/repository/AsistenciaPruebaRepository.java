package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.AsistenciaPrueba;
import com.example.demo.model.AsistenciaPruebaId;

/**
 * Repositorio JPA para la entidad {@link AsistenciaPrueba}.
 * <p>
 * La clave primaria es compuesta ({@link AsistenciaPruebaId}): usuarioLicencia + idPrueba.
 * </p>
 */
public interface AsistenciaPruebaRepository extends JpaRepository<AsistenciaPrueba, AsistenciaPruebaId> {

    /** Devuelve todos los registros de asistencia de una prueba. */
    List<AsistenciaPrueba> findByIdIdPrueba(Integer idPrueba);

    /** Devuelve todos los registros de asistencia de un usuario por su licencia. */
    List<AsistenciaPrueba> findByIdUsuarioLicencia(String usuarioLicencia);
}
