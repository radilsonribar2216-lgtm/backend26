package com.uniminuto.clinica.models.rq;

import com.uniminuto.clinica.entities.EstadoCita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Datos para registrar una cita.
 */
@Data
public class CitaCrearRQ {

    /** Cliente dueno de la mascota. */
    @NotNull(message = "El cliente es obligatorio.")
    private Long clienteId;

    /** Mascota que sera atendida (debe pertenecer al cliente). */
    @NotNull(message = "La mascota es obligatoria.")
    private Long mascotaId;

    /** Veterinario asignado. */
    @NotNull(message = "El veterinario es obligatorio.")
    private Long veterinarioId;

    /** Fecha de la cita (yyyy-MM-dd). */
    @NotNull(message = "La fecha es obligatoria.")
    private LocalDate fecha;

    /** Hora de la cita (HH:mm). */
    @NotNull(message = "La hora es obligatoria.")
    private LocalTime hora;

    /** Motivo de la consulta. */
    @NotBlank(message = "El motivo de la consulta es obligatorio.")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres.")
    private String motivo;

    /** Estado inicial. Si no se envia, queda PENDIENTE. */
    private EstadoCita estado;
}
