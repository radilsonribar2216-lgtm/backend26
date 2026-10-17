package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entities.Cita;
import com.uniminuto.clinica.entities.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Acceso a datos de {@link Cita}. Extiende {@link JpaSpecificationExecutor}
 * para armar filtros dinamicos con paginacion y ordenamiento.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long>, JpaSpecificationExecutor<Cita> {

    /**
     * Cuenta las citas de un veterinario dentro de un rango de tiempo, ignorando
     * las de un estado dado (normalmente CANCELADA) y una cita puntual (la que se
     * esta editando). Se usa para validar el choque de horarios (+-15 minutos).
     *
     * @param veterinarioId  veterinario a consultar.
     * @param desde          inicio del rango (inclusive).
     * @param hasta          fin del rango (inclusive).
     * @param estadoExcluido estado que no cuenta como ocupado.
     * @param excluirCitaId  id de la cita a ignorar; usar -1 al crear una nueva.
     * @return cantidad de citas en conflicto.
     */
    @Query("select count(c) from Cita c "
            + "where c.veterinario.id = :veterinarioId "
            + "and c.estado <> :estadoExcluido "
            + "and c.id <> :excluirCitaId "
            + "and c.fechaHora between :desde and :hasta")
    long contarCitasVeterinarioEnRango(@Param("veterinarioId") Long veterinarioId,
                                       @Param("desde") LocalDateTime desde,
                                       @Param("hasta") LocalDateTime hasta,
                                       @Param("estadoExcluido") EstadoCita estadoExcluido,
                                       @Param("excluirCitaId") Long excluirCitaId);

    /**
     * Cuenta las citas de una mascota dentro de un rango de tiempo, con las mismas
     * exclusiones que {@link #contarCitasVeterinarioEnRango}. Se usa para impedir
     * dos citas simultaneas de la misma mascota.
     *
     * @param mascotaId      mascota a consultar.
     * @param desde          inicio del rango (inclusive).
     * @param hasta          fin del rango (inclusive).
     * @param estadoExcluido estado que no cuenta como ocupado.
     * @param excluirCitaId  id de la cita a ignorar; usar -1 al crear una nueva.
     * @return cantidad de citas en conflicto.
     */
    @Query("select count(c) from Cita c "
            + "where c.mascota.id = :mascotaId "
            + "and c.estado <> :estadoExcluido "
            + "and c.id <> :excluirCitaId "
            + "and c.fechaHora between :desde and :hasta")
    long contarCitasMascotaEnRango(@Param("mascotaId") Long mascotaId,
                                   @Param("desde") LocalDateTime desde,
                                   @Param("hasta") LocalDateTime hasta,
                                   @Param("estadoExcluido") EstadoCita estadoExcluido,
                                   @Param("excluirCitaId") Long excluirCitaId);
}
