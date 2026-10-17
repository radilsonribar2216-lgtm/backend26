package com.uniminuto.clinica.models.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Respuesta paginada generica usada por los listados (DataTable).
 *
 * @param <T> tipo de los elementos de la pagina.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginaRS<T> {

    /** Elementos de la pagina actual. */
    private List<T> content;

    /** Numero de la pagina actual (base 0). */
    private int page;

    /** Tamano de pagina solicitado. */
    private int size;

    /** Total de registros que cumplen el filtro. */
    private long totalElements;

    /** Total de paginas. */
    private int totalPages;
}
