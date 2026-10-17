package com.uniminuto.clinica.models.rq;

import com.uniminuto.clinica.entities.EstadoCita;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Filtros del listado de citas. Todos son opcionales.
 */
@Getter
@Setter
public class CitaFiltroRQ extends PaginacionRQ {

    /** Estado de la cita. */
    private EstadoCita estado;

    /** Identificador del cliente. */
    private Long clienteId;

    /** Identificador de la mascota. */
    private Long mascotaId;

    /** Identificador del veterinario. */
    private Long veterinarioId;

    /** Fecha inicial del rango (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha final del rango (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    /** Texto libre: busca en cliente, mascota, veterinario y motivo. */
    private String texto;
}
