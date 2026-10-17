package com.uniminuto.clinica.entities;

/**
 * Estados posibles de una cita medica.
 */
public enum EstadoCita {
    /** Cita solicitada, aun sin confirmar. */
    PENDIENTE,
    /** Cita confirmada con el cliente. */
    CONFIRMADA,
    /** Cita ya realizada; puede generar o asociarse a una historia clinica. */
    ATENDIDA,
    /** Cita cancelada; no puede modificarse como si siguiera activa. */
    CANCELADA,
    /** Cita programada en agenda. */
    PROGRAMADA
}
