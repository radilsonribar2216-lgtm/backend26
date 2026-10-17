package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.rs.ClienteRS;
import com.uniminuto.clinica.models.rs.MascotaRS;
import com.uniminuto.clinica.models.rs.VeterinarioRS;

import java.util.List;

/**
 * Consultas de apoyo (catalogos) usadas por los combos del frontend:
 * clientes, mascotas y veterinarios.
 */
public interface CatalogoService {

    /**
     * Lista los clientes activos.
     *
     * @return clientes ordenados por apellidos.
     */
    List<ClienteRS> listarClientes();

    /**
     * Consulta un cliente.
     *
     * @param id identificador del cliente.
     * @return datos del cliente.
     */
    ClienteRS consultarCliente(Long id);

    /**
     * Lista las mascotas activas de un cliente (combo dependiente).
     *
     * @param clienteId identificador del cliente.
     * @return mascotas del cliente.
     */
    List<MascotaRS> listarMascotasDeCliente(Long clienteId);

    /**
     * Consulta una mascota.
     *
     * @param id identificador de la mascota.
     * @return datos de la mascota.
     */
    MascotaRS consultarMascota(Long id);

    /**
     * Lista los veterinarios activos.
     *
     * @return veterinarios ordenados por apellidos.
     */
    List<VeterinarioRS> listarVeterinarios();
}
