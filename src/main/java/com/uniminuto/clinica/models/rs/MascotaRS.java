package com.uniminuto.clinica.models.rs;

import lombok.Data;

import java.time.LocalDate;

/**
 * Datos de una mascota expuestos por la API.
 */
@Data
public class MascotaRS {

    /** Identificador de la mascota. */
    private Long id;

    /** Identificador del cliente propietario. */
    private Long clienteId;

    /** Nombre completo del cliente propietario. */
    private String clienteNombre;

    /** Nombre de la mascota. */
    private String nombre;

    /** Especie. */
    private String especie;

    /** Raza. */
    private String raza;

    /** Sexo. */
    private String sexo;

    /** Fecha de nacimiento. */
    private LocalDate fechaNacimiento;
}
