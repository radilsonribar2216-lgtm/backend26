package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.models.rq.FormulaCrearRQ;
import com.uniminuto.clinica.models.rq.FormulaEditarRQ;
import com.uniminuto.clinica.models.rq.FormulaFiltroRQ;
import com.uniminuto.clinica.models.rs.FormulaDetalleRS;
import com.uniminuto.clinica.models.rs.FormulaResumenRS;
import com.uniminuto.clinica.models.rs.PaginaRS;
import com.uniminuto.clinica.service.FormulaMedicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de formulas medicas. Solo delega en {@link FormulaMedicaService}.
 */
@RestController
@RequiredArgsConstructor
public class FormulaMedicaApiController implements FormulaMedicaApi {

    /** Servicio de formulas medicas. */
    private final FormulaMedicaService formulaService;

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<PaginaRS<FormulaResumenRS>> listarFormulas(FormulaFiltroRQ filtro) {
        return ResponseEntity.ok(formulaService.listar(filtro));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<FormulaDetalleRS> consultarFormula(Long id) {
        return ResponseEntity.ok(formulaService.consultar(id));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<FormulaDetalleRS> crearFormula(FormulaCrearRQ rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formulaService.crear(rq));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<FormulaDetalleRS> editarFormula(Long id, FormulaEditarRQ rq) {
        return ResponseEntity.ok(formulaService.editar(id, rq));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<FormulaDetalleRS> inactivarFormula(Long id) {
        return ResponseEntity.ok(formulaService.cambiarActivo(id, false));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<FormulaDetalleRS> activarFormula(Long id) {
        return ResponseEntity.ok(formulaService.cambiarActivo(id, true));
    }
}
