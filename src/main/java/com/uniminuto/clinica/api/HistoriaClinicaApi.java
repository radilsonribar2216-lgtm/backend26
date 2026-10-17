package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.rq.AnotacionCrearRQ;
import com.uniminuto.clinica.models.rq.AnotacionFiltroRQ;
import com.uniminuto.clinica.models.rq.HistoriaClinicaCrearRQ;
import com.uniminuto.clinica.models.rq.HistoriaClinicaFiltroRQ;
import com.uniminuto.clinica.models.rs.AnotacionRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaDetalleRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaResumenRS;
import com.uniminuto.clinica.models.rs.PaginaRS;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato REST de historias clinicas y anotaciones.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/historias-clinicas")
public interface HistoriaClinicaApi {

    /**
     * Lista historias con filtros, paginacion y ordenamiento.
     *
     * @param filtro parametros de la URL.
     * @return pagina de historias (resumen).
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PaginaRS<HistoriaClinicaResumenRS>> listarHistorias(
            @ModelAttribute HistoriaClinicaFiltroRQ filtro);

    /**
     * Consulta el detalle de una historia.
     *
     * @param id identificador de la historia.
     * @return detalle.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<HistoriaClinicaDetalleRS> consultarHistoria(@PathVariable("id") Long id);

    /**
     * Crea una historia clinica.
     *
     * @param rq datos de la historia.
     * @return historia creada (201).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<HistoriaClinicaDetalleRS> crearHistoria(@Valid @RequestBody HistoriaClinicaCrearRQ rq);

    /**
     * Lista las anotaciones de una historia (o del historico de la mascota).
     *
     * @param historiaId identificador de la historia.
     * @param filtro     parametros de la URL.
     * @return pagina de anotaciones.
     */
    @GetMapping(value = "/{historiaId}/anotaciones", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PaginaRS<AnotacionRS>> listarAnotaciones(
            @PathVariable("historiaId") Long historiaId, @ModelAttribute AnotacionFiltroRQ filtro);

    /**
     * Consulta el detalle de una anotacion.
     *
     * @param historiaId  identificador de la historia.
     * @param anotacionId identificador de la anotacion.
     * @return anotacion.
     */
    @GetMapping(value = "/{historiaId}/anotaciones/{anotacionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AnotacionRS> consultarAnotacion(
            @PathVariable("historiaId") Long historiaId, @PathVariable("anotacionId") Long anotacionId);

    /**
     * Agrega una anotacion a una historia.
     *
     * @param historiaId identificador de la historia.
     * @param rq         datos de la anotacion.
     * @return anotacion creada (201).
     */
    @PostMapping(value = "/{historiaId}/anotaciones", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AnotacionRS> crearAnotacion(
            @PathVariable("historiaId") Long historiaId, @Valid @RequestBody AnotacionCrearRQ rq);
}
