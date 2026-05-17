package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Tecnico;

/**
 * Repositorio JPA para la subentidad {@link Tecnico}.
 */
public interface TecnicoRepository extends JpaRepository<Tecnico, String> {

    /** Busca un técnico por su nombre de usuario. */
    Tecnico findByNombre(String nombre);
}
