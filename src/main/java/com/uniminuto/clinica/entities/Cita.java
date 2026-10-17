package com.uniminuto.clinica.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;

/**
 * Cita medica de una {@link Mascota} con un {@link Veterinario}.
 * El cliente se obtiene a traves de la mascota ({@code cita.getMascota().getCliente()}),
 * lo que impide inconsistencias entre cliente y mascota.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cita")
public class Cita extends EntidadAuditable {

    /** Identificador unico de la cita. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mascota que sera atendida. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    /** Veterinario asignado. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    /** Fecha y hora programadas de la cita. */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    @Column(name = "motivo", nullable = false, length = 255)
    private String motivo;

    /** Estado actual de la cita. */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PENDIENTE;
}
