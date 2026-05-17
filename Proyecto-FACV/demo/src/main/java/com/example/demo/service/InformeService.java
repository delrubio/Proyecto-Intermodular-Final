package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Informe;
import com.example.demo.model.Observador;
import com.example.demo.model.Prueba;
import com.example.demo.model.Usuario;
import com.example.demo.repository.InformeRepository;
import com.example.demo.repository.PruebaRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de negocio para la gestión de informes de observación.
 * <p>
 * Al crear un informe, el observador se obtiene del contexto de seguridad si el usuario
 * autenticado tiene rol {@code OBSERVADOR}. El administrador no puede crear informes
 * directamente (lanza excepción).
 * </p>
 */
@Slf4j
@Service
public class InformeService {

    @Autowired InformeRepository informeRepository;
    @Autowired PruebaRepository pruebaRepository;
    @Autowired UsuarioRepository usuarioRepository;

    public List<Informe> findAll() {
        log.info("InformeService - Obteniendo todos los informes");
        return informeRepository.findAll();
    }

    public Informe findById(Integer id) {
        return informeRepository.findById(id).orElse(null);
    }

    public Informe save(Integer pruebaId, String contenido, LocalDate fecha, BigDecimal puntuacion) {
        Observador observador = obtenerObservadorAutenticado();
        Prueba prueba = pruebaRepository.findById(pruebaId).orElseThrow(() -> new IllegalArgumentException("Prueba no encontrada"));

        Informe informe = new Informe();
        informe.setObservador(observador);
        informe.setPrueba(prueba);
        informe.setContenido(contenido);
        informe.setFecha(fecha);
        informe.setPuntuacionFinal(puntuacion != null ? puntuacion : BigDecimal.ZERO);

        log.info("InformeService - Guardando informe para prueba: {}", prueba.getNombre());
        return informeRepository.save(informe);
    }

    public Informe update(Integer id, String contenido, LocalDate fecha, BigDecimal puntuacion) {
        Informe existente = informeRepository.findById(id).orElse(null);
        if (existente == null) {
            log.warn("InformeService - Informe {} no encontrado", id);
            return null;
        }
        existente.setContenido(contenido);
        existente.setFecha(fecha);
        existente.setPuntuacionFinal(puntuacion != null ? puntuacion : BigDecimal.ZERO);
        log.info("InformeService - Actualizando informe ID: {}", id);
        return informeRepository.save(existente);
    }

    public void deleteById(Integer id) {
        log.info("InformeService - Eliminando informe ID: {}", id);
        informeRepository.deleteById(id);
    }

    public List<Prueba> getAllPruebas() {
        return pruebaRepository.findAll();
    }

    private Observador obtenerObservadorAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("No hay usuario autenticado");
        Usuario usuario = usuarioRepository.findByNombre(auth.getName());
        if (usuario == null) throw new IllegalStateException("Usuario no encontrado");
        if (usuario.getRol() == RolUsuario.OBSERVADOR) return (Observador) usuario;
        throw new IllegalStateException("El usuario autenticado no es un observador");
    }
}
