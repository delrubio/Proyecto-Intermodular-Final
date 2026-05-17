package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Organizador;

/**
 * Repositorio JPA para la subentidad {@link Organizador}.
 */
public interface OrganizadorRepository extends JpaRepository<Organizador, String> {

    /** Busca un organizador por su nombre de usuario. */
    Organizador findByNombre(String nombre);
}
