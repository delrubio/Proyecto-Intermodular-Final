package com.example.demo.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Administrador;
import com.example.demo.model.Observador;
import com.example.demo.model.Organizador;
import com.example.demo.model.Piloto;
import com.example.demo.model.Tecnico;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> findAll() {
        log.info("UsuarioService - Obteniendo listado de todos los usuarios");
        return usuarioRepository.findAll();
    }

    public Usuario findById(String licencia) {
        log.info("UsuarioService - Buscando usuario con licencia: {}", licencia);
        return usuarioRepository.findById(licencia).orElse(null);
    }

    public List<Usuario> findByRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Transactional
    public Usuario crear(String licencia, String nombre, String apellidos, String email,
                         String fechaNacimientoStr, String telefono, String localidad,
                         RolUsuario rol, String rawPassword,
                         String federacion, Boolean presidenteFacv, Byte experiencia,
                         String club, Integer carrerasGanadas,
                         Byte nivelTecnico, String descripcion) {

        Usuario usuario = construirSubtipo(rol);
        usuario.setLicencia(licencia);
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setLocalidad(localidad);
        usuario.setPassword(passwordEncoder.encode(rawPassword));

        if (fechaNacimientoStr != null && !fechaNacimientoStr.isBlank()) {
            usuario.setFechaNacimiento(java.time.LocalDate.parse(fechaNacimientoStr));
        }

        rellenarCamposEspecificos(usuario, rol, federacion, presidenteFacv, experiencia,
                club, carrerasGanadas, nivelTecnico, descripcion);

        log.info("UsuarioService - Creando usuario: {} con rol {}", nombre, rol);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(String licencia, String nombre, String apellidos, String email,
                              String fechaNacimientoStr, String telefono, String localidad,
                              String rawPassword,
                              String federacion, Boolean presidenteFacv, Byte experiencia,
                              String club, Integer carrerasGanadas,
                              Byte nivelTecnico, String descripcion) {

        Usuario existente = usuarioRepository.findById(licencia).orElse(null);
        if (existente == null) {
            log.warn("UsuarioService - Usuario con licencia {} no encontrado", licencia);
            return null;
        }

        existente.setNombre(nombre);
        existente.setApellidos(apellidos);
        existente.setEmail(email);
        existente.setTelefono(telefono);
        existente.setLocalidad(localidad);

        if (fechaNacimientoStr != null && !fechaNacimientoStr.isBlank()) {
            existente.setFechaNacimiento(java.time.LocalDate.parse(fechaNacimientoStr));
        }
        if (rawPassword != null && !rawPassword.isBlank()) {
            existente.setPassword(passwordEncoder.encode(rawPassword));
        }

        rellenarCamposEspecificos(existente, existente.getRol(), federacion, presidenteFacv, experiencia,
                club, carrerasGanadas, nivelTecnico, descripcion);

        log.info("UsuarioService - Actualizando usuario con licencia: {}", licencia);
        return usuarioRepository.save(existente);
    }

    public void deleteById(String licencia) {
        log.info("UsuarioService - Eliminando usuario con licencia: {}", licencia);
        usuarioRepository.deleteById(licencia);
    }

    public List<Usuario> searchByName(String nameText) {
        if (nameText == null || nameText.isBlank()) return findAll();
        String q = nameText.toLowerCase();
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getNombre().toLowerCase().contains(q)
                          || u.getApellidos().toLowerCase().contains(q))
                .toList();
    }

    private Usuario construirSubtipo(RolUsuario rol) {
        return switch (rol) {
            case OBSERVADOR    -> new Observador();
            case ADMINISTRADOR -> new Administrador();
            case ORGANIZADOR   -> new Organizador();
            case PILOTO        -> new Piloto();
            case TECNICO       -> new Tecnico();
        };
    }

    private void rellenarCamposEspecificos(Usuario u, RolUsuario rol,
                                            String federacion, Boolean presidenteFacv, Byte experiencia,
                                            String club, Integer carrerasGanadas,
                                            Byte nivelTecnico, String descripcion) {
        switch (rol) {
            case OBSERVADOR -> ((Observador) u).setFederacion(federacion != null ? federacion : "");
            case ADMINISTRADOR -> {
                ((Administrador) u).setPresidenteFacv(presidenteFacv != null && presidenteFacv);
                ((Administrador) u).setExperiencia(experiencia != null ? experiencia : 0);
            }
            case ORGANIZADOR -> ((Organizador) u).setClub(club != null ? club : "");
            case PILOTO -> {
                ((Piloto) u).setClub(club);
                ((Piloto) u).setCarrerasGanadas(carrerasGanadas != null ? carrerasGanadas : 0);
            }
            case TECNICO -> {
                ((Tecnico) u).setNivelTecnico(nivelTecnico != null ? nivelTecnico : 1);
                ((Tecnico) u).setDescripcion(descripcion != null ? descripcion : "");
            }
        }
    }
}
