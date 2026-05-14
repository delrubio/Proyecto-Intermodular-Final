package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Observador;

public interface ObservadorRepository extends JpaRepository<Observador, String> {
    Observador findByNombre(String nombre);
}
