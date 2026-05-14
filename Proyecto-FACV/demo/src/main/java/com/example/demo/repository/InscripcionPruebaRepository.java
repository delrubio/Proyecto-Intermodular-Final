package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.InscripcionPrueba;
import com.example.demo.model.InscripcionPruebaId;
import com.example.demo.model.Vehiculo;

public interface InscripcionPruebaRepository extends JpaRepository<InscripcionPrueba, InscripcionPruebaId> {

    List<InscripcionPrueba> findByIdIdPrueba(Integer idPrueba);

    List<InscripcionPrueba> findByIdMatricula(String matricula);

    @Query("SELECT ip.vehiculo FROM InscripcionPrueba ip WHERE ip.prueba.idPrueba = :pruebaId")
    List<Vehiculo> findVehiculosByPruebaId(@Param("pruebaId") Integer pruebaId);
}
