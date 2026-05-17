package com.example.demo.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.Administrador;
import com.example.demo.model.Observador;
import com.example.demo.model.Organizador;
import com.example.demo.model.Piloto;
import com.example.demo.model.Prueba;
import com.example.demo.model.Tecnico;
import com.example.demo.model.Vehiculo;
import com.example.demo.repository.PruebaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.VehiculoRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Inicializador de datos de prueba para arranque en entorno de desarrollo.
 * <p>
 * Implementa {@link org.springframework.boot.CommandLineRunner} a través de un bean
 * {@code @Bean} que se ejecuta al inicio de la aplicación. Si ya hay usuarios en la base
 * de datos, la semilla se omite para no duplicar registros en reinicios.
 * </p>
 * <p>
 * Crea los siguientes usuarios con contraseña {@code 1234} (codificada con BCrypt):
 * <ul>
 *   <li>Ignacio (ADMINISTRADOR) – licencia {@code LIC-ADMIN-001}</li>
 *   <li>Carlos (ORGANIZADOR) – licencia {@code LIC-ORG-001}</li>
 *   <li>Miguel (PILOTO) – licencia {@code LIC-PIL-001}</li>
 *   <li>Ana (TECNICO) – licencia {@code LIC-TEC-001}</li>
 *   <li>Luis (OBSERVADOR) – licencia {@code LIC-OBS-001}</li>
 * </ul>
 * También crea dos pruebas y dos vehículos de ejemplo.
 * </p>
 */
@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UsuarioRepository usuarioRepo,
            PasswordEncoder passwordEncoder,
            PruebaRepository pruebaRepo,
            VehiculoRepository vehiculoRepo) {

        return args -> {
            if (usuarioRepo.count() > 0) {
                log.info("DataInitializer - BD ya inicializada, omitiendo seed");
                return;
            }

            log.info("DataInitializer - Inicializando datos de prueba...");

            // ── Administrador ─────────────────────────────────────────────────────────────
            Administrador admin = new Administrador();
            admin.setLicencia("LIC-ADMIN-001");
            admin.setNombre("Ignacio");
            admin.setApellidos("Aviño");
            admin.setEmail("admin@facv.es");
            admin.setPassword(passwordEncoder.encode("12345"));
            admin.setTelefono("600000001");
            admin.setLocalidad("Valencia");
            admin.setFechaNacimiento(LocalDate.of(1980, 1, 15));
            admin.setPresidenteFacv(true);
            admin.setExperiencia((byte) 10);
            usuarioRepo.save(admin);

            // ── Organizador ───────────────────────────────────────────────────────────────
            Organizador org = new Organizador();
            org.setLicencia("LIC-ORG-001");
            org.setNombre("Carlos");
            org.setApellidos("Montoya");
            org.setEmail("organizador@facv.es");
            org.setPassword(passwordEncoder.encode("1234"));
            org.setTelefono("600000002");
            org.setLocalidad("Alicante");
            org.setFechaNacimiento(LocalDate.of(1975, 6, 20));
            org.setClub("Club Rally Valencia");
            usuarioRepo.save(org);

            // ── Piloto ────────────────────────────────────────────────────────────────────
            Piloto piloto = new Piloto();
            piloto.setLicencia("LIC-PIL-001");
            piloto.setNombre("Miguel");
            piloto.setApellidos("Fernandez");
            piloto.setEmail("piloto@facv.es");
            piloto.setPassword(passwordEncoder.encode("1234"));
            piloto.setTelefono("600000003");
            piloto.setLocalidad("Castellón");
            piloto.setFechaNacimiento(LocalDate.of(1990, 3, 10));
            piloto.setClub("Team Speed");
            piloto.setCarrerasGanadas(5);
            usuarioRepo.save(piloto);

            // ── Técnico ───────────────────────────────────────────────────────────────────
            Tecnico tecnico = new Tecnico();
            tecnico.setLicencia("LIC-TEC-001");
            tecnico.setNombre("Ana");
            tecnico.setApellidos("Garcia");
            tecnico.setEmail("tecnico@facv.es");
            tecnico.setPassword(passwordEncoder.encode("1234"));
            tecnico.setTelefono("600000004");
            tecnico.setLocalidad("Valencia");
            tecnico.setFechaNacimiento(LocalDate.of(1985, 9, 5));
            tecnico.setNivelTecnico((byte) 3);
            tecnico.setDescripcion("Técnica especializada en motores de rally");
            usuarioRepo.save(tecnico);

            // ── Observador ────────────────────────────────────────────────────────────────
            Observador obs = new Observador();
            obs.setLicencia("LIC-OBS-001");
            obs.setNombre("Luis");
            obs.setApellidos("Pérez");
            obs.setEmail("observador@facv.es");
            obs.setPassword(passwordEncoder.encode("1234"));
            obs.setTelefono("600000005");
            obs.setLocalidad("Valencia");
            obs.setFechaNacimiento(LocalDate.of(1970, 12, 1));
            obs.setFederacion("Federación Española Automovilismo");
            usuarioRepo.save(obs);

            // ── Prueba 1 ──────────────────────────────────────────────────────────────────
            Prueba prueba1 = new Prueba();
            prueba1.setNombre("Rally Costa Azahar");
            prueba1.setFecha(LocalDate.of(2026, 6, 14));
            prueba1.setLocalidad("Castellón");
            prueba1.setCampeonato("Campeonato Autonómico Valenciano");
            prueba1.setOrganizador(org);
            prueba1.setNInscritos(0);
            pruebaRepo.save(prueba1);

            // ── Prueba 2 ──────────────────────────────────────────────────────────────────
            Prueba prueba2 = new Prueba();
            prueba2.setNombre("Rally Città di Valencia");
            prueba2.setFecha(LocalDate.of(2026, 9, 20));
            prueba2.setLocalidad("Valencia");
            prueba2.setCampeonato("Campeonato Autonómico Valenciano");
            prueba2.setOrganizador(org);
            prueba2.setNInscritos(0);
            pruebaRepo.save(prueba2);

            // ── Vehículo 1 ────────────────────────────────────────────────────────────────
            Vehiculo v1 = new Vehiculo();
            v1.setMatricula("1234-ABC");
            v1.setMarca("Citroën");
            v1.setModelo("C3 Rally2");
            v1.setCategoria("Rally2");
            v1.setPiloto(piloto);
            vehiculoRepo.save(v1);

            // ── Vehículo 2 ────────────────────────────────────────────────────────────────
            Vehiculo v2 = new Vehiculo();
            v2.setMatricula("5678-XYZ");
            v2.setMarca("Hyundai");
            v2.setModelo("i20 N Rally2");
            v2.setCategoria("Rally2");
            v2.setPiloto(piloto);
            vehiculoRepo.save(v2);

            log.info("DataInitializer - Datos inicializados correctamente");
            log.info("  ADMIN       → Ignacio  / 1234");
            log.info("  ORGANIZADOR → Carlos   / 1234");
            log.info("  PILOTO      → Miguel   / 1234");
            log.info("  TECNICO     → Ana      / 1234");
            log.info("  OBSERVADOR  → Luis     / 1234");
            log.info("  Pruebas: 'Rally Costa Azahar' y 'Rally Città di Valencia'");
            log.info("  Vehículos: 1234-ABC (Citroën C3) y 5678-XYZ (Hyundai i20)");
        };
    }
}
