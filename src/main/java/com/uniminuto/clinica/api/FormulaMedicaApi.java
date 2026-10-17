package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.rq.FormulaCrearRQ;
import com.uniminuto.clinica.models.rq.FormulaEditarRQ;
import com.uniminuto.clinica.models.rq.FormulaFiltroRQ;
import com.uniminuto.clinica.models.rs.FormulaDetalleRS;
import com.uniminuto.clinica.models.rs.FormulaResumenRS;
import com.uniminuto.clinica.models.rs.PaginaRS;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato REST de la gestion de formulas medicas. No existe DELETE: las
 * formulas se inactivan para preservar la informacion clinica.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formulas")
public interface FormulaMedicaApi {

    /**
     * Lista formulas con filtros, paginacion y ordenamiento.
     *
     * @param filtro parametros de la URL.
     * @return pagina de formulas (resumen).
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PaginaRS<FormulaResumenRS>> listarFormulas(@ModelAttribute FormulaFiltroRQ filtro);

    /**
     * Consulta el detalle de una formula.
     *
     * @param id identificador de la formula.
     * @return detalle.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FormulaDetalleRS> consultarFormula(@PathVariable("id") Long id);

    /**
     * Crea una formula.
     *
     * @param rq datos de la formula.
     * @return formula creada (201).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FormulaDetalleRS> crearFormula(@Valid @RequestBody FormulaCrearRQ rq);

    /**
     * Edita los datos permitidos de una formula activa.
     *
     * @param id identificador de la formula.
     * @param rq nuevos datos.
     * @return formula actualizada.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FormulaDetalleRS> editarFormula(@PathVariable("id") Long id, @Valid @RequestBody FormulaEditarRQ rq);

    /**
     * Inactiva una formula (en lugar de eliminarla).
     *
     * @param id identificador de la formula.
     * @return formula inactivada.
     */
    @PatchMapping(value = "/{id}/inactivar", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FormulaDetalleRS> inactivarFormula(@PathVariable("id") Long id);

    /**
     * Activa una formula inactiva.
     *
     * @param id identificador de la formula.
     * @return formula activada.
     */
    @PatchMapping(value = "/{id}/activar", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FormulaDetalleRS> activarFormula(@PathVariable("id") Long id);
}
