package com.uniminuto.clinica.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Profesional que atiende citas, registra historias, anotaciones y formulas.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "veterinario")
public class Veterinario extends EntidadAuditable {

    /** Identificador unico del veterinario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numero de documento de identidad (unico). */
    @Column(name = "documento", nullable = false, length = 20)
    private String documento;

    /** Nombres del veterinario. */
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    /** Apellidos del veterinario. */
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /** Especialidad medica. */
    @Column(name = "especialidad", length = 80)
    private String especialidad;

    /** Telefono de contacto. */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /** Correo electronico. */
    @Column(name = "email", length = 120)
    private String email;

    /** Indica si el veterinario esta disponible en el sistema. */
    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}
