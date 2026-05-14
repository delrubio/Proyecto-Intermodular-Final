package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.Estado;
import com.example.demo.enums.ResultadoVerificacion;
import com.example.demo.model.Incidencia;
import com.example.demo.model.Prueba;
import com.example.demo.model.Tecnico;
import com.example.demo.model.Usuario;
import com.example.demo.model.Vehiculo;
import com.example.demo.model.VerificacionTecnica;
import com.example.demo.repository.IncidenciaRepository;
import com.example.demo.repository.InscripcionPruebaRepository;
import com.example.demo.repository.PruebaRepository;
import com.example.demo.repository.TecnicoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.VehiculoRepository;
import com.example.demo.repository.VerificacionTecnicaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VerificacionService {

    private final VerificacionTecnicaRepository verificacionRepo;
    private final VehiculoRepository vehiculoRepo;
    private final PruebaRepository pruebaRepo;
    private final UsuarioRepository usuarioRepo;
    private final TecnicoRepository tecnicoRepo;
    private final InscripcionPruebaRepository inscripcionRepo;
    private final IncidenciaRepository incidenciaRepo;

    public VerificacionService(VerificacionTecnicaRepository verificacionRepo,
                               VehiculoRepository vehiculoRepo,
                               PruebaRepository pruebaRepo,
                               UsuarioRepository usuarioRepo,
                               TecnicoRepository tecnicoRepo,
                               InscripcionPruebaRepository inscripcionRepo,
                               IncidenciaRepository incidenciaRepo) {
        this.verificacionRepo = verificacionRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.pruebaRepo = pruebaRepo;
        this.usuarioRepo = usuarioRepo;
        this.tecnicoRepo = tecnicoRepo;
        this.inscripcionRepo = inscripcionRepo;
        this.incidenciaRepo = incidenciaRepo;
    }

    public List<VerificacionTecnica> findAll() {
        log.info("VerificacionService - Obteniendo todas las verificaciones");
        return verificacionRepo.findAll();
    }

    public VerificacionTecnica findById(Integer id) {
        return verificacionRepo.findById(id).orElse(null);
    }

    @Transactional
    public VerificacionTecnica save(String matricula, Integer pruebaId, String resultado,
                                    LocalDate fecha, String tecnico2Licencia, String tecnico1Licencia) {
        Tecnico tecnico1;
        if (tecnico1Licencia != null && !tecnico1Licencia.isBlank()) {
            tecnico1 = tecnicoRepo.findById(tecnico1Licencia)
                    .orElseThrow(() -> new IllegalArgumentException("Técnico 1 no encontrado"));
        } else {
            tecnico1 = obtenerTecnicoAutenticado();
        }
        Vehiculo vehiculo = vehiculoRepo.findById(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
        Prueba prueba = pruebaRepo.findById(pruebaId)
                .orElseThrow(() -> new IllegalArgumentException("Prueba no encontrada"));

        VerificacionTecnica v = new VerificacionTecnica();
        v.setVehiculo(vehiculo);
        v.setPrueba(prueba);
        v.setTecnico1(tecnico1);
        v.setFecha(fecha);
        v.setResultado(resultado != null ? ResultadoVerificacion.valueOf(resultado) : ResultadoVerificacion.NO_APTO);

        if (tecnico2Licencia != null && !tecnico2Licencia.isBlank()) {
            tecnicoRepo.findById(tecnico2Licencia).ifPresent(v::setTecnico2);
        }

        log.info("VerificacionService - Guardando verificación para vehículo: {}", matricula);
        VerificacionTecnica saved = verificacionRepo.save(v);

        if (saved.getResultado() == ResultadoVerificacion.NO_APTO) {
            Incidencia inc = new Incidencia();
            inc.setVerificacion(saved);
            inc.setVehiculo(vehiculo);
            inc.setTecnico1(tecnico1);
            inc.setDescripcionIncidencia("Indica cuál ha sido el problema...");
            inc.setEstado(Estado.ABIERTA);
            inc.setFecha(LocalDate.now());
            incidenciaRepo.save(inc);
            log.info("VerificacionService - Incidencia creada automáticamente por resultado NO_APTO");
        }

        return saved;
    }

    @Transactional
    public VerificacionTecnica update(Integer id, String matricula, Integer pruebaId,
                                      String resultado, LocalDate fecha, String tecnico2Licencia,
                                      String tecnico1Licencia) {
        VerificacionTecnica existente = verificacionRepo.findById(id).orElse(null);
        if (existente == null) return null;

        Vehiculo vehiculo = vehiculoRepo.findById(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
        Prueba prueba = pruebaRepo.findById(pruebaId)
                .orElseThrow(() -> new IllegalArgumentException("Prueba no encontrada"));

        existente.setVehiculo(vehiculo);
        existente.setPrueba(prueba);
        existente.setFecha(fecha);
        existente.setResultado(resultado != null ? ResultadoVerificacion.valueOf(resultado) : ResultadoVerificacion.NO_APTO);
        if (tecnico1Licencia != null && !tecnico1Licencia.isBlank()) {
            tecnicoRepo.findById(tecnico1Licencia).ifPresent(existente::setTecnico1);
        }

        if (tecnico2Licencia != null && !tecnico2Licencia.isBlank()) {
            tecnicoRepo.findById(tecnico2Licencia).ifPresent(existente::setTecnico2);
        } else {
            existente.setTecnico2(null);
        }

        log.info("VerificacionService - Actualizando verificación ID: {}", id);
        return verificacionRepo.save(existente);
    }

    public void deleteById(Integer id) {
        log.info("VerificacionService - Eliminando verificación ID: {}", id);
        verificacionRepo.deleteById(id);
    }

    public List<Vehiculo> getVehiculosPendientesPorPrueba(Integer pruebaId) {
        Set<String> yaVerificados = verificacionRepo.findByPrueba_IdPrueba(pruebaId).stream()
                .map(v -> v.getVehiculo().getMatricula())
                .collect(Collectors.toSet());

        return inscripcionRepo.findVehiculosByPruebaId(pruebaId).stream()
                .filter(v -> !yaVerificados.contains(v.getMatricula()))
                .distinct()
                .toList();
    }

    public List<Vehiculo> getAllVehiculos() { return vehiculoRepo.findAll(); }

    public List<Prueba> getAllPruebas() { return pruebaRepo.findAll(); }

    public List<Tecnico> getAllTecnicos() { return tecnicoRepo.findAll(); }

    private Tecnico obtenerTecnicoAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("No hay usuario autenticado");
        Usuario usuario = usuarioRepo.findByNombre(auth.getName());
        if (usuario == null) throw new IllegalStateException("Usuario no encontrado");
        if (usuario instanceof Tecnico t) return t;
        // Admin puede verificar también
        throw new IllegalStateException("El usuario autenticado no es un técnico");
    }
}
