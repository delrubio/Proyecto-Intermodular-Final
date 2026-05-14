package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Informe;

public interface InformeRepository extends JpaRepository<Informe, Integer> {
    List<Informe> findByObservador_Licencia(String licencia);
    List<Informe> findByPrueba_IdPrueba(Integer idPrueba);
}
