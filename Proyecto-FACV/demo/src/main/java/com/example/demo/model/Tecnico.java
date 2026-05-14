package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tecnico")
@PrimaryKeyJoinColumn(name = "licencia")
@DiscriminatorValue("TECNICO")
@Getter
@Setter
@NoArgsConstructor
public class Tecnico extends Usuario {

    @NotNull(message = "El nivel técnico es obligatorio")
    @Column(name = "nivel_tecnico", nullable = false)
    private Byte nivelTecnico;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(name = "descripcion", length = 150, nullable = false)
    private String descripcion;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tecnico1", fetch = FetchType.LAZY)
    private List<VerificacionTecnica> verificaciones = new ArrayList<>();
}
