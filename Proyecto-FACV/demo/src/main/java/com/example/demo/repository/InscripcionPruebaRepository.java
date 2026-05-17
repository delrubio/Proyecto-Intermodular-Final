package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.InscripcionPrueba;
import com.example.demo.model.InscripcionPruebaId;
import com.example.demo.model.Vehiculo;

/**
 * Repositorio JPA para la entidad {@link InscripcionPrueba}.
 * <p>
 * La clave primaria es compuesta ({@link InscripcionPruebaId}): matrícula + idPrueba.
 * </p>
 */
public interface InscripcionPruebaRepository extends JpaRepository<InscripcionPrueba, InscripcionPruebaId> {

    /** Devuelve todas las inscripciones de una prueba por su ID. */
    List<InscripcionPrueba> findByIdIdPrueba(Integer idPrueba);

    /** Devuelve todas las inscripciones de un vehículo por matrícula. */
    List<InscripcionPrueba> findByIdMatricula(String matricula);

    /**
     * Devuelve los vehículos inscritos en una prueba (proyección desde {@link InscripcionPrueba}).
     *
     * @param pruebaId ID de la prueba
     */
    @Query("SELECT ip.vehiculo FROM InscripcionPrueba ip WHERE ip.prueba.idPrueba = :pruebaId")
    List<Vehiculo> findVehiculosByPruebaId(@Param("pruebaId") Integer pruebaId);
}
