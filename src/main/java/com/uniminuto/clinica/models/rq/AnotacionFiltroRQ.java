package com.uniminuto.clinica.models.rq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Filtros del historico de anotaciones. Todos son opcionales.
 */
@Getter
@Setter
public class AnotacionFiltroRQ extends PaginacionRQ {

    /** Identificador del veterinario. */
    private Long veterinarioId;

    /** Fecha inicial del rango (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDesde;

    /** Fecha final del rango (inclusive), formato yyyy-MM-dd. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaHasta;

    /** Texto libre: busca en descripcion, observacion y diagnostico. */
    private String texto;

    /**
     * Si es true, solo trae las anotaciones de esta historia. Si es false
     * (valor por defecto) trae el historico de toda la mascota.
     */
    private boolean soloHistoria = false;
}
