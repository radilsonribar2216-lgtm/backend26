package com.uniminuto.clinica.entities;

/**
 * Estados posibles de una historia clinica.
 */
public enum EstadoHistoriaClinica {
    /** Historia en seguimiento; admite nuevas anotaciones. */
    ABIERTA,
    /** Historia cerrada; ya no admite cambios. */
    CERRADA
}
