package com.uniminuto.clinica.models.rs;

import com.uniminuto.clinica.entities.EstadoHistoriaClinica;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Detalle completo de una historia clinica (vista "Ver").
 */
@Data
public class HistoriaClinicaDetalleRS {

    /** Identificador de la historia. */
    private Long id;

    /** Identificador de la cita que origino la historia. */
    private Long citaId;

    /** Identificador del cliente. */
    private Long clienteId;

    /** Nombre completo del cliente. */
    private String clienteNombre;

    /** Identificador de la mascota. */
    private Long mascotaId;

    /** Nombre de la mascota. */
    private String mascotaNombre;

    /** Identificador del veterinario. */
    private Long veterinarioId;

    /** Nombre completo del veterinario. */
    private String veterinarioNombre;

    /** Fecha y hora de la consulta. */
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    private String motivoConsulta;

    /** Diagnostico. */
    private String diagnostico;

    /** Observaciones. */
    private String observaciones;

    /** Estado de la historia. */
    private EstadoHistoriaClinica estado;

    /** Fecha de creacion del registro. */
    private LocalDateTime createdAt;
}
