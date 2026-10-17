package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.rq.AnotacionCrearRQ;
import com.uniminuto.clinica.models.rq.AnotacionFiltroRQ;
import com.uniminuto.clinica.models.rq.HistoriaClinicaCrearRQ;
import com.uniminuto.clinica.models.rq.HistoriaClinicaFiltroRQ;
import com.uniminuto.clinica.models.rs.AnotacionRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaDetalleRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaResumenRS;
import com.uniminuto.clinica.models.rs.PaginaRS;

/**
 * Casos de uso de historias clinicas y sus anotaciones.
 */
public interface HistoriaClinicaService {

    /**
     * Lista historias con filtros, paginacion y ordenamiento. Por defecto ordena
     * por fecha y hora de la consulta descendente.
     *
     * @param filtro filtros y parametros de paginacion.
     * @return pagina de historias (informacion resumida).
     */
    PaginaRS<HistoriaClinicaResumenRS> listar(HistoriaClinicaFiltroRQ filtro);

    /**
     * Consulta el detalle completo de una historia.
     *
     * @param id identificador de la historia.
     * @return detalle de la historia.
     */
    HistoriaClinicaDetalleRS consultar(Long id);

    /**
     * Registra una historia clinica validando cliente, mascota, cita y veterinario.
     *
     * @param rq datos de la historia.
     * @return historia creada.
     */
    HistoriaClinicaDetalleRS crear(HistoriaClinicaCrearRQ rq);

    /**
     * Lista las anotaciones (mas recientes primero) de una historia o del
     * historico completo de su mascota.
     *
     * @param historiaId identificador de la historia.
     * @param filtro     filtros y parametros de paginacion.
     * @return pagina de anotaciones.
     */
    PaginaRS<AnotacionRS> listarAnotaciones(Long historiaId, AnotacionFiltroRQ filtro);

    /**
     * Consulta el detalle de una anotacion.
     *
     * @param historiaId   identificador de la historia desde la que se consulta.
     * @param anotacionId  identificador de la anotacion.
     * @return detalle de la anotacion.
     */
    AnotacionRS consultarAnotacion(Long historiaId, Long anotacionId);

    /**
     * Agrega una anotacion a una historia abierta.
     *
     * @param historiaId identificador de la historia.
     * @param rq         datos de la anotacion.
     * @return anotacion creada.
     */
    AnotacionRS crearAnotacion(Long historiaId, AnotacionCrearRQ rq);
}
