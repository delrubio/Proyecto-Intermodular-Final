package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "organizador")
@PrimaryKeyJoinColumn(name = "licencia")
@DiscriminatorValue("ORGANIZADOR")
@Getter
@Setter
@NoArgsConstructor
public class Organizador extends Usuario {

    @NotBlank(message = "El club es obligatorio")
    @Column(name = "club", length = 100, nullable = false)
    private String club;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "organizador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prueba> pruebas = new ArrayList<>();
}
