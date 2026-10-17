package com.uniminuto.clinica.models.rs;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Detalle completo de una formula medica (vista "Ver").
 */
@Data
public class FormulaDetalleRS {

    /** Identificador de la formula. */
    private Long id;

    /** Identificador de la mascota. */
    private Long mascotaId;

    /** Nombre de la mascota. */
    private String mascotaNombre;

    /** Identificador del cliente. */
    private Long clienteId;

    /** Nombre completo del cliente. */
    private String clienteNombre;

    /** Identificador de la historia clinica asociada. */
    private Long historiaClinicaId;

    /** Fecha y hora de la consulta asociada. */
    private LocalDateTime fechaConsulta;

    /** Motivo de la consulta asociada. */
    private String motivoConsulta;

    /** Identificador del veterinario. */
    private Long veterinarioId;

    /** Nombre completo del veterinario. */
    private String veterinarioNombre;

    /** Fecha y hora de emision. */
    private LocalDateTime fechaEmision;

    /** Medicamento. */
    private String medicamento;

    /** Cantidad de la dosis. */
    private BigDecimal dosisCantidad;

    /** Unidad de la dosis. */
    private String dosisUnidad;

    /** Frecuencia de administracion. */
    private String frecuencia;

    /** Duracion del tratamiento en dias. */
    private Integer duracionDias;

    /** Indicaciones. */
    private String indicaciones;

    /** Indica si la formula esta activa. */
    private boolean activo;

    /** Fecha de creacion del registro. */
    private LocalDateTime createdAt;

    /** Fecha de la ultima modificacion. */
    private LocalDateTime updatedAt;
}
