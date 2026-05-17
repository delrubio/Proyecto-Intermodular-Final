package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Usuario;

/**
 * Repositorio JPA para la entidad {@link Usuario} (y sus subclases).
 * <p>
 * Extiende {@link JpaRepository} con clave {@code String} (la licencia).
 * Spring Data genera la implementación en tiempo de arranque.
 * </p>
 */
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    /** Busca un usuario por nombre de usuario (username en Spring Security). */
    Usuario findByNombre(String nombre);

    /** Busca un usuario por dirección de correo electrónico. */
    Usuario findByEmail(String email);

    /** Filtra los usuarios que tienen exactamente el rol indicado. */
    List<Usuario> findByRol(RolUsuario rol);
}
