package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Organizador;
import com.example.demo.model.Prueba;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PruebaRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de negocio para la gestión de pruebas de rally.
 * <p>
 * Si el usuario autenticado es {@code ORGANIZADOR}, se asocia automáticamente como
 * organizador de la prueba al persistirla. Si es {@code ADMINISTRADOR}, el organizador
 * se pasa explícitamente desde el controlador.
 * </p>
 */
@Slf4j
@Service
public class PruebaService {

    @Autowired PruebaRepository pruebaRepository;
    @Autowired UsuarioRepository usuarioRepository;

    public List<Prueba> findAll() {
        log.info("PruebaService - Obteniendo listado de todas las pruebas");
        return pruebaRepository.findAll();
    }

    public Prueba findById(Integer id) {
        log.info("PruebaService - Buscando prueba con ID: {}", id);
        return pruebaRepository.findById(id).orElse(null);
    }

    public Prueba save(Prueba prueba) {
        if (prueba.getOrganizador() == null) {
            prueba.setOrganizador(obtenerOrganizadorAutenticado());
        }
        if (prueba.getNInscritos() == null) prueba.setNInscritos(0);
        log.info("PruebaService - Guardando prueba: {}", prueba.getNombre());
        return pruebaRepository.save(prueba);
    }

    public void deleteById(Integer id) {
        log.info("PruebaService - Eliminando prueba con ID: {}", id);
        pruebaRepository.deleteById(id);
    }

    public List<Prueba> searchByNombre(String texto) {
        if (texto == null || texto.isBlank()) return findAll();
        String q = texto.toLowerCase();
        return pruebaRepository.findAll().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(q))
                .toList();
    }

    private Organizador obtenerOrganizadorAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("No hay usuario autenticado");
        Usuario usuario = usuarioRepository.findByNombre(auth.getName());
        if (usuario == null) throw new IllegalStateException("Usuario no encontrado");
        if (usuario.getRol() == RolUsuario.ORGANIZADOR) return (Organizador) usuario;
        if (usuario.getRol() == RolUsuario.ADMINISTRADOR) return null;
        throw new IllegalStateException("El usuario no es organizador");
    }
}
