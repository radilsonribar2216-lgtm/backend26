package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entities.EstadoCita;
import com.uniminuto.clinica.models.rq.CitaCrearRQ;
import com.uniminuto.clinica.models.rq.CitaEditarRQ;
import com.uniminuto.clinica.models.rq.CitaFiltroRQ;
import com.uniminuto.clinica.models.rs.CitaRS;
import com.uniminuto.clinica.models.rs.PaginaRS;

/**
 * Casos de uso de la gestion de citas medicas.
 */
public interface CitaService {

    /**
     * Lista citas con filtros, paginacion y ordenamiento. Por defecto ordena por
     * fecha y hora descendente.
     *
     * @param filtro filtros y parametros de paginacion.
     * @return pagina de citas.
     */
    PaginaRS<CitaRS> listar(CitaFiltroRQ filtro);

    /**
     * Consulta una cita.
     *
     * @param id identificador de la cita.
     * @return datos de la cita.
     */
    CitaRS consultar(Long id);

    /**
     * Registra una cita validando todas las reglas de negocio.
     *
     * @param rq datos de la cita.
     * @return cita creada.
     */
    CitaRS crear(CitaCrearRQ rq);

    /**
     * Modifica fecha, hora, veterinario y motivo de una cita, segun su estado.
     *
     * @param id identificador de la cita.
     * @param rq nuevos datos.
     * @return cita actualizada.
     */
    CitaRS editar(Long id, CitaEditarRQ rq);

    /**
     * Cambia el estado de una cita respetando las transiciones permitidas.
     *
     * @param id    identificador de la cita.
     * @param nuevo estado deseado.
     * @return cita actualizada.
     */
    CitaRS cambiarEstado(Long id, EstadoCita nuevo);

    /**
     * Reactiva (explicitamente) una cita cancelada, dejandola en PENDIENTE.
     *
     * @param id identificador de la cita.
     * @return cita reactivada.
     */
    CitaRS reactivar(Long id);
}
