package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entities.Anotacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de {@link Anotacion}.
 */
@Repository
public interface AnotacionRepository
        extends JpaRepository<Anotacion, Long>, JpaSpecificationExecutor<Anotacion> {

    /**
     * Lista paginada de las anotaciones de una historia clinica.
     *
     * @param historiaClinicaId identificador de la historia clinica.
     * @param pageable          paginacion y ordenamiento (por fecha).
     * @return pagina de anotaciones.
     */
    Page<Anotacion> findByHistoriaClinicaId(Long historiaClinicaId, Pageable pageable);

    /**
     * Lista paginada de las anotaciones de todas las historias de una mascota
     * (historico completo de la mascota).
     *
     * @param mascotaId identificador de la mascota.
     * @param pageable  paginacion y ordenamiento (por fecha).
     * @return pagina de anotaciones.
     */
    Page<Anotacion> findByHistoriaClinicaMascotaId(Long mascotaId, Pageable pageable);
}
