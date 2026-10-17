package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entities.Cita;
import com.uniminuto.clinica.entities.Cliente;
import com.uniminuto.clinica.entities.HistoriaClinica;
import com.uniminuto.clinica.entities.Mascota;
import com.uniminuto.clinica.entities.Veterinario;
import com.uniminuto.clinica.exception.ConflictoNegocioException;
import com.uniminuto.clinica.exception.RecursoNoEncontradoException;
import com.uniminuto.clinica.exception.SolicitudInvalidaException;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.HistoriaClinicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Componente reutilizable que busca entidades referenciadas y valida su
 * existencia y relaciones. Lo usan todos los servicios para no repetir codigo
 * y mantener mensajes de error consistentes.
 */
@Component
@RequiredArgsConstructor
public class ValidadorRelaciones {

    /** Repositorio de clientes. */
    private final ClienteRepository clienteRepository;

    /** Repositorio de mascotas. */
    private final MascotaRepository mascotaRepository;

    /** Repositorio de veterinarios. */
    private final VeterinarioRepository veterinarioRepository;

    /** Repositorio de citas. */
    private final CitaRepository citaRepository;

    /** Repositorio de historias clinicas. */
    private final HistoriaClinicaRepository historiaClinicaRepository;

    /**
     * Obtiene un cliente existente y activo.
     *
     * @param id identificador del cliente.
     * @return cliente.
     */
    public Cliente obtenerCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El cliente seleccionado no existe."));
        if (!cliente.isActivo()) {
            throw new ConflictoNegocioException("El cliente seleccionado se encuentra inactivo.");
        }
        return cliente;
    }

    /**
     * Obtiene una mascota existente.
     *
     * @param id identificador de la mascota.
     * @return mascota.
     */
    public Mascota obtenerMascota(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La mascota seleccionada no existe."));
    }

    /**
     * Obtiene una mascota activa y valida que pertenezca al cliente indicado.
     *
     * @param mascotaId identificador de la mascota.
     * @param clienteId identificador del cliente.
     * @return mascota.
     */
    public Mascota obtenerMascotaDeCliente(Long mascotaId, Long clienteId) {
        Mascota mascota = obtenerMascota(mascotaId);
        if (!mascota.getCliente().getId().equals(clienteId)) {
            throw new SolicitudInvalidaException("La mascota seleccionada no pertenece al cliente.");
        }
        if (!mascota.isActivo()) {
            throw new ConflictoNegocioException("La mascota seleccionada se encuentra inactiva.");
        }
        return mascota;
    }

    /**
     * Obtiene un veterinario existente y disponible (activo).
     *
     * @param id identificador del veterinario.
     * @return veterinario.
     */
    public Veterinario obtenerVeterinarioDisponible(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El veterinario seleccionado no existe."));
        if (!veterinario.isActivo()) {
            throw new ConflictoNegocioException("El veterinario seleccionado no se encuentra disponible.");
        }
        return veterinario;
    }

    /**
     * Obtiene una cita existente.
     *
     * @param id identificador de la cita.
     * @return cita.
     */
    public Cita obtenerCita(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La cita solicitada no existe."));
    }

    /**
     * Obtiene una historia clinica existente.
     *
     * @param id identificador de la historia.
     * @return historia clinica.
     */
    public HistoriaClinica obtenerHistoria(Long id) {
        return historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La historia clínica solicitada no existe."));
    }
}
