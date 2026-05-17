package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Vehiculo;

/**
 * Repositorio JPA para la entidad {@link Vehiculo}.
 * <p>
 * La clave primaria es la matrícula ({@code String}).
 * </p>
 */
public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {

    /** Devuelve todos los vehículos pertenecientes a un piloto dado por su licencia. */
    List<Vehiculo> findByPilotoLicencia(String pilotoLicencia);
}
