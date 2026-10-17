package com.uniminuto.clinica.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Utilidades comunes para construir filtros dinamicos con Criteria API.
 */
public final class SpecUtil {

    private SpecUtil() {
    }

    /**
     * Navega una ruta de atributos a partir de una raiz (por ejemplo
     * {@code ruta(root, "mascota", "cliente", "id")}).
     *
     * @param origen raiz de la consulta.
     * @param partes atributos a recorrer en orden.
     * @param <T>    tipo del atributo final.
     * @return ruta resultante.
     */
    @SuppressWarnings("unchecked")
    public static <T> Path<T> ruta(From<?, ?> origen, String... partes) {
        Path<?> actual = origen;
        for (String parte : partes) {
            actual = actual.get(parte);
        }
        return (Path<T>) actual;
    }

    /**
     * Predicado "contiene" sin distinguir mayusculas.
     *
     * @param cb    constructor de criterios.
     * @param campo expresion de texto a comparar.
     * @param texto texto buscado.
     * @return predicado LIKE.
     */
    public static Predicate contiene(CriteriaBuilder cb, Expression<String> campo, String texto) {
        return cb.like(cb.lower(campo), "%" + texto.trim().toLowerCase() + "%");
    }

    /**
     * Expresion "nombres apellidos" para buscar por nombre completo.
     *
     * @param cb        constructor de criterios.
     * @param nombres   atributo de nombres.
     * @param apellidos atributo de apellidos.
     * @return expresion concatenada.
     */
    public static Expression<String> nombreCompleto(CriteriaBuilder cb,
                                                    Expression<String> nombres,
                                                    Expression<String> apellidos) {
        return cb.concat(cb.concat(nombres, " "), apellidos);
    }

    /**
     * Inicio del dia de una fecha (para filtros "desde").
     *
     * @param fecha fecha.
     * @return fecha y hora a las 00:00.
     */
    public static LocalDateTime inicioDelDia(LocalDate fecha) {
        return fecha.atStartOfDay();
    }

    /**
     * Inicio del dia siguiente (limite exclusivo para filtros "hasta" inclusivos).
     *
     * @param fecha fecha.
     * @return fecha y hora a las 00:00 del dia siguiente.
     */
    public static LocalDateTime inicioDiaSiguiente(LocalDate fecha) {
        return fecha.plusDays(1).atStartOfDay();
    }

    /**
     * Indica si un texto tiene contenido util para filtrar.
     *
     * @param texto texto a evaluar.
     * @return true si no es nulo ni vacio.
     */
    public static boolean hayTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
