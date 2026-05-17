package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Prueba;

/**
 * Repositorio JPA para la entidad {@link Prueba}.
 * <p>
 * Incluye operaciones JPQL de actualización atómica del contador {@code nInscritos}
 * para evitar condiciones de carrera cuando varios pilotos se inscriben simultáneamente.
 * </p>
 */
public interface PruebaRepository extends JpaRepository<Prueba, Integer> {

    /**
     * Incrementa en 1 el contador de inscritos de la prueba indicada.
     * Se llama transaccionalmente desde {@link com.example.demo.service.InscripcionService#save}.
     *
     * @param id identificador de la prueba
     */
    @Modifying
    @Transactional
    @Query("UPDATE Prueba p SET p.nInscritos = COALESCE(p.nInscritos, 0) + 1 WHERE p.idPrueba = :id")
    void incrementarInscritos(@Param("id") Integer id);

    /**
     * Decrementa en 1 el contador de inscritos (mínimo 0) de la prueba indicada.
     * Se llama transaccionalmente desde {@link com.example.demo.service.InscripcionService#deleteById}.
     *
     * @param id identificador de la prueba
     */
    @Modifying
    @Transactional
    @Query("UPDATE Prueba p SET p.nInscritos = CASE WHEN COALESCE(p.nInscritos, 0) > 0 THEN COALESCE(p.nInscritos, 0) - 1 ELSE 0 END WHERE p.idPrueba = :id")
    void decrementarInscritos(@Param("id") Integer id);
}
