package com.example.demo.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.Piloto;
import com.example.demo.model.Usuario;
import com.example.demo.model.Vehiculo;
import com.example.demo.repository.PilotoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.VehiculoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PilotoRepository pilotoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository,
                           UsuarioRepository usuarioRepository,
                           PilotoRepository pilotoRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pilotoRepository = pilotoRepository;
    }

    public List<Vehiculo> findAll() {
        log.info("VehiculoService - Obteniendo listado de todos los vehículos");
        return vehiculoRepository.findAll();
    }

    public Vehiculo findById(String matricula) {
        log.info("VehiculoService - Buscando vehículo con matrícula: {}", matricula);
        return vehiculoRepository.findById(matricula).orElse(null);
    }

    public Vehiculo save(Vehiculo vehiculo, String pilotoLicencia) {
        Piloto piloto;
        if (pilotoLicencia != null && !pilotoLicencia.isBlank()) {
            piloto = pilotoRepository.findById(pilotoLicencia)
                    .orElseThrow(() -> new IllegalArgumentException("Piloto no encontrado"));
        } else {
            piloto = obtenerPilotoAutenticado();
        }
        vehiculo.setPiloto(piloto);
        log.info("VehiculoService - Guardando vehículo con matrícula: {}", vehiculo.getMatricula());
        return vehiculoRepository.save(vehiculo);
    }

    public Vehiculo update(String matricula, Vehiculo vehiculo) {
        Vehiculo existente = vehiculoRepository.findById(matricula).orElse(null);
        if (existente == null) {
            log.warn("VehiculoService - Vehículo con matrícula {} no encontrado", matricula);
            return null;
        }
        vehiculo.setMatricula(matricula);
        vehiculo.setPiloto(existente.getPiloto());
        log.info("VehiculoService - Actualizando vehículo con matrícula: {}", matricula);
        return vehiculoRepository.save(vehiculo);
    }

    public void deleteById(String matricula) {
        log.info("VehiculoService - Eliminando vehículo con matrícula: {}", matricula);
        vehiculoRepository.deleteById(matricula);
    }

    public List<Vehiculo> searchByMarca(String brandText) {
        if (brandText == null || brandText.isBlank()) return findAll();
        String q = brandText.toLowerCase();
        return vehiculoRepository.findAll().stream()
                .filter(v -> v.getMarca().toLowerCase().contains(q))
                .toList();
    }

    private Piloto obtenerPilotoAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        Usuario usuario = usuarioRepository.findByNombre(auth.getName());
        if (usuario == null) throw new IllegalStateException("Usuario no encontrado en BD");
        if (usuario.getRol() == RolUsuario.PILOTO) return (Piloto) usuario;
        // El admin también puede crear vehículos pero sin asignación de piloto propia
        throw new IllegalStateException("El usuario autenticado no es un piloto");
    }
}
