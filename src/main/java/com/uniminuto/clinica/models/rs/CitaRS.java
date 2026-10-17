package com.uniminuto.clinica.models.rs;

import com.uniminuto.clinica.entities.EstadoCita;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Datos de una cita expuestos por la API (listado y detalle).
 */
@Data
public class CitaRS {

    /** Identificador de la cita. */
    private Long id;

    /** Fecha de la cita. */
    private LocalDate fecha;

    /** Hora de la cita. */
    private LocalTime hora;

    /** Fecha y hora combinadas. */
    private LocalDateTime fechaHora;

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

    /** Motivo de la consulta. */
    private String motivo;

    /** Estado de la cita. */
    private EstadoCita estado;
}
