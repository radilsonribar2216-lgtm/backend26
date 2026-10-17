package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entities.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso a datos de {@link Veterinario}.
 */
@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    /**
     * Lista los veterinarios activos ordenados por apellidos y nombres.
     *
     * @return veterinarios activos.
     */
    List<Veterinario> findByActivoTrueOrderByApellidosAscNombresAsc();
}
