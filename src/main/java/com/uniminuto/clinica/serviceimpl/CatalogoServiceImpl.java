package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entities.Cliente;
import com.uniminuto.clinica.exception.RecursoNoEncontradoException;
import com.uniminuto.clinica.models.rs.ClienteRS;
import com.uniminuto.clinica.models.rs.MascotaRS;
import com.uniminuto.clinica.models.rs.VeterinarioRS;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.VeterinarioRepository;
import com.uniminuto.clinica.service.CatalogoService;
import com.uniminuto.clinica.util.Mapeador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementacion de {@link CatalogoService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoServiceImpl implements CatalogoService {

    /** Repositorio de clientes. */
    private final ClienteRepository clienteRepository;

    /** Repositorio de mascotas. */
    private final MascotaRepository mascotaRepository;

    /** Repositorio de veterinarios. */
    private final VeterinarioRepository veterinarioRepository;

    /** Validador de relaciones compartido. */
    private final ValidadorRelaciones validador;

    /** {@inheritDoc} */
    @Override
    public List<ClienteRS> listarClientes() {
        return clienteRepository.findByActivoTrueOrderByApellidosAscNombresAsc()
                .stream().map(Mapeador::aClienteRS).toList();
    }

    /** {@inheritDoc} */
    @Override
    public ClienteRS consultarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El cliente solicitado no existe."));
        return Mapeador.aClienteRS(cliente);
    }

    /** {@inheritDoc} */
    @Override
    public List<MascotaRS> listarMascotasDeCliente(Long clienteId) {
        validador.obtenerCliente(clienteId);
        return mascotaRepository.findByClienteIdAndActivoTrueOrderByNombreAsc(clienteId)
                .stream().map(Mapeador::aMascotaRS).toList();
    }

    /** {@inheritDoc} */
    @Override
    public MascotaRS consultarMascota(Long id) {
        return Mapeador.aMascotaRS(validador.obtenerMascota(id));
    }

    /** {@inheritDoc} */
    @Override
    public List<VeterinarioRS> listarVeterinarios() {
        return veterinarioRepository.findByActivoTrueOrderByApellidosAscNombresAsc()
                .stream().map(Mapeador::aVeterinarioRS).toList();
    }
}
