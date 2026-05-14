package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Vehiculo;
import com.example.demo.model.VerificacionTecnica;

public interface VerificacionTecnicaRepository extends JpaRepository<VerificacionTecnica, Integer> {

    List<VerificacionTecnica> findByPrueba_IdPrueba(Integer idPrueba);

    List<VerificacionTecnica> findByVehiculo_Matricula(String matricula);

    @Query("""
        SELECT ip.vehiculo FROM InscripcionPrueba ip
        WHERE ip.prueba.idPrueba = :pruebaId
        AND NOT EXISTS (
            SELECT v FROM VerificacionTecnica v
            WHERE v.vehiculo = ip.vehiculo
            AND v.prueba.idPrueba = :pruebaId
        )
        """)
    List<Vehiculo> findVehiculosPendientesPorPrueba(@Param("pruebaId") Integer pruebaId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE verificacion_tecnica SET resultado = 'APTO' WHERE id = :id AND resultado = 'NO_APTO'",
           nativeQuery = true)
    void cambiarResultadoVerificacionIncidencia(@Param("id") Integer id);
}
