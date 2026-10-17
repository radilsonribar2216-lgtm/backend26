package com.uniminuto.clinica.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Mascota que pertenece a un {@link Cliente}. Relacion N:1 con cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mascota")
public class Mascota extends EntidadAuditable {

    /** Identificador unico de la mascota. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Cliente propietario de la mascota. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** Nombre de la mascota. */
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    /** Especie (perro, gato, etc.). */
    @Column(name = "especie", nullable = false, length = 40)
    private String especie;

    /** Raza de la mascota. */
    @Column(name = "raza", length = 60)
    private String raza;

    /** Sexo de la mascota. */
    @Column(name = "sexo", length = 10)
    private String sexo;

    /** Fecha de nacimiento. */
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    /** Indica si la mascota esta activa en el sistema. */
    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}
