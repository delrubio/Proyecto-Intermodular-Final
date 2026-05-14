package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Tecnico;

public interface TecnicoRepository extends JpaRepository<Tecnico, String> {
    Tecnico findByNombre(String nombre);
}
