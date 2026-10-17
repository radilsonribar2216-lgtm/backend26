package com.uniminuto.clinica.models.rq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Datos editables de una cita. El cliente y la mascota no se pueden cambiar;
 * el estado se modifica con operaciones explicitas.
 */
@Data
public class CitaEditarRQ {

    /** Veterinario asignado. */
    @NotNull(message = "El veterinario es obligatorio.")
    private Long veterinarioId;

    /** Nueva fecha (yyyy-MM-dd). */
    @NotNull(message = "La fecha es obligatoria.")
    private LocalDate fecha;

    /** Nueva hora (HH:mm). */
    @NotNull(message = "La hora es obligatoria.")
    private LocalTime hora;

    /** Motivo de la consulta. */
    @NotBlank(message = "El motivo de la consulta es obligatorio.")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres.")
    private String motivo;
}
