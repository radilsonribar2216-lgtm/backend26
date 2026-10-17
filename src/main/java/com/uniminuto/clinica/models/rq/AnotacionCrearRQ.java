package com.uniminuto.clinica.models.rq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos para agregar una anotacion a una historia clinica existente.
 */
@Data
public class AnotacionCrearRQ {

    /** Veterinario que registra la anotacion. */
    @NotNull(message = "El veterinario es obligatorio.")
    private Long veterinarioId;

    /** Fecha y hora. Si no se envia se usa la actual. */
    private LocalDateTime fechaHora;

    /** Descripcion de lo realizado. */
    @NotBlank(message = "La descripción es obligatoria.")
    private String descripcion;

    /** Observacion (opcional). */
    private String observacion;

    /** Diagnostico o evolucion. */
    @NotBlank(message = "El diagnóstico o evolucion es obligatorio.")
    private String diagnosticoEvolucion;
}
