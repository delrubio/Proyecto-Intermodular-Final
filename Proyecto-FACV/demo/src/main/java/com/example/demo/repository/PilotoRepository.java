package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Piloto;

public interface PilotoRepository extends JpaRepository<Piloto, String> {
    Piloto findByNombre(String nombre);
}
