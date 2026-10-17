package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.HistoriaClinicaApi;
import com.uniminuto.clinica.models.rq.AnotacionCrearRQ;
import com.uniminuto.clinica.models.rq.AnotacionFiltroRQ;
import com.uniminuto.clinica.models.rq.HistoriaClinicaCrearRQ;
import com.uniminuto.clinica.models.rq.HistoriaClinicaFiltroRQ;
import com.uniminuto.clinica.models.rs.AnotacionRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaDetalleRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaResumenRS;
import com.uniminuto.clinica.models.rs.PaginaRS;
import com.uniminuto.clinica.service.HistoriaClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de historias clinicas y anotaciones. Solo delega en
 * {@link HistoriaClinicaService}.
 */
@RestController
@RequiredArgsConstructor
public class HistoriaClinicaApiController implements HistoriaClinicaApi {

    /** Servicio de historias clinicas. */
    private final HistoriaClinicaService historiaService;

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<PaginaRS<HistoriaClinicaResumenRS>> listarHistorias(HistoriaClinicaFiltroRQ filtro) {
        return ResponseEntity.ok(historiaService.listar(filtro));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<HistoriaClinicaDetalleRS> consultarHistoria(Long id) {
        return ResponseEntity.ok(historiaService.consultar(id));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<HistoriaClinicaDetalleRS> crearHistoria(HistoriaClinicaCrearRQ rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historiaService.crear(rq));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<PaginaRS<AnotacionRS>> listarAnotaciones(Long historiaId, AnotacionFiltroRQ filtro) {
        return ResponseEntity.ok(historiaService.listarAnotaciones(historiaId, filtro));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<AnotacionRS> consultarAnotacion(Long historiaId, Long anotacionId) {
        return ResponseEntity.ok(historiaService.consultarAnotacion(historiaId, anotacionId));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<AnotacionRS> crearAnotacion(Long historiaId, AnotacionCrearRQ rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historiaService.crearAnotacion(historiaId, rq));
    }
}
