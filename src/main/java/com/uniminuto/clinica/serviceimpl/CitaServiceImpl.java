package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entities.Cita;
import com.uniminuto.clinica.entities.Cliente;
import com.uniminuto.clinica.entities.EstadoCita;
import com.uniminuto.clinica.entities.Mascota;
import com.uniminuto.clinica.entities.Veterinario;
import com.uniminuto.clinica.exception.ConflictoNegocioException;
import com.uniminuto.clinica.exception.SolicitudInvalidaException;
import com.uniminuto.clinica.models.rq.CitaCrearRQ;
import com.uniminuto.clinica.models.rq.CitaEditarRQ;
import com.uniminuto.clinica.models.rq.CitaFiltroRQ;
import com.uniminuto.clinica.models.rs.CitaRS;
import com.uniminuto.clinica.models.rs.PaginaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.specification.CitaSpecs;
import com.uniminuto.clinica.util.Mapeador;
import com.uniminuto.clinica.util.PaginacionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementacion de {@link CitaService}. Concentra las reglas de negocio de
 * las citas: existencia y pertenencia de cliente/mascota/veterinario, fechas
 * futuras, cruces de horario (+-15 minutos) y transiciones de estado.
 */
@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    /** Minutos de margen antes y despues de una cita para detectar cruces. */
    private static final long MARGEN_MINUTOS = 15;

    /** Valor de "cita a excluir" cuando se crea una cita nueva (ningun id es -1). */
    private static final long SIN_EXCLUSION = -1L;

    /** Columnas ordenables: nombre expuesto -> ruta en la entidad. */
    private static final Map<String, String> ORDENABLES = Map.of(
            "fecha", "fechaHora",
            "hora", "fechaHora",
            "cliente", "mascota.cliente.apellidos",
            "mascota", "mascota.nombre",
            "veterinario", "veterinario.apellidos",
            "estado", "estado");

    /** Transiciones de estado permitidas (CANCELADA solo sale con "reactivar"). */
    private static final Map<EstadoCita, Set<EstadoCita>> TRANSICIONES = new EnumMap<>(EstadoCita.class);

    static {
        TRANSICIONES.put(EstadoCita.PENDIENTE, EnumSet.of(EstadoCita.CONFIRMADA, EstadoCita.PROGRAMADA,
                EstadoCita.ATENDIDA, EstadoCita.CANCELADA));
        TRANSICIONES.put(EstadoCita.CONFIRMADA, EnumSet.of(EstadoCita.PROGRAMADA,
                EstadoCita.ATENDIDA, EstadoCita.CANCELADA));
        TRANSICIONES.put(EstadoCita.PROGRAMADA, EnumSet.of(EstadoCita.CONFIRMADA,
                EstadoCita.ATENDIDA, EstadoCita.CANCELADA));
        TRANSICIONES.put(EstadoCita.ATENDIDA, EnumSet.noneOf(EstadoCita.class));
        TRANSICIONES.put(EstadoCita.CANCELADA, EnumSet.noneOf(EstadoCita.class));
    }

    /** Repositorio de citas. */
    private final CitaRepository citaRepository;

    /** Validador de relaciones compartido. */
    private final ValidadorRelaciones validador;

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PaginaRS<CitaRS> listar(CitaFiltroRQ filtro) {
        Pageable pageable = PaginacionUtil.crear(filtro, ORDENABLES, "fechaHora", Sort.Direction.DESC);
        Page<Cita> pagina = citaRepository.findAll(CitaSpecs.filtrar(filtro), pageable);
        return PaginacionUtil.aRespuesta(pagina, Mapeador::aCitaRS);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public CitaRS consultar(Long id) {
        return Mapeador.aCitaRS(validador.obtenerCita(id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CitaRS crear(CitaCrearRQ rq) {
        Cliente cliente = validador.obtenerCliente(rq.getClienteId());
        Mascota mascota = validador.obtenerMascotaDeCliente(rq.getMascotaId(), cliente.getId());
        Veterinario veterinario = validador.obtenerVeterinarioDisponible(rq.getVeterinarioId());

        EstadoCita estado = rq.getEstado() == null ? EstadoCita.PENDIENTE : rq.getEstado();
        if (estado == EstadoCita.ATENDIDA || estado == EstadoCita.CANCELADA) {
            throw new SolicitudInvalidaException(
                    "Una cita nueva solo puede crearse como Pendiente, Confirmada o Programada.");
        }

        LocalDateTime fechaHora = LocalDateTime.of(rq.getFecha(), rq.getHora());
        validarFechaFutura(fechaHora, "No se pueden registrar citas en fechas u horas anteriores a la actual.");
        validarDisponibilidad(veterinario.getId(), mascota.getId(), fechaHora, SIN_EXCLUSION);

        Cita cita = new Cita();
        cita.setMascota(mascota);
        cita.setVeterinario(veterinario);
        cita.setFechaHora(fechaHora);
        cita.setMotivo(rq.getMotivo().trim());
        cita.setEstado(estado);
        return Mapeador.aCitaRS(citaRepository.save(cita));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CitaRS editar(Long id, CitaEditarRQ rq) {
        Cita cita = validador.obtenerCita(id);
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new ConflictoNegocioException(
                    "La cita está cancelada y no puede modificarse. Si desea retomarla use la opción Reactivar.");
        }
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new ConflictoNegocioException(
                    "La cita ya fue atendida y su información histórica no puede modificarse.");
        }

        Veterinario veterinario = validador.obtenerVeterinarioDisponible(rq.getVeterinarioId());
        LocalDateTime fechaHora = LocalDateTime.of(rq.getFecha(), rq.getHora());
        validarFechaFutura(fechaHora, "La cita no puede reprogramarse a una fecha u hora anterior a la actual.");
        validarDisponibilidad(veterinario.getId(), cita.getMascota().getId(), fechaHora, cita.getId());

        cita.setVeterinario(veterinario);
        cita.setFechaHora(fechaHora);
        cita.setMotivo(rq.getMotivo().trim());
        return Mapeador.aCitaRS(citaRepository.save(cita));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CitaRS cambiarEstado(Long id, EstadoCita nuevo) {
        Cita cita = validador.obtenerCita(id);
        EstadoCita actual = cita.getEstado();
        if (actual == EstadoCita.CANCELADA) {
            throw new ConflictoNegocioException(
                    "La cita está cancelada. Para retomarla use la operación Reactivar.");
        }
        if (!TRANSICIONES.get(actual).contains(nuevo)) {
            throw new ConflictoNegocioException("No es posible cambiar una cita de estado "
                    + etiqueta(actual) + " a " + etiqueta(nuevo) + ".");
        }
        cita.setEstado(nuevo);
        return Mapeador.aCitaRS(citaRepository.save(cita));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CitaRS reactivar(Long id) {
        Cita cita = validador.obtenerCita(id);
        if (cita.getEstado() != EstadoCita.CANCELADA) {
            throw new ConflictoNegocioException("Solo se pueden reactivar citas canceladas.");
        }
        validador.obtenerVeterinarioDisponible(cita.getVeterinario().getId());
        validarFechaFutura(cita.getFechaHora(),
                "La cita cancelada ya pasó; cree una nueva cita en una fecha futura.");
        validarDisponibilidad(cita.getVeterinario().getId(), cita.getMascota().getId(),
                cita.getFechaHora(), cita.getId());
        cita.setEstado(EstadoCita.PENDIENTE);
        return Mapeador.aCitaRS(citaRepository.save(cita));
    }

    /**
     * Verifica que la fecha y hora no sean anteriores al momento actual.
     *
     * @param fechaHora fecha y hora a validar.
     * @param mensaje   mensaje de error si no se cumple.
     */
    private void validarFechaFutura(LocalDateTime fechaHora, String mensaje) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new SolicitudInvalidaException(mensaje);
        }
    }

    /**
     * Verifica que ni el veterinario ni la mascota tengan otra cita (no
     * cancelada) dentro de {@value #MARGEN_MINUTOS} minutos antes o despues.
     *
     * @param veterinarioId identificador del veterinario.
     * @param mascotaId     identificador de la mascota.
     * @param fechaHora     fecha y hora de la cita a registrar.
     * @param excluirCitaId cita a ignorar (la que se edita) o {@link #SIN_EXCLUSION}.
     */
    private void validarDisponibilidad(Long veterinarioId, Long mascotaId,
                                       LocalDateTime fechaHora, long excluirCitaId) {
        LocalDateTime desde = fechaHora.minusMinutes(MARGEN_MINUTOS);
        LocalDateTime hasta = fechaHora.plusMinutes(MARGEN_MINUTOS);

        if (citaRepository.contarCitasVeterinarioEnRango(
                veterinarioId, desde, hasta, EstadoCita.CANCELADA, excluirCitaId) > 0) {
            throw new ConflictoNegocioException("El veterinario no está disponible en ese horario: "
                    + "ya tiene una cita dentro de los " + MARGEN_MINUTOS + " minutos anteriores o posteriores.");
        }
        if (citaRepository.contarCitasMascotaEnRango(
                mascotaId, desde, hasta, EstadoCita.CANCELADA, excluirCitaId) > 0) {
            throw new ConflictoNegocioException("La mascota ya tiene una cita en ese horario. "
                    + "Una mascota no puede tener dos citas simultáneas.");
        }
    }

    /**
     * Nombre legible de un estado para los mensajes al usuario.
     *
     * @param estado estado de la cita.
     * @return etiqueta en espanol.
     */
    private String etiqueta(EstadoCita estado) {
        return switch (estado) {
            case PENDIENTE -> "Pendiente";
            case CONFIRMADA -> "Confirmada";
            case ATENDIDA -> "Atendida";
            case CANCELADA -> "Cancelada";
            case PROGRAMADA -> "Programada";
        };
    }
}
