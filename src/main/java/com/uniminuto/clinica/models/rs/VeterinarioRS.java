package com.uniminuto.clinica.models.rs;

import lombok.Data;

/**
 * Datos de un veterinario expuestos por la API.
 */
@Data
public class VeterinarioRS {

    /** Identificador del veterinario. */
    private Long id;

    /** Documento de identidad. */
    private String documento;

    /** Nombre completo. */
    private String nombreCompleto;

    /** Especialidad. */
    private String especialidad;
}
