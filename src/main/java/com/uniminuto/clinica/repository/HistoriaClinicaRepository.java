package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entities.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de {@link HistoriaClinica}. Soporta filtros dinamicos,
 * paginacion y ordenamiento mediante {@link JpaSpecificationExecutor}.
 */
@Repository
public interface HistoriaClinicaRepository
        extends JpaRepository<HistoriaClinica, Long>, JpaSpecificationExecutor<HistoriaClinica> {

    /**
     * Verifica si una cita ya genero una historia clinica.
     *
     * @param citaId identificador de la cita.
     * @return true si ya existe una historia para esa cita.
     */
    boolean existsByCitaId(Long citaId);
}
