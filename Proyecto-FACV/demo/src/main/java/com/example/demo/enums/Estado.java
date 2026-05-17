package com.example.demo.enums;

/**
 * Estado del ciclo de vida de una {@link com.example.demo.model.Incidencia}.
 * <ul>
 *   <li>{@link #ABIERTA} – incidencia recién creada, pendiente de revisión.</li>
 *   <li>{@link #EN_REVISION} – en proceso de análisis por un técnico.</li>
 *   <li>{@link #RESUELTA} – marcada como solucionada; el sistema la pasa automáticamente a {@link #OCULTA}
 *       y la verificación técnica asociada cambia a {@code APTO}.</li>
 *   <li>{@link #OCULTA} – cerrada, no aparece en el listado público.</li>
 * </ul>
 */
public enum Estado {
    ABIERTA,
    EN_REVISION,
    RESUELTA,
    OCULTA
}
