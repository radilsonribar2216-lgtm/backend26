package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.rq.CitaCrearRQ;
import com.uniminuto.clinica.models.rq.CitaEditarRQ;
import com.uniminuto.clinica.models.rq.CitaEstadoRQ;
import com.uniminuto.clinica.models.rq.CitaFiltroRQ;
import com.uniminuto.clinica.models.rs.CitaRS;
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
 * Contrato REST de la gestion de citas medicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/citas")
public interface CitaApi {

    /**
     * Lista citas con filtros, paginacion y ordenamiento.
     *
     * @param filtro parametros de la URL (page, size, sortBy, sortDir, estado, texto...).
     * @return pagina de citas.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PaginaRS<CitaRS>> listarCitas(@ModelAttribute CitaFiltroRQ filtro);

    /**
     * Consulta una cita.
     *
     * @param id identificador de la cita.
     * @return cita.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CitaRS> consultarCita(@PathVariable("id") Long id);

    /**
     * Crea una cita.
     *
     * @param rq datos de la cita.
     * @return cita creada (201).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CitaRS> crearCita(@Valid @RequestBody CitaCrearRQ rq);

    /**
     * Edita fecha, hora, veterinario y motivo de una cita.
     *
     * @param id identificador de la cita.
     * @param rq nuevos datos.
     * @return cita actualizada.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CitaRS> editarCita(@PathVariable("id") Long id, @Valid @RequestBody CitaEditarRQ rq);

    /**
     * Cambia el estado de una cita.
     *
     * @param id identificador de la cita.
     * @param rq nuevo estado.
     * @return cita actualizada.
     */
    @PatchMapping(value = "/{id}/estado", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CitaRS> cambiarEstadoCita(@PathVariable("id") Long id, @Valid @RequestBody CitaEstadoRQ rq);

    /**
     * Reactiva una cita cancelada (operacion explicita).
     *
     * @param id identificador de la cita.
     * @return cita reactivada.
     */
    @PatchMapping(value = "/{id}/reactivar", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CitaRS> reactivarCita(@PathVariable("id") Long id);
}
