package com.example.demo.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.enums.RolUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad base de la jerarquía de usuarios, mapeada a la tabla {@code usuario}.
 * <p>
 * Utiliza herencia {@code JOINED}: cada subclase tiene su propia tabla que comparte
 * la clave primaria {@code licencia} con esta tabla padre. La columna {@code rol}
 * actúa como discriminador JPA y determina la subclase concreta a instanciar.
 * </p>
 * <p>
 * Implementa {@link UserDetails} para integración directa con Spring Security;
 * el {@code username} es el campo {@code nombre} y la autoridad se construye
 * como {@code ROLE_<ROL>}.
 * </p>
 *
 * @see Administrador
 * @see Piloto
 * @see Tecnico
 * @see Observador
 * @see Organizador
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "rol", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Usuario implements UserDetails {

    @Id
    @Column(name = "licencia", length = 20)
    private String licencia;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", length = 30, nullable = false, unique = true)
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Column(name = "apellidos", length = 30, nullable = false)
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "telefono", length = 9)
    private String telefono;

    @Column(name = "localidad", length = 100)
    private String localidad;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 20, nullable = false, insertable = false, updatable = false)
    private RolUsuario rol;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    @Override
    public String getUsername() {
        return this.nombre;
    }

    @Override
    public boolean isAccountNonExpired()     { return true; }
    @Override
    public boolean isAccountNonLocked()      { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled()               { return true; }
}
