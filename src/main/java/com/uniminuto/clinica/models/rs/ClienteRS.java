package com.uniminuto.clinica.models.rs;

import lombok.Data;

/**
 * Datos de un cliente expuestos por la API.
 */
@Data
public class ClienteRS {

    /** Identificador del cliente. */
    private Long id;

    /** Documento de identidad. */
    private String documento;

    /** Nombres. */
    private String nombres;

    /** Apellidos. */
    private String apellidos;

    /** Nombre completo (nombres y apellidos). */
    private String nombreCompleto;

    /** Telefono. */
    private String telefono;

    /** Correo electronico. */
    private String email;

    /** Direccion. */
    private String direccion;
}
