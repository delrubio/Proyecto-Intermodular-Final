package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // Públicas
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/pruebas/*/imagen").permitAll()

                // Consola H2 + panel admin: solo ADMINISTRADOR
                .requestMatchers("/h2-console/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/usuarios", "/findByName").hasRole("ADMINISTRADOR")

                // Vehículos — CUD: ADMIN + PILOTO
                .requestMatchers(HttpMethod.GET,  "/nuevo-vehiculo").hasAnyRole("ADMINISTRADOR", "PILOTO")
                .requestMatchers(HttpMethod.POST, "/nuevo-vehiculo").hasAnyRole("ADMINISTRADOR", "PILOTO")
                .requestMatchers(HttpMethod.GET,  "/vehiculos/*/editar").hasAnyRole("ADMINISTRADOR", "PILOTO")
                .requestMatchers(HttpMethod.POST, "/vehiculos/*/editar").hasAnyRole("ADMINISTRADOR", "PILOTO")
                .requestMatchers(HttpMethod.POST, "/vehiculos/*/eliminar").hasAnyRole("ADMINISTRADOR", "PILOTO")

                // Pruebas — CUD: ADMIN + ORGANIZADOR
                .requestMatchers(HttpMethod.GET,  "/nueva-prueba").hasAnyRole("ADMINISTRADOR", "ORGANIZADOR")
                .requestMatchers(HttpMethod.POST, "/nueva-prueba").hasAnyRole("ADMINISTRADOR", "ORGANIZADOR")
                .requestMatchers("/pruebas/*/editar").hasAnyRole("ADMINISTRADOR", "ORGANIZADOR")
                .requestMatchers("/pruebas/*/eliminar").hasAnyRole("ADMINISTRADOR", "ORGANIZADOR")

                // Inscripciones — CUD: ADMIN + PILOTO
                .requestMatchers(HttpMethod.GET,  "/nueva-inscripcion").hasAnyRole("ADMINISTRADOR", "PILOTO")
                .requestMatchers(HttpMethod.POST, "/nueva-inscripcion").hasAnyRole("ADMINISTRADOR", "PILOTO")
                .requestMatchers(HttpMethod.POST, "/inscripciones/*/eliminar").hasAnyRole("ADMINISTRADOR", "PILOTO")

                // Verificaciones — flujo completo: ADMIN + TECNICO
                .requestMatchers("/nueva-verificacion").hasAnyRole("ADMINISTRADOR", "TECNICO")
                .requestMatchers("/verificaciones/*/editar").hasAnyRole("ADMINISTRADOR", "TECNICO")
                .requestMatchers("/verificaciones/*/eliminar").hasAnyRole("ADMINISTRADOR", "TECNICO")
                .requestMatchers("/verificaciones/seleccionar-prueba").hasAnyRole("ADMINISTRADOR", "TECNICO")
                .requestMatchers("/verificaciones/pendientes").hasAnyRole("ADMINISTRADOR", "TECNICO")

                // Incidencias — CUD: ADMIN + TECNICO
                .requestMatchers("/nueva-incidencia").hasAnyRole("ADMINISTRADOR", "TECNICO")
                .requestMatchers("/incidencias/*/editar").hasAnyRole("ADMINISTRADOR", "TECNICO")
                .requestMatchers("/incidencias/*/eliminar").hasAnyRole("ADMINISTRADOR", "TECNICO")

                // Informes — CUD: ADMIN + OBSERVADOR
                .requestMatchers("/nuevo-informe").hasAnyRole("ADMINISTRADOR", "OBSERVADOR")
                .requestMatchers("/informes/*/editar").hasAnyRole("ADMINISTRADOR", "OBSERVADOR")
                .requestMatchers("/informes/*/eliminar").hasAnyRole("ADMINISTRADOR", "OBSERVADOR")

                // Resto: cualquier usuario autenticado
                .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
