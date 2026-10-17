package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entities.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de {@link FormulaMedica}. Soporta filtros dinamicos,
 * paginacion y ordenamiento mediante {@link JpaSpecificationExecutor}.
 */
@Repository
public interface FormulaMedicaRepository
        extends JpaRepository<FormulaMedica, Long>, JpaSpecificationExecutor<FormulaMedica> {
}
