package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.enums.Estado;
import com.example.demo.model.Incidencia;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
    List<Incidencia> findByVehiculo_Matricula(String matricula);
    List<Incidencia> findByEstado(Estado estado);
    List<Incidencia> findByEstadoNot(Estado estado);
}
