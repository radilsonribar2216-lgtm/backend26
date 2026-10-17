package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.models.rq.CitaCrearRQ;
import com.uniminuto.clinica.models.rq.CitaEditarRQ;
import com.uniminuto.clinica.models.rq.CitaEstadoRQ;
import com.uniminuto.clinica.models.rq.CitaFiltroRQ;
import com.uniminuto.clinica.models.rs.CitaRS;
import com.uniminuto.clinica.models.rs.PaginaRS;
import com.uniminuto.clinica.service.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de citas. Solo delega en {@link CitaService}.
 */
@RestController
@RequiredArgsConstructor
public class CitaApiController implements CitaApi {

    /** Servicio de citas. */
    private final CitaService citaService;

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<PaginaRS<CitaRS>> listarCitas(CitaFiltroRQ filtro) {
        return ResponseEntity.ok(citaService.listar(filtro));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<CitaRS> consultarCita(Long id) {
        return ResponseEntity.ok(citaService.consultar(id));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<CitaRS> crearCita(CitaCrearRQ rq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crear(rq));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<CitaRS> editarCita(Long id, CitaEditarRQ rq) {
        return ResponseEntity.ok(citaService.editar(id, rq));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<CitaRS> cambiarEstadoCita(Long id, CitaEstadoRQ rq) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, rq.getEstado()));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<CitaRS> reactivarCita(Long id) {
        return ResponseEntity.ok(citaService.reactivar(id));
    }
}
