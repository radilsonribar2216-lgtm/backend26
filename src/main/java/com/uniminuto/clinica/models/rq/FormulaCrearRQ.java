package com.uniminuto.clinica.models.rq;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Datos para registrar una formula medica.
 */
@Data
public class FormulaCrearRQ {

    /** Mascota para la que se emite la formula. */
    @NotNull(message = "La mascota es obligatoria.")
    private Long mascotaId;

    /** Historia clinica (consulta) a la que se asocia. */
    @NotNull(message = "La historia clínica es obligatoria.")
    private Long historiaClinicaId;

    /** Veterinario que emite la formula. */
    @NotNull(message = "El veterinario es obligatorio.")
    private Long veterinarioId;

    /** Fecha y hora de emision (yyyy-MM-ddTHH:mm). */
    @NotNull(message = "La fecha de emisión es obligatoria.")
    private LocalDateTime fechaEmision;

    /** Nombre del medicamento. */
    @NotBlank(message = "El medicamento es obligatorio.")
    @Size(max = 150, message = "El medicamento no puede superar 150 caracteres.")
    private String medicamento;

    /** Cantidad de la dosis (mayor que cero). */
    @NotNull(message = "La dosis es obligatoria.")
    @Positive(message = "La dosis debe ser mayor que cero.")
    @Digits(integer = 8, fraction = 2, message = "La dosis admite máximo 2 decimales.")
    private BigDecimal dosisCantidad;

    /** Unidad de la dosis. */
    @NotBlank(message = "La unidad de la dosis es obligatoria.")
    @Size(max = 20, message = "La unidad no puede superar 20 caracteres.")
    private String dosisUnidad;

    /** Frecuencia de administracion. */
    @NotBlank(message = "La frecuencia es obligatoria.")
    @Size(max = 100, message = "La frecuencia no puede superar 100 caracteres.")
    private String frecuencia;

    /** Duracion del tratamiento en dias (1 a 365). */
    @NotNull(message = "La duración del tratamiento es obligatoria.")
    @Min(value = 1, message = "La duración debe ser de al menos 1 día.")
    @Max(value = 365, message = "La duración no puede superar 365 días.")
    private Integer duracionDias;

    /** Indicaciones para el cliente. */
    @NotBlank(message = "Las indicaciones son obligatorias.")
    private String indicaciones;
}
