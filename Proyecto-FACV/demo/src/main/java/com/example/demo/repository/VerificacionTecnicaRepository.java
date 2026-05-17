package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Vehiculo;
import com.example.demo.model.VerificacionTecnica;

/**
 * Repositorio JPA para la entidad {@link VerificacionTecnica}.
 * <p>
 * Incluye consultas JPQL personalizadas para localizar vehículos pendientes de
 * verificación en una prueba y para actualizar el resultado de una verificación
 * cuando su incidencia se resuelve.
 * </p>
 */
public interface VerificacionTecnicaRepository extends JpaRepository<VerificacionTecnica, Integer> {

    /** Devuelve todas las verificaciones de una prueba. */
    List<VerificacionTecnica> findByPrueba_IdPrueba(Integer idPrueba);

    /** Devuelve todas las verificaciones asociadas a un vehículo por matrícula. */
    List<VerificacionTecnica> findByVehiculo_Matricula(String matricula);

    /**
     * Devuelve los vehículos inscritos en una prueba que todavía no han sido verificados.
     * Se usa para mostrar la lista de pendientes al técnico.
     *
     * @param pruebaId ID de la prueba
     */
    @Query("SELECT ip.vehiculo FROM InscripcionPrueba ip WHERE ip.prueba.idPrueba = :pruebaId AND NOT EXISTS ( SELECT v FROM VerificacionTecnica v WHERE v.vehiculo = ip.vehiculo AND v.prueba.idPrueba = :pruebaId)")
    List<Vehiculo> findVehiculosPendientesPorPrueba(@Param("pruebaId") Integer pruebaId);

    /**
     * Cambia el resultado de una verificación de {@code NO_APTO} a {@code APTO}.
     * Se invoca cuando la incidencia asociada se marca como {@code RESUELTA}.
     *
     * @param id ID de la verificación técnica
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE verificacion_tecnica SET resultado = 'APTO' WHERE id = :id AND resultado = 'NO_APTO'", nativeQuery = true)
    void cambiarResultadoVerificacionIncidencia(@Param("id") Integer id);
}
