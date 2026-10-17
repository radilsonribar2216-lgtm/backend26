package com.uniminuto.clinica.models.rq;

import lombok.Getter;
import lombok.Setter;

/**
 * Parametros comunes de paginacion y ordenamiento de los listados.
 * Llegan como parametros de la URL (query string).
 */
@Getter
@Setter
public class PaginacionRQ {

    /** Numero de pagina solicitado (base 0). */
    private int page = 0;

    /** Cantidad de registros por pagina (entre 1 y 100). */
    private int size = 10;

    /** Nombre de la columna por la que se ordena (opcional). */
    private String sortBy;

    /** Direccion del orden: "asc" o "desc" (opcional). */
    private String sortDir;
}
