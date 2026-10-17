package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.rq.FormulaCrearRQ;
import com.uniminuto.clinica.models.rq.FormulaEditarRQ;
import com.uniminuto.clinica.models.rq.FormulaFiltroRQ;
import com.uniminuto.clinica.models.rs.FormulaDetalleRS;
import com.uniminuto.clinica.models.rs.FormulaResumenRS;
import com.uniminuto.clinica.models.rs.PaginaRS;

/**
 * Casos de uso de la gestion de formulas medicas.
 */
public interface FormulaMedicaService {

    /**
     * Lista formulas con filtros, paginacion y ordenamiento. Por defecto ordena
     * por fecha de creacion descendente.
     *
     * @param filtro filtros y parametros de paginacion.
     * @return pagina de formulas (informacion resumida).
     */
    PaginaRS<FormulaResumenRS> listar(FormulaFiltroRQ filtro);

    /**
     * Consulta el detalle completo de una formula.
     *
     * @param id identificador de la formula.
     * @return detalle de la formula.
     */
    FormulaDetalleRS consultar(Long id);

    /**
     * Registra una formula validando mascota, cliente, veterinario, consulta y fechas.
     *
     * @param rq datos de la formula.
     * @return formula creada.
     */
    FormulaDetalleRS crear(FormulaCrearRQ rq);

    /**
     * Modifica los datos permitidos de una formula activa.
     *
     * @param id identificador de la formula.
     * @param rq nuevos datos.
     * @return formula actualizada.
     */
    FormulaDetalleRS editar(Long id, FormulaEditarRQ rq);

    /**
     * Activa o inactiva una formula (en lugar de eliminarla fisicamente).
     *
     * @param id     identificador de la formula.
     * @param activo true para activar, false para inactivar.
     * @return formula actualizada.
     */
    FormulaDetalleRS cambiarActivo(Long id, boolean activo);
}
