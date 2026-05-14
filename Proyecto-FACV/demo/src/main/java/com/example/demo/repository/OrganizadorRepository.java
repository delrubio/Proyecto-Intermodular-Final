package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Organizador;

public interface OrganizadorRepository extends JpaRepository<Organizador, String> {
    Organizador findByNombre(String nombre);
}
