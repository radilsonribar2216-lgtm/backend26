package com.uniminuto.clinica.util;

import com.uniminuto.clinica.entities.Anotacion;
import com.uniminuto.clinica.entities.Cita;
import com.uniminuto.clinica.entities.Cliente;
import com.uniminuto.clinica.entities.FormulaMedica;
import com.uniminuto.clinica.entities.HistoriaClinica;
import com.uniminuto.clinica.entities.Mascota;
import com.uniminuto.clinica.entities.Veterinario;
import com.uniminuto.clinica.models.rs.AnotacionRS;
import com.uniminuto.clinica.models.rs.CitaRS;
import com.uniminuto.clinica.models.rs.ClienteRS;
import com.uniminuto.clinica.models.rs.FormulaDetalleRS;
import com.uniminuto.clinica.models.rs.FormulaResumenRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaDetalleRS;
import com.uniminuto.clinica.models.rs.HistoriaClinicaResumenRS;
import com.uniminuto.clinica.models.rs.MascotaRS;
import com.uniminuto.clinica.models.rs.VeterinarioRS;

/**
 * Convierte entidades en DTOs de respuesta. Se invoca dentro de una transaccion
 * porque recorre relaciones perezosas (lazy).
 */
public final class Mapeador {

    private Mapeador() {
    }

    /**
     * Une nombres y apellidos.
     *
     * @param nombres   nombres.
     * @param apellidos apellidos.
     * @return nombre completo.
     */
    public static String nombreCompleto(String nombres, String apellidos) {
        return nombres + " " + apellidos;
    }

    /**
     * Convierte un cliente.
     *
     * @param c entidad.
     * @return DTO.
     */
    public static ClienteRS aClienteRS(Cliente c) {
        ClienteRS rs = new ClienteRS();
        rs.setId(c.getId());
        rs.setDocumento(c.getDocumento());
        rs.setNombres(c.getNombres());
        rs.setApellidos(c.getApellidos());
        rs.setNombreCompleto(nombreCompleto(c.getNombres(), c.getApellidos()));
        rs.setTelefono(c.getTelefono());
        rs.setEmail(c.getEmail());
        rs.setDireccion(c.getDireccion());
        return rs;
    }

    /**
     * Convierte una mascota.
     *
     * @param m entidad.
     * @return DTO.
     */
    public static MascotaRS aMascotaRS(Mascota m) {
        MascotaRS rs = new MascotaRS();
        rs.setId(m.getId());
        rs.setClienteId(m.getCliente().getId());
        rs.setClienteNombre(nombreCompleto(m.getCliente().getNombres(), m.getCliente().getApellidos()));
        rs.setNombre(m.getNombre());
        rs.setEspecie(m.getEspecie());
        rs.setRaza(m.getRaza());
        rs.setSexo(m.getSexo());
        rs.setFechaNacimiento(m.getFechaNacimiento());
        return rs;
    }

    /**
     * Convierte un veterinario.
     *
     * @param v entidad.
     * @return DTO.
     */
    public static VeterinarioRS aVeterinarioRS(Veterinario v) {
        VeterinarioRS rs = new VeterinarioRS();
        rs.setId(v.getId());
        rs.setDocumento(v.getDocumento());
        rs.setNombreCompleto(nombreCompleto(v.getNombres(), v.getApellidos()));
        rs.setEspecialidad(v.getEspecialidad());
        return rs;
    }

    /**
     * Convierte una cita.
     *
     * @param c entidad.
     * @return DTO.
     */
    public static CitaRS aCitaRS(Cita c) {
        Cliente cliente = c.getMascota().getCliente();
        CitaRS rs = new CitaRS();
        rs.setId(c.getId());
        rs.setFecha(c.getFechaHora().toLocalDate());
        rs.setHora(c.getFechaHora().toLocalTime());
        rs.setFechaHora(c.getFechaHora());
        rs.setClienteId(cliente.getId());
        rs.setClienteNombre(nombreCompleto(cliente.getNombres(), cliente.getApellidos()));
        rs.setMascotaId(c.getMascota().getId());
        rs.setMascotaNombre(c.getMascota().getNombre());
        rs.setVeterinarioId(c.getVeterinario().getId());
        rs.setVeterinarioNombre(nombreCompleto(c.getVeterinario().getNombres(), c.getVeterinario().getApellidos()));
        rs.setMotivo(c.getMotivo());
        rs.setEstado(c.getEstado());
        return rs;
    }

    /**
     * Convierte una historia clinica a su version resumida (listado).
     *
     * @param h entidad.
     * @return DTO resumido.
     */
    public static HistoriaClinicaResumenRS aHistoriaResumenRS(HistoriaClinica h) {
        Cliente cliente = h.getMascota().getCliente();
        HistoriaClinicaResumenRS rs = new HistoriaClinicaResumenRS();
        rs.setId(h.getId());
        rs.setFechaHora(h.getFechaHora());
        rs.setMascotaNombre(h.getMascota().getNombre());
        rs.setClienteNombre(nombreCompleto(cliente.getNombres(), cliente.getApellidos()));
        rs.setVeterinarioNombre(nombreCompleto(h.getVeterinario().getNombres(), h.getVeterinario().getApellidos()));
        rs.setMotivoConsulta(h.getMotivoConsulta());
        rs.setEstado(h.getEstado());
        return rs;
    }

    /**
     * Convierte una historia clinica a su version detallada.
     *
     * @param h entidad.
     * @return DTO detallado.
     */
    public static HistoriaClinicaDetalleRS aHistoriaDetalleRS(HistoriaClinica h) {
        Cliente cliente = h.getMascota().getCliente();
        HistoriaClinicaDetalleRS rs = new HistoriaClinicaDetalleRS();
        rs.setId(h.getId());
        rs.setCitaId(h.getCita().getId());
        rs.setClienteId(cliente.getId());
        rs.setClienteNombre(nombreCompleto(cliente.getNombres(), cliente.getApellidos()));
        rs.setMascotaId(h.getMascota().getId());
        rs.setMascotaNombre(h.getMascota().getNombre());
        rs.setVeterinarioId(h.getVeterinario().getId());
        rs.setVeterinarioNombre(nombreCompleto(h.getVeterinario().getNombres(), h.getVeterinario().getApellidos()));
        rs.setFechaHora(h.getFechaHora());
        rs.setMotivoConsulta(h.getMotivoConsulta());
        rs.setDiagnostico(h.getDiagnostico());
        rs.setObservaciones(h.getObservaciones());
        rs.setEstado(h.getEstado());
        rs.setCreatedAt(h.getCreatedAt());
        return rs;
    }

    /**
     * Convierte una anotacion.
     *
     * @param a entidad.
     * @return DTO.
     */
    public static AnotacionRS aAnotacionRS(Anotacion a) {
        AnotacionRS rs = new AnotacionRS();
        rs.setId(a.getId());
        rs.setHistoriaClinicaId(a.getHistoriaClinica().getId());
        rs.setFechaHora(a.getFechaHora());
        rs.setVeterinarioId(a.getVeterinario().getId());
        rs.setVeterinarioNombre(nombreCompleto(a.getVeterinario().getNombres(), a.getVeterinario().getApellidos()));
        rs.setDescripcion(a.getDescripcion());
        rs.setObservacion(a.getObservacion());
        rs.setDiagnosticoEvolucion(a.getDiagnosticoEvolucion());
        return rs;
    }

    /**
     * Convierte una formula a su version resumida (listado).
     *
     * @param f entidad.
     * @return DTO resumido.
     */
    public static FormulaResumenRS aFormulaResumenRS(FormulaMedica f) {
        Cliente cliente = f.getMascota().getCliente();
        FormulaResumenRS rs = new FormulaResumenRS();
        rs.setId(f.getId());
        rs.setCreatedAt(f.getCreatedAt());
        rs.setFechaEmision(f.getFechaEmision());
        rs.setMascotaNombre(f.getMascota().getNombre());
        rs.setClienteNombre(nombreCompleto(cliente.getNombres(), cliente.getApellidos()));
        rs.setVeterinarioNombre(nombreCompleto(f.getVeterinario().getNombres(), f.getVeterinario().getApellidos()));
        rs.setMedicamento(f.getMedicamento());
        rs.setDosis(f.getDosisCantidad().toPlainString() + " " + f.getDosisUnidad());
        rs.setDuracionDias(f.getDuracionDias());
        rs.setActivo(f.isActivo());
        return rs;
    }

    /**
     * Convierte una formula a su version detallada.
     *
     * @param f entidad.
     * @return DTO detallado.
     */
    public static FormulaDetalleRS aFormulaDetalleRS(FormulaMedica f) {
        Cliente cliente = f.getMascota().getCliente();
        FormulaDetalleRS rs = new FormulaDetalleRS();
        rs.setId(f.getId());
        rs.setMascotaId(f.getMascota().getId());
        rs.setMascotaNombre(f.getMascota().getNombre());
        rs.setClienteId(cliente.getId());
        rs.setClienteNombre(nombreCompleto(cliente.getNombres(), cliente.getApellidos()));
        rs.setHistoriaClinicaId(f.getHistoriaClinica().getId());
        rs.setFechaConsulta(f.getHistoriaClinica().getFechaHora());
        rs.setMotivoConsulta(f.getHistoriaClinica().getMotivoConsulta());
        rs.setVeterinarioId(f.getVeterinario().getId());
        rs.setVeterinarioNombre(nombreCompleto(f.getVeterinario().getNombres(), f.getVeterinario().getApellidos()));
        rs.setFechaEmision(f.getFechaEmision());
        rs.setMedicamento(f.getMedicamento());
        rs.setDosisCantidad(f.getDosisCantidad());
        rs.setDosisUnidad(f.getDosisUnidad());
        rs.setFrecuencia(f.getFrecuencia());
        rs.setDuracionDias(f.getDuracionDias());
        rs.setIndicaciones(f.getIndicaciones());
        rs.setActivo(f.isActivo());
        rs.setCreatedAt(f.getCreatedAt());
        rs.setUpdatedAt(f.getUpdatedAt());
        return rs;
    }
}
