package com.uniminuto.clinica.specification;

import com.uniminuto.clinica.entities.Anotacion;
import com.uniminuto.clinica.models.rq.AnotacionFiltroRQ;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Filtros dinamicos para el historico de anotaciones.
 */
public final class AnotacionSpecs {

    private AnotacionSpecs() {
    }

    /**
     * Construye la especificacion. Debe indicarse la historia O la mascota
     * (la otra en null) para acotar el alcance del historico.
     *
     * @param f          filtros (todos opcionales).
     * @param historiaId si no es null, solo anotaciones de esa historia.
     * @param mascotaId  si no es null, anotaciones de todas las historias de esa mascota.
     * @return especificacion para {@code findAll(spec, pageable)}.
     */
    public static Specification<Anotacion> filtrar(AnotacionFiltroRQ f, Long historiaId, Long mascotaId) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (historiaId != null) {
                p.add(cb.equal(SpecUtil.<Long>ruta(root, "historiaClinica", "id"), historiaId));
            }
            if (mascotaId != null) {
                p.add(cb.equal(SpecUtil.<Long>ruta(root, "historiaClinica", "mascota", "id"), mascotaId));
            }
            if (f.getVeterinarioId() != null) {
                p.add(cb.equal(SpecUtil.<Long>ruta(root, "veterinario", "id"), f.getVeterinarioId()));
            }
            if (f.getFechaDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(SpecUtil.<LocalDateTime>ruta(root, "fechaHora"),
                        SpecUtil.inicioDelDia(f.getFechaDesde())));
            }
            if (f.getFechaHasta() != null) {
                p.add(cb.lessThan(SpecUtil.<LocalDateTime>ruta(root, "fechaHora"),
                        SpecUtil.inicioDiaSiguiente(f.getFechaHasta())));
            }
            if (SpecUtil.hayTexto(f.getTexto())) {
                String t = f.getTexto();
                p.add(cb.or(
                        SpecUtil.contiene(cb, SpecUtil.<String>ruta(root, "descripcion"), t),
                        SpecUtil.contiene(cb, SpecUtil.<String>ruta(root, "observacion"), t),
                        SpecUtil.contiene(cb, SpecUtil.<String>ruta(root, "diagnosticoEvolucion"), t)));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
