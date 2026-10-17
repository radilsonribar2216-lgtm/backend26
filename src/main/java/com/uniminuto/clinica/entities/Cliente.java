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
 * Propietario de una o varias mascotas. Relacion 1:N con {@link Mascota}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente extends EntidadAuditable {

    /** Identificador unico del cliente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numero de documento de identidad (unico). */
    @Column(name = "documento", nullable = false, length = 20)
    private String documento;

    /** Nombres del cliente. */
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    /** Apellidos del cliente. */
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /** Telefono de contacto. */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /** Correo electronico. */
    @Column(name = "email", length = 120)
    private String email;

    /** Direccion de residencia. */
    @Column(name = "direccion", length = 200)
    private String direccion;

    /** Indica si el cliente esta activo en el sistema. */
    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}
