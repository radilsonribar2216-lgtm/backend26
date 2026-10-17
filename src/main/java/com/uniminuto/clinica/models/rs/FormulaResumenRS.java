package com.uniminuto.clinica.models.rs;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Informacion resumida de una formula medica para el listado (DataTable).
 * No incluye indicaciones ni datos completos: eso va en {@link FormulaDetalleRS}.
 */
@Data
public class FormulaResumenRS {

    /** Identificador de la formula. */
    private Long id;

    /** Fecha y hora de creacion del registro (criterio del orden inicial). */
    private LocalDateTime createdAt;

    /** Fecha y hora de emision. */
    private LocalDateTime fechaEmision;

    /** Nombre de la mascota. */
    private String mascotaNombre;

    /** Nombre completo del cliente. */
    private String clienteNombre;

    /** Nombre completo del veterinario. */
    private String veterinarioNombre;

    /** Medicamento. */
    private String medicamento;

    /** Dosis en texto (cantidad y unidad, por ejemplo "4.00 gotas"). */
    private String dosis;

    /** Duracion del tratamiento en dias. */
    private Integer duracionDias;

    /** Indica si la formula esta activa. */
    private boolean activo;
}
