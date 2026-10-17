package com.uniminuto.clinica.models.rq;

import com.uniminuto.clinica.entities.EstadoCita;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Solicitud de cambio de estado de una cita.
 */
@Data
public class CitaEstadoRQ {

    /** Nuevo estado deseado. */
    @NotNull(message = "El estado es obligatorio.")
    private EstadoCita estado;
}
