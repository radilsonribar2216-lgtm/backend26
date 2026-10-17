package com.uniminuto.clinica.models.rq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos para registrar una historia clinica.
 */
@Data
public class HistoriaClinicaCrearRQ {

    /** Cliente dueno de la mascota. */
    @NotNull(message = "El cliente es obligatorio.")
    private Long clienteId;

    /** Mascota (debe pertenecer al cliente). */
    @NotNull(message = "La mascota es obligatoria.")
    private Long mascotaId;

    /** Cita de origen (debe ser de la misma mascota). */
    @NotNull(message = "La cita es obligatoria.")
    private Long citaId;

    /** Veterinario que atendio la consulta. */
    @NotNull(message = "El veterinario es obligatorio.")
    private Long veterinarioId;

    /** Fecha y hora de la consulta (yyyy-MM-ddTHH:mm). */
    @NotNull(message = "La fecha y hora son obligatorias.")
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    @NotBlank(message = "El motivo de la consulta es obligatorio.")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres.")
    private String motivoConsulta;

    /** Diagnostico. */
    @NotBlank(message = "El diagnóstico es obligatorio.")
    private String diagnostico;

    /** Observaciones (opcional). */
    private String observaciones;
}
