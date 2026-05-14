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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "piloto")
@PrimaryKeyJoinColumn(name = "licencia")
@DiscriminatorValue("PILOTO")
@Getter
@Setter
@NoArgsConstructor
public class Piloto extends Usuario {

    @Column(name = "club", length = 100)
    private String club;

    @Column(name = "carreras_ganadas")
    private Integer carrerasGanadas = 0;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "piloto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehiculo> vehiculos = new ArrayList<>();
}
