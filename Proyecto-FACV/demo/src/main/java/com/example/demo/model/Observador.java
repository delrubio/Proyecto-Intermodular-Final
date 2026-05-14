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
@Table(name = "observador")
@PrimaryKeyJoinColumn(name = "licencia")
@DiscriminatorValue("OBSERVADOR")
@Getter
@Setter
@NoArgsConstructor
public class Observador extends Usuario {

    @NotBlank(message = "La federación es obligatoria")
    @Column(name = "federacion", length = 100, nullable = false)
    private String federacion;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "observador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Informe> informes = new ArrayList<>();
}
