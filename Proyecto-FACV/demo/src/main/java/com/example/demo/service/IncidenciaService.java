package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.Estado;
import com.example.demo.model.Incidencia;
import com.example.demo.model.Prueba;
import com.example.demo.model.Vehiculo;
import com.example.demo.model.VerificacionTecnica;
import com.example.demo.repository.IncidenciaRepository;
import com.example.demo.repository.PruebaRepository;
import com.example.demo.repository.VehiculoRepository;
import com.example.demo.repository.VerificacionTecnicaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IncidenciaService {

    @Autowired IncidenciaRepository incidenciaRepository;
    @Autowired VehiculoRepository vehiculoRepository;
    @Autowired PruebaRepository pruebaRepository;
    @Autowired VerificacionTecnicaRepository verificacionTecnicaRepository;

    public List<Incidencia> findAll() {
        return incidenciaRepository.findAll();
    }

    public List<Incidencia> findAllVisible() {
        log.info("IncidenciaService - Incidencias visibles (no OCULTA)");
        return incidenciaRepository.findByEstadoNot(Estado.OCULTA);
    }

    public List<Incidencia> findByVehiculo(String matricula) {
        return incidenciaRepository.findByVehiculo_Matricula(matricula);
    }

    public Vehiculo findVehiculoByMatricula(String matricula) {
        return vehiculoRepository.findById(matricula).orElse(null);
    }

    public Incidencia findById(Integer id) {
        return incidenciaRepository.findById(id).orElse(null);
    }

    public Incidencia update(Integer id, String descripcionIncidencia, Estado estado) {
        Incidencia existente = incidenciaRepository.findById(id).orElse(null);
        if (existente == null) {
            log.warn("IncidenciaService - Incidencia {} no encontrada", id);
            return null;
        }

        existente.setDescripcionIncidencia(descripcionIncidencia);
        // RESUELTA → OCULTA automáticamente; y la verificación pasa a APTO
        Estado estadoFinal = (estado == Estado.RESUELTA) ? Estado.OCULTA : estado;
        existente.setEstado(estadoFinal);
        if (estado == Estado.RESUELTA) {
            VerificacionTecnica verificacion = existente.getVerificacion();
            if (verificacion != null) {
                verificacionTecnicaRepository.cambiarResultadoVerificacionIncidencia(verificacion.getId());
                log.info("IncidenciaService - Verificación {} marcada como APTO por resolución de incidencia", verificacion.getId());
            }
        }
        log.info("IncidenciaService - Actualizando incidencia ID: {}", id);
        return incidenciaRepository.save(existente);
    }

    public void deleteById(Integer id) {
        log.info("IncidenciaService - Eliminando incidencia ID: {}", id);
        incidenciaRepository.deleteById(id);
    }

    public List<Vehiculo> getAllVehiculos() { return vehiculoRepository.findAll(); }

    public List<Prueba> getAllPruebas() { return pruebaRepository.findAll(); }

    public Estado[] getAllEstados() { return Estado.values(); }
}
