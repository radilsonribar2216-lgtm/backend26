package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entities.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de {@link Mascota}.
 */
@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    /**
     * Lista las mascotas activas de un cliente (para el combo dependiente).
     *
     * @param clienteId identificador del cliente.
     * @return mascotas activas del cliente.
     */
    List<Mascota> findByClienteIdAndActivoTrueOrderByNombreAsc(Long clienteId);

    /**
     * Busca una mascota solo si pertenece al cliente indicado.
     *
     * @param id        identificador de la mascota.
     * @param clienteId identificador del cliente.
     * @return la mascota si existe y pertenece al cliente.
     */
    Optional<Mascota> findByIdAndClienteId(Long id, Long clienteId);
}
