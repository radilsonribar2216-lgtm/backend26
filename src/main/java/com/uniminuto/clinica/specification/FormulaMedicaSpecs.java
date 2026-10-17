package com.uniminuto.clinica.specification;

import com.uniminuto.clinica.entities.FormulaMedica;
import com.uniminuto.clinica.models.rq.FormulaFiltroRQ;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Filtros dinamicos para el listado de formulas medicas.
 */
public final class FormulaMedicaSpecs {

    private FormulaMedicaSpecs() {
    }

    /**
     * Construye la especificacion a partir de los filtros recibidos.
     *
     * @param f filtros (todos opcionales).
     * @return especificacion para {@code findAll(spec, pageable)}.
     */
    public static Specification<FormulaMedica> filtrar(FormulaFiltroRQ f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (f.getActivo() != null) {
                p.add(cb.equal(SpecUtil.<Boolean>ruta(root, "activo"), f.getActivo()));
            }
            if (f.getClienteId() != null) {
                p.add(cb.equal(SpecUtil.<Long>ruta(root, "mascota", "cliente", "id"), f.getClienteId()));
            }
            if (f.getMascotaId() != null) {
                p.add(cb.equal(SpecUtil.<Long>ruta(root, "mascota", "id"), f.getMascotaId()));
            }
            if (f.getVeterinarioId() != null) {
                p.add(cb.equal(SpecUtil.<Long>ruta(root, "veterinario", "id"), f.getVeterinarioId()));
            }
            if (f.getFechaDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(SpecUtil.<LocalDateTime>ruta(root, "fechaEmision"),
                        SpecUtil.inicioDelDia(f.getFechaDesde())));
            }
            if (f.getFechaHasta() != null) {
                p.add(cb.lessThan(SpecUtil.<LocalDateTime>ruta(root, "fechaEmision"),
                        SpecUtil.inicioDiaSiguiente(f.getFechaHasta())));
            }
            if (SpecUtil.hayTexto(f.getTexto())) {
                String t = f.getTexto();
                p.add(cb.or(
                        SpecUtil.contiene(cb, SpecUtil.<String>ruta(root, "medicamento"), t),
                        SpecUtil.contiene(cb, SpecUtil.<String>ruta(root, "mascota", "nombre"), t),
                        SpecUtil.contiene(cb, SpecUtil.nombreCompleto(cb,
                                SpecUtil.<String>ruta(root, "mascota", "cliente", "nombres"),
                                SpecUtil.<String>ruta(root, "mascota", "cliente", "apellidos")), t),
                        SpecUtil.contiene(cb, SpecUtil.nombreCompleto(cb,
                                SpecUtil.<String>ruta(root, "veterinario", "nombres"),
                                SpecUtil.<String>ruta(root, "veterinario", "apellidos")), t)));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
