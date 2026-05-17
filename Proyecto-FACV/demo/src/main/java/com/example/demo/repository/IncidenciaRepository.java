package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.enums.Estado;
import com.example.demo.model.Incidencia;

/**
 * Repositorio JPA para la entidad {@link Incidencia}.
 */
public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {

    /** Devuelve todas las incidencias de un vehículo por matrícula. */
    List<Incidencia> findByVehiculo_Matricula(String matricula);

    /** Devuelve todas las incidencias con el estado indicado. */
    List<Incidencia> findByEstado(Estado estado);

    /** Devuelve todas las incidencias cuyo estado NO es el indicado (p.ej. excluir {@code OCULTA}). */
    List<Incidencia> findByEstadoNot(Estado estado);
}
