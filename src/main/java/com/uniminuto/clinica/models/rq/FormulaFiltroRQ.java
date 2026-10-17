package com.uniminuto.clinica.models.rq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Filtros del listado de formulas medicas. Todos son opcionales.
 */
@Getter
@Setter
public class FormulaFiltroRQ extends PaginacionRQ {

    /** true = solo activas, false = solo inactivas, null = todas. */
    private Boolean activo;

    /** Identificador del cliente. */
    private Long clienteId;

    /** Identificador de la mascota. */
    private Long mascotaId;

    /** Identificador del veterinario. */
    private Long veterinarioId;

    /** Fecha de emision inicial (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha de emision final (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    /** Texto libre: busca en medicamento, mascota, cliente y veterinario. */
    private String texto;
}
