package com.uniminuto.clinica.models.rs;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos de una anotacion de historia clinica.
 */
@Data
public class AnotacionRS {

    /** Identificador de la anotacion. */
    private Long id;

    /** Identificador de la historia clinica a la que pertenece. */
    private Long historiaClinicaId;

    /** Fecha y hora de la anotacion. */
    private LocalDateTime fechaHora;

    /** Identificador del veterinario. */
    private Long veterinarioId;

    /** Nombre completo del veterinario. */
    private String veterinarioNombre;

    /** Descripcion. */
    private String descripcion;

    /** Observacion. */
    private String observacion;

    /** Diagnostico o evolucion. */
    private String diagnosticoEvolucion;
}
