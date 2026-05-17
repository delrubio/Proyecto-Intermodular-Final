package com.example.demo.enums;

/**
 * Roles disponibles en la aplicación FACV.
 * <p>
 * El rol se almacena como columna discriminadora en la tabla {@code usuario} y determina la
 * subclase JPA instanciada, así como los permisos de Spring Security (prefijo {@code ROLE_}).
 * </p>
 * <ul>
 *   <li>{@link #OBSERVADOR} – puede redactar informes de pruebas.</li>
 *   <li>{@link #ADMINISTRADOR} – acceso total; único rol con acceso al panel {@code /admin}.</li>
 *   <li>{@link #ORGANIZADOR} – crea y gestiona pruebas de rally.</li>
 *   <li>{@link #PILOTO} – registra vehículos e inscripciones.</li>
 *   <li>{@link #TECNICO} – realiza verificaciones técnicas de vehículos.</li>
 * </ul>
 */
public enum RolUsuario {
    OBSERVADOR, ADMINISTRADOR, ORGANIZADOR, PILOTO, TECNICO
}
