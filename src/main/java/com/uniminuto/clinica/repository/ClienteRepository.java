package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a datos de {@link Cliente}.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Lista los clientes activos ordenados por apellidos y nombres.
     *
     * @return clientes activos.
     */
    List<Cliente> findByActivoTrueOrderByApellidosAscNombresAsc();

    /**
     * Verifica si ya existe un cliente con el documento indicado.
     *
     * @param documento documento de identidad.
     * @return true si ya existe.
     */
    boolean existsByDocumento(String documento);
}
