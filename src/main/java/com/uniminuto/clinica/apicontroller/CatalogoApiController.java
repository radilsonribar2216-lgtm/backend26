package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CatalogoApi;
import com.uniminuto.clinica.models.rs.ClienteRS;
import com.uniminuto.clinica.models.rs.MascotaRS;
import com.uniminuto.clinica.models.rs.VeterinarioRS;
import com.uniminuto.clinica.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de los catalogos de apoyo. Solo delega en {@link CatalogoService}.
 */
@RestController
@RequiredArgsConstructor
public class CatalogoApiController implements CatalogoApi {

    /** Servicio de catalogos. */
    private final CatalogoService catalogoService;

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<List<ClienteRS>> listarClientes() {
        return ResponseEntity.ok(catalogoService.listarClientes());
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<ClienteRS> consultarCliente(Long id) {
        return ResponseEntity.ok(catalogoService.consultarCliente(id));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<List<MascotaRS>> listarMascotasDeCliente(Long clienteId) {
        return ResponseEntity.ok(catalogoService.listarMascotasDeCliente(clienteId));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<MascotaRS> consultarMascota(Long id) {
        return ResponseEntity.ok(catalogoService.consultarMascota(id));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<List<VeterinarioRS>> listarVeterinarios() {
        return ResponseEntity.ok(catalogoService.listarVeterinarios());
    }
}
