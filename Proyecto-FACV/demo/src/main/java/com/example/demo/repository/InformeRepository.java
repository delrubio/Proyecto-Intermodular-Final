package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Informe;

/**
 * Repositorio JPA para la entidad {@link Informe}.
 */
public interface InformeRepository extends JpaRepository<Informe, Integer> {

    /** Devuelve los informes redactados por un observador (por su licencia). */
    List<Informe> findByObservador_Licencia(String licencia);

    /** Devuelve todos los informes asociados a una prueba. */
    List<Informe> findByPrueba_IdPrueba(Integer idPrueba);
}
