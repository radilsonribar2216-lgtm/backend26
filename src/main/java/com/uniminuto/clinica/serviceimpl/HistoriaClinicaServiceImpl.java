package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entities.Anotacion;
import com.uniminuto.clinica.entities.Cita;
import com.uniminuto.clinica.entities.Cliente;
import com.uniminuto.clinica.entities.EstadoCita;
import com.uniminuto.clinica.entities.EstadoHistoriaClinica;
import com.uniminuto.clinica.entities.HistoriaClinica;
import com.uniminuto.clinica.entities.Mascota;
import com.uniminuto.clinica.entities.Veterinario;
import com.uniminuto.clinica.exception.ConflictoNegocioException;
import com.uniminuto.clinica.exception.RecursoNoEncontradoException;
import com.uniminuto.clinica.exception.SolicitudInvalidaException;
import com.uniminuto.clinica.models.rq.AnotacionCrearRQ;
import com.uniminuto.clinica.models.rq.AnotacionFiltroRQ;
import com.uniminuto.clinica.models.rq.HistoriaClinicaCrearRQ;
import com.uniminuto.clinica.models.rq.HistoriaClinicaFiltroRQ;
import com.uniminuto.clinica.models.rs.AnotacionRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaDetalleRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaResumenRS;
import com.uniminuto.clinica.models.rs.PaginaRS;
import com.uniminuto.clinica.repository.AnotacionRepository;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.HistoriaClinicaRepository;
import com.uniminuto.clinica.service.HistoriaClinicaService;
import com.uniminuto.clinica.specification.AnotacionSpecs;
import com.uniminuto.clinica.specification.HistoriaClinicaSpecs;
import com.uniminuto.clinica.util.Mapeador;
import com.uniminuto.clinica.util.PaginacionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Implementacion de {@link HistoriaClinicaService}. Valida la relacion
 * cliente-mascota-cita-veterinario al crear historias y controla que solo las
 * historias abiertas reciban anotaciones.
 */
@Service
@RequiredArgsConstructor
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {

    /** Columnas ordenables del listado de historias. */
    private static final Map<String, String> ORDENABLES_HISTORIA = Map.of(
            "fecha", "fechaHora",
            "mascota", "mascota.nombre",
            "cliente", "mascota.cliente.apellidos",
            "veterinario", "veterinario.apellidos",
            "motivo", "motivoConsulta",
            "estado", "estado");

    /** Columnas ordenables del historico de anotaciones. */
    private static final Map<String, String> ORDENABLES_ANOTACION = Map.of(
            "fecha", "fechaHora",
            "veterinario", "veterinario.apellidos");

    /** Repositorio de historias clinicas. */
    private final HistoriaClinicaRepository historiaRepository;

    /** Repositorio de anotaciones. */
    private final AnotacionRepository anotacionRepository;

    /** Repositorio de citas. */
    private final CitaRepository citaRepository;

    /** Validador de relaciones compartido. */
    private final ValidadorRelaciones validador;

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PaginaRS<HistoriaClinicaResumenRS> listar(HistoriaClinicaFiltroRQ filtro) {
        Pageable pageable = PaginacionUtil.crear(filtro, ORDENABLES_HISTORIA, "fechaHora", Sort.Direction.DESC);
        Page<HistoriaClinica> pagina = historiaRepository.findAll(HistoriaClinicaSpecs.filtrar(filtro), pageable);
        return PaginacionUtil.aRespuesta(pagina, Mapeador::aHistoriaResumenRS);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public HistoriaClinicaDetalleRS consultar(Long id) {
        return Mapeador.aHistoriaDetalleRS(validador.obtenerHistoria(id));
    }

    /**
     * {@inheritDoc}
     * Si la cita no estaba marcada como ATENDIDA, se marca automaticamente en
     * la misma transaccion, porque registrar la historia implica que la
     * consulta se realizo.
     */
    @Override
    @Transactional
    public HistoriaClinicaDetalleRS crear(HistoriaClinicaCrearRQ rq) {
        Cliente cliente = validador.obtenerCliente(rq.getClienteId());
        Mascota mascota = validador.obtenerMascotaDeCliente(rq.getMascotaId(), cliente.getId());
        Cita cita = validador.obtenerCita(rq.getCitaId());

        if (!cita.getMascota().getId().equals(mascota.getId())) {
            throw new SolicitudInvalidaException("La cita seleccionada pertenece a otra mascota.");
        }
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new ConflictoNegocioException(
                    "No se puede registrar una historia clínica para una cita cancelada.");
        }
        if (historiaRepository.existsByCitaId(cita.getId())) {
            throw new ConflictoNegocioException("La cita seleccionada ya tiene una historia clínica registrada.");
        }
        Veterinario veterinario = validador.obtenerVeterinarioDisponible(rq.getVeterinarioId());

        if (cita.getEstado() != EstadoCita.ATENDIDA) {
            cita.setEstado(EstadoCita.ATENDIDA);
            citaRepository.save(cita);
        }

        HistoriaClinica historia = new HistoriaClinica();
        historia.setMascota(mascota);
        historia.setCita(cita);
        historia.setVeterinario(veterinario);
        historia.setFechaHora(rq.getFechaHora());
        historia.setMotivoConsulta(rq.getMotivoConsulta().trim());
        historia.setDiagnostico(rq.getDiagnostico().trim());
        historia.setObservaciones(rq.getObservaciones());
        historia.setEstado(EstadoHistoriaClinica.ABIERTA);
        return Mapeador.aHistoriaDetalleRS(historiaRepository.save(historia));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PaginaRS<AnotacionRS> listarAnotaciones(Long historiaId, AnotacionFiltroRQ filtro) {
        HistoriaClinica historia = validador.obtenerHistoria(historiaId);
        Long soloHistoria = filtro.isSoloHistoria() ? historia.getId() : null;
        Long deMascota = filtro.isSoloHistoria() ? null : historia.getMascota().getId();

        Pageable pageable = PaginacionUtil.crear(filtro, ORDENABLES_ANOTACION, "fechaHora", Sort.Direction.DESC);
        Page<Anotacion> pagina = anotacionRepository.findAll(
                AnotacionSpecs.filtrar(filtro, soloHistoria, deMascota), pageable);
        return PaginacionUtil.aRespuesta(pagina, Mapeador::aAnotacionRS);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public AnotacionRS consultarAnotacion(Long historiaId, Long anotacionId) {
        HistoriaClinica historia = validador.obtenerHistoria(historiaId);
        Anotacion anotacion = anotacionRepository.findById(anotacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La anotacion solicitada no existe."));
        if (!anotacion.getHistoriaClinica().getMascota().getId().equals(historia.getMascota().getId())) {
            throw new RecursoNoEncontradoException("La anotacion solicitada no pertenece al historial de esta mascota.");
        }
        return Mapeador.aAnotacionRS(anotacion);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public AnotacionRS crearAnotacion(Long historiaId, AnotacionCrearRQ rq) {
        HistoriaClinica historia = validador.obtenerHistoria(historiaId);
        if (historia.getEstado() != EstadoHistoriaClinica.ABIERTA) {
            throw new ConflictoNegocioException(
                    "La historia clínica está cerrada y no admite nuevas anotaciones.");
        }
        Veterinario veterinario = validador.obtenerVeterinarioDisponible(rq.getVeterinarioId());

        LocalDateTime fechaHora = rq.getFechaHora();
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
            if (fechaHora.isBefore(historia.getFechaHora())) {
                fechaHora = historia.getFechaHora();
            }
        } else if (fechaHora.isBefore(historia.getFechaHora())) {
            throw new SolicitudInvalidaException(
                    "La fecha de la anotacion no puede ser anterior a la fecha de la consulta.");
        }

        Anotacion anotacion = new Anotacion();
        anotacion.setHistoriaClinica(historia);
        anotacion.setVeterinario(veterinario);
        anotacion.setFechaHora(fechaHora);
        anotacion.setDescripcion(rq.getDescripcion().trim());
        anotacion.setObservacion(rq.getObservacion());
        anotacion.setDiagnosticoEvolucion(rq.getDiagnosticoEvolucion().trim());
        return Mapeador.aAnotacionRS(anotacionRepository.save(anotacion));
    }
}
