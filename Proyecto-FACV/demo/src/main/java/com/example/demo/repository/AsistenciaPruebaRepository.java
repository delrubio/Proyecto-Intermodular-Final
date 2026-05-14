package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.AsistenciaPrueba;
import com.example.demo.model.AsistenciaPruebaId;

public interface AsistenciaPruebaRepository extends JpaRepository<AsistenciaPrueba, AsistenciaPruebaId> {
    List<AsistenciaPrueba> findByIdIdPrueba(Integer idPrueba);
    List<AsistenciaPrueba> findByIdUsuarioLicencia(String usuarioLicencia);
}
