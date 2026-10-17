package com.uniminuto.clinica.models.rs;

import com.uniminuto.clinica.entities.EstadoHistoriaClinica;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Informacion resumida de una historia clinica para el listado (DataTable).
 */
@Data
public class HistoriaClinicaResumenRS {

    /** Identificador de la historia. */
    private Long id;

    /** Fecha y hora de la consulta. */
    private LocalDateTime fechaHora;

    /** Nombre de la mascota. */
    private String mascotaNombre;

    /** Nombre completo del cliente. */
    private String clienteNombre;

    /** Nombre completo del veterinario. */
    private String veterinarioNombre;

    /** Motivo de la consulta. */
    private String motivoConsulta;

    /** Estado de la historia. */
    private EstadoHistoriaClinica estado;
}
