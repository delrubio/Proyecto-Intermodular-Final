package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RolUsuario;
import com.example.demo.model.InscripcionPrueba;
import com.example.demo.model.InscripcionPruebaId;
import com.example.demo.model.Piloto;
import com.example.demo.model.Prueba;
import com.example.demo.model.Usuario;
import com.example.demo.model.Vehiculo;
import com.example.demo.repository.InscripcionPruebaRepository;
import com.example.demo.repository.PruebaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.VehiculoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InscripcionService {

    @Autowired InscripcionPruebaRepository inscripcionRepo;
    @Autowired VehiculoRepository vehiculoRepo;
    @Autowired PruebaRepository pruebaRepo;
    @Autowired UsuarioRepository usuarioRepo;

    public List<InscripcionPrueba> findAll() {
        Usuario usuario = obtenerUsuarioAutenticado();
        if (usuario.getRol() == RolUsuario.PILOTO) {
            Piloto piloto = (Piloto) usuario;
            log.info("InscripcionService - Inscripciones del piloto: {}", piloto.getNombre());
            return piloto.getVehiculos().stream()
                    .flatMap(v -> inscripcionRepo.findByIdMatricula(v.getMatricula()).stream())
                    .toList();
        }
        log.info("InscripcionService - Obteniendo todas las inscripciones");
        return inscripcionRepo.findAll();
    }

    public InscripcionPrueba findById(InscripcionPruebaId id) {
        return inscripcionRepo.findById(id).orElse(null);
    }

    @Transactional
    public InscripcionPrueba save(String matricula, Integer pruebaId) {
        Vehiculo vehiculo = vehiculoRepo.findById(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
        Prueba prueba = pruebaRepo.findById(pruebaId)
                .orElseThrow(() -> new IllegalArgumentException("Prueba no encontrada"));

        InscripcionPrueba inscripcion = new InscripcionPrueba();
        inscripcion.setVehiculo(vehiculo);
        inscripcion.setPrueba(prueba);
        inscripcion.getId().setMatricula(matricula);
        inscripcion.getId().setIdPrueba(pruebaId);

        log.info("InscripcionService - Inscribiendo vehículo {} en prueba {}", matricula, prueba.getNombre());
        InscripcionPrueba saved = inscripcionRepo.save(inscripcion);
        pruebaRepo.incrementarInscritos(pruebaId);
        return saved;
    }

    @Transactional
    public void deleteById(InscripcionPruebaId id) {
        InscripcionPrueba inscripcion = inscripcionRepo.findById(id).orElse(null);
        if (inscripcion == null) return;
        Integer pruebaId = inscripcion.getPrueba().getIdPrueba();
        log.info("InscripcionService - Eliminando inscripción {}", id);
        inscripcionRepo.deleteById(id);
        pruebaRepo.decrementarInscritos(pruebaId);
    }

    public List<Vehiculo> getAllVehiculos() {
        Usuario usuario = obtenerUsuarioAutenticado();
        if (usuario.getRol() == RolUsuario.PILOTO) {
            return vehiculoRepo.findByPilotoLicencia(usuario.getLicencia());
        }
        return vehiculoRepo.findAll();
    }

    public List<Prueba> getAllPruebas() {
        return pruebaRepo.findAll();
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new IllegalStateException("No hay usuario autenticado");
        Usuario usuario = usuarioRepo.findByNombre(auth.getName());
        if (usuario == null) throw new IllegalStateException("Usuario no encontrado en BD");
        return usuario;
    }

    // Métodos de compatibilidad para el controlador
    public List<LocalDate> getFechasDisponibles() { return List.of(); }
}
