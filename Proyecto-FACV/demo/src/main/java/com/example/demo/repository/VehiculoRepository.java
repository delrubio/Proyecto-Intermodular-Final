package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {
    List<Vehiculo> findByPilotoLicencia(String pilotoLicencia);
}
