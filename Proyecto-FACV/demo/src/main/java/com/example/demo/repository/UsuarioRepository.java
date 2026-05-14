package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Usuario findByNombre(String nombre);

    Usuario findByEmail(String email);

    List<Usuario> findByRol(RolUsuario rol);
}
