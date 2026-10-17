package com.uniminuto.clinica.models.rq;

import com.uniminuto.clinica.entities.EstadoHistoriaClinica;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Filtros del listado de historias clinicas. Todos son opcionales.
 */
@Getter
@Setter
public class HistoriaClinicaFiltroRQ extends PaginacionRQ {

    /** Estado de la historia. */
    private EstadoHistoriaClinica estado;

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

    /** Texto libre: busca en cliente, mascota, veterinario, motivo y diagnostico. */
    private String texto;
}
