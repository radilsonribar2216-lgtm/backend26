package com.uniminuto.clinica.util;

import com.uniminuto.clinica.models.rq.PaginacionRQ;
import com.uniminuto.clinica.models.rs.PaginaRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.function.Function;

/**
 * Utilidades para construir la paginacion/ordenamiento y convertir las paginas
 * de entidades en {@link PaginaRS}.
 */
public final class PaginacionUtil {

    /** Tamano maximo de pagina permitido. */
    private static final int TAMANO_MAXIMO = 100;

    private PaginacionUtil() {
    }

    /**
     * Construye el {@link Pageable} a partir de los parametros recibidos. Solo
     * se permite ordenar por las columnas incluidas en {@code camposOrdenables},
     * lo que evita ordenar por atributos arbitrarios.
     *
     * @param rq               parametros de paginacion.
     * @param camposOrdenables mapa nombre expuesto -> ruta del atributo en la entidad.
     * @param campoPorDefecto  ruta del atributo para el orden inicial.
     * @param direccionDefecto direccion del orden inicial.
     * @return paginacion lista para el repositorio.
     */
    public static Pageable crear(PaginacionRQ rq, Map<String, String> camposOrdenables,
                                 String campoPorDefecto, Sort.Direction direccionDefecto) {
        int pagina = Math.max(rq.getPage(), 0);
        int tamano = Math.min(Math.max(rq.getSize(), 1), TAMANO_MAXIMO);

        String campo = rq.getSortBy() == null ? null : camposOrdenables.get(rq.getSortBy());
        Sort.Direction direccion;
        if (campo == null) {
            campo = campoPorDefecto;
            direccion = direccionDefecto;
        } else {
            direccion = "desc".equalsIgnoreCase(rq.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        }
        // Orden secundario por id para que la paginacion sea estable.
        Sort orden = Sort.by(direccion, campo).and(Sort.by(Sort.Direction.DESC, "id"));
        return PageRequest.of(pagina, tamano, orden);
    }

    /**
     * Convierte una pagina de entidades en una pagina de respuesta.
     *
     * @param pagina      pagina de entidades.
     * @param convertidor funcion entidad -> DTO.
     * @param <E>         tipo de la entidad.
     * @param <R>         tipo del DTO.
     * @return pagina de respuesta.
     */
    public static <E, R> PaginaRS<R> aRespuesta(Page<E> pagina, Function<E, R> convertidor) {
        return new PaginaRS<>(
                pagina.getContent().stream().map(convertidor).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages());
    }
}
