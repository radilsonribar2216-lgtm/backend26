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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Historia clinica generada a partir de una {@link Cita} atendida.
 * Puede tener multiples {@link Anotacion} y {@link FormulaMedica}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "historia_clinica")
public class HistoriaClinica extends EntidadAuditable {

    /** Identificador unico de la historia clinica. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mascota a la que pertenece la historia. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    /** Cita de la cual se origina la historia (una cita genera maximo una historia). */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    /** Veterinario que realizo la consulta. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    /** Fecha y hora de la consulta. */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    @Column(name = "motivo_consulta", nullable = false, length = 255)
    private String motivoConsulta;

    /** Diagnostico establecido. */
    @Column(name = "diagnostico", nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    /** Observaciones generales de la consulta. */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /** Estado de la historia clinica. */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoHistoriaClinica estado = EstadoHistoriaClinica.ABIERTA;
}
