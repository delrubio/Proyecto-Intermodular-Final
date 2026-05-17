package com.example.demo.enums;

/**
 * Resultado posible de una {@link com.example.demo.model.VerificacionTecnica}.
 * <ul>
 *   <li>{@link #APTO} – el vehículo cumple todos los requisitos técnicos para competir.</li>
 *   <li>{@link #NO_APTO} – el vehículo presenta deficiencias; se genera automáticamente
 *       una {@link com.example.demo.model.Incidencia} en estado {@code ABIERTA}.</li>
 * </ul>
 */
public enum ResultadoVerificacion {
    APTO, NO_APTO
}
