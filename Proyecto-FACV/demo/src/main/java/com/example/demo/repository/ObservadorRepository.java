package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Observador;

/**
 * Repositorio JPA para la subentidad {@link Observador}.
 */
public interface ObservadorRepository extends JpaRepository<Observador, String> {

    /** Busca un observador por su nombre de usuario. */
    Observador findByNombre(String nombre);
}
