package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.rs.ClienteRS;
import com.uniminuto.clinica.models.rs.MascotaRS;
import com.uniminuto.clinica.models.rs.VeterinarioRS;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Contrato REST de los catalogos de apoyo: clientes, mascotas y veterinarios.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface CatalogoApi {

    /**
     * Lista los clientes activos.
     *
     * @return clientes.
     */
    @GetMapping(value = "/clientes", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ClienteRS>> listarClientes();

    /**
     * Consulta un cliente.
     *
     * @param id identificador del cliente.
     * @return cliente.
     */
    @GetMapping(value = "/clientes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ClienteRS> consultarCliente(@PathVariable("id") Long id);

    /**
     * Lista las mascotas de un cliente.
     *
     * @param clienteId identificador del cliente.
     * @return mascotas del cliente.
     */
    @GetMapping(value = "/clientes/{clienteId}/mascotas", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<MascotaRS>> listarMascotasDeCliente(@PathVariable("clienteId") Long clienteId);

    /**
     * Consulta una mascota.
     *
     * @param id identificador de la mascota.
     * @return mascota.
     */
    @GetMapping(value = "/mascotas/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MascotaRS> consultarMascota(@PathVariable("id") Long id);

    /**
     * Lista los veterinarios activos.
     *
     * @return veterinarios.
     */
    @GetMapping(value = "/veterinarios", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<VeterinarioRS>> listarVeterinarios();
}
