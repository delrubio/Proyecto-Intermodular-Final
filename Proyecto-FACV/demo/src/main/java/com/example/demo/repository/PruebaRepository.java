package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Prueba;

public interface PruebaRepository extends JpaRepository<Prueba, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Prueba p SET p.nInscritos = COALESCE(p.nInscritos, 0) + 1 WHERE p.idPrueba = :id")
    void incrementarInscritos(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE Prueba p SET p.nInscritos = CASE WHEN COALESCE(p.nInscritos, 0) > 0 THEN COALESCE(p.nInscritos, 0) - 1 ELSE 0 END WHERE p.idPrueba = :id")
    void decrementarInscritos(@Param("id") Integer id);
}
