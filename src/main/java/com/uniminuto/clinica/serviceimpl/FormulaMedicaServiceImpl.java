package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entities.FormulaMedica;
import com.uniminuto.clinica.entities.HistoriaClinica;
import com.uniminuto.clinica.entities.Mascota;
import com.uniminuto.clinica.entities.Veterinario;
import com.uniminuto.clinica.exception.ConflictoNegocioException;
import com.uniminuto.clinica.exception.RecursoNoEncontradoException;
import com.uniminuto.clinica.exception.SolicitudInvalidaException;
import com.uniminuto.clinica.models.rq.FormulaCrearRQ;
import com.uniminuto.clinica.models.rq.FormulaEditarRQ;
import com.uniminuto.clinica.models.rq.FormulaFiltroRQ;
import com.uniminuto.clinica.models.rs.FormulaDetalleRS;
import com.uniminuto.clinica.models.rs.FormulaResumenRS;
import com.uniminuto.clinica.models.rs.PaginaRS;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.specification.FormulaMedicaSpecs;
import com.uniminuto.clinica.util.Mapeador;
import com.uniminuto.clinica.util.PaginacionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Implementacion de {@link FormulaMedicaService}. Garantiza que la formula
 * pertenezca a una mascota existente con cliente registrado, que la consulta
 * asociada sea de esa misma mascota y que la fecha de emision sea coherente.
 */
@Service
@RequiredArgsConstructor
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    /** Columnas ordenables del listado: nombre expuesto -> ruta en la entidad. */
    private static final Map<String, String> ORDENABLES = Map.of(
            "fechaCreacion", "createdAt",
            "fechaEmision", "fechaEmision",
            "mascota", "mascota.nombre",
            "cliente", "mascota.cliente.apellidos",
            "veterinario", "veterinario.apellidos",
            "medicamento", "medicamento",
            "duracion", "duracionDias",
            "estado", "activo");

    /** Repositorio de formulas medicas. */
    private final FormulaMedicaRepository formulaRepository;

    /** Validador de relaciones compartido. */
    private final ValidadorRelaciones validador;

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PaginaRS<FormulaResumenRS> listar(FormulaFiltroRQ filtro) {
        Pageable pageable = PaginacionUtil.crear(filtro, ORDENABLES, "createdAt", Sort.Direction.DESC);
        Page<FormulaMedica> pagina = formulaRepository.findAll(FormulaMedicaSpecs.filtrar(filtro), pageable);
        return PaginacionUtil.aRespuesta(pagina, Mapeador::aFormulaResumenRS);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public FormulaDetalleRS consultar(Long id) {
        return Mapeador.aFormulaDetalleRS(obtenerFormula(id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public FormulaDetalleRS crear(FormulaCrearRQ rq) {
        Mascota mascota = validador.obtenerMascota(rq.getMascotaId());
        if (!mascota.getCliente().isActivo()) {
            throw new ConflictoNegocioException("La mascota debe pertenecer a un cliente registrado y activo.");
        }
        Veterinario veterinario = validador.obtenerVeterinarioDisponible(rq.getVeterinarioId());
        HistoriaClinica historia = validador.obtenerHistoria(rq.getHistoriaClinicaId());
        if (!historia.getMascota().getId().equals(mascota.getId())) {
            throw new SolicitudInvalidaException("La consulta seleccionada pertenece a otra mascota.");
        }
        validarFechaEmision(rq.getFechaEmision(), historia);
        validarDosisYDuracion(rq.getDosisCantidad(), rq.getDuracionDias());

        FormulaMedica formula = new FormulaMedica();
        formula.setMascota(mascota);
        formula.setHistoriaClinica(historia);
        formula.setVeterinario(veterinario);
        formula.setActivo(true);
        aplicarDatos(formula, rq.getFechaEmision(), rq.getMedicamento(), rq.getDosisCantidad(),
                rq.getDosisUnidad(), rq.getFrecuencia(), rq.getDuracionDias(), rq.getIndicaciones());
        return Mapeador.aFormulaDetalleRS(formulaRepository.save(formula));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public FormulaDetalleRS editar(Long id, FormulaEditarRQ rq) {
        FormulaMedica formula = obtenerFormula(id);
        if (!formula.isActivo()) {
            throw new ConflictoNegocioException(
                    "La fórmula está inactiva y no puede modificarse. Actívela primero si desea editarla.");
        }
        validarFechaEmision(rq.getFechaEmision(), formula.getHistoriaClinica());
        validarDosisYDuracion(rq.getDosisCantidad(), rq.getDuracionDias());
        aplicarDatos(formula, rq.getFechaEmision(), rq.getMedicamento(), rq.getDosisCantidad(),
                rq.getDosisUnidad(), rq.getFrecuencia(), rq.getDuracionDias(), rq.getIndicaciones());
        return Mapeador.aFormulaDetalleRS(formulaRepository.save(formula));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public FormulaDetalleRS cambiarActivo(Long id, boolean activo) {
        FormulaMedica formula = obtenerFormula(id);
        formula.setActivo(activo);
        return Mapeador.aFormulaDetalleRS(formulaRepository.save(formula));
    }

    /**
     * Busca una formula o lanza 404.
     *
     * @param id identificador de la formula.
     * @return formula encontrada.
     */
    private FormulaMedica obtenerFormula(Long id) {
        return formulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La fórmula solicitada no existe."));
    }

    /**
     * La emision no puede ser anterior a la fecha de la consulta asociada.
     *
     * @param fechaEmision fecha y hora de emision.
     * @param historia     consulta asociada.
     */
    private void validarFechaEmision(LocalDateTime fechaEmision, HistoriaClinica historia) {
        if (fechaEmision.isBefore(historia.getFechaHora())) {
            throw new SolicitudInvalidaException(
                    "La fecha de emisión no puede ser anterior a la fecha de la consulta asociada.");
        }
    }

    /**
     * Revalida en el servicio que dosis y duracion sean valores validos.
     *
     * @param dosis    cantidad de la dosis.
     * @param duracion duracion en dias.
     */
    private void validarDosisYDuracion(BigDecimal dosis, Integer duracion) {
        if (dosis == null || dosis.signum() <= 0) {
            throw new SolicitudInvalidaException("La dosis debe ser un valor mayor que cero.");
        }
        if (duracion == null || duracion < 1 || duracion > 365) {
            throw new SolicitudInvalidaException("La duración del tratamiento debe estar entre 1 y 365 días.");
        }
    }

    /**
     * Copia a la entidad los datos editables de la formula.
     *
     * @param formula      entidad destino.
     * @param fechaEmision fecha y hora de emision.
     * @param medicamento  medicamento.
     * @param dosis        cantidad de la dosis.
     * @param unidad       unidad de la dosis.
     * @param frecuencia   frecuencia.
     * @param duracion     duracion en dias.
     * @param indicaciones indicaciones.
     */
    private void aplicarDatos(FormulaMedica formula, LocalDateTime fechaEmision, String medicamento,
                              BigDecimal dosis, String unidad, String frecuencia,
                              Integer duracion, String indicaciones) {
        formula.setFechaEmision(fechaEmision);
        formula.setMedicamento(medicamento.trim());
        formula.setDosisCantidad(dosis);
        formula.setDosisUnidad(unidad.trim());
        formula.setFrecuencia(frecuencia.trim());
        formula.setDuracionDias(duracion);
        formula.setIndicaciones(indicaciones.trim());
    }
}
