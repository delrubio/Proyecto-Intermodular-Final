package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Piloto;

/**
 * Repositorio JPA para la subentidad {@link Piloto}.
 * <p>
 * Hereda las operaciones CRUD de {@link JpaRepository}; la clave es la licencia ({@code String}).
 * </p>
 */
public interface PilotoRepository extends JpaRepository<Piloto, String> {

    /** Busca un piloto por su nombre de usuario. */
    Piloto findByNombre(String nombre);
}
