package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Subclase de {@link Usuario} con rol {@code ADMINISTRADOR}.
 * <p>
 * Mapeada a la tabla {@code administrador} mediante herencia JOINED; la clave primaria
 * ({@code licencia}) referencia la tabla padre {@code usuario}.
 * </p>
 * <p>
 * El administrador tiene acceso total a la aplicación incluyendo el panel
 * {@code /admin/**} y puede operar sobre cualquier entidad del sistema.
 * </p>
 */
@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "licencia")
@DiscriminatorValue("ADMINISTRADOR")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Usuario {

    @Column(name = "presidente_facv")
    private Boolean presidenteFacv = false;

    @Column(name = "experiencia")
    private Byte experiencia = 0;
}
