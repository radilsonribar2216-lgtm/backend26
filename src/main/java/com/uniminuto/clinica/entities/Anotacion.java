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

import java.time.LocalDateTime;

/**
 * Anotacion de seguimiento dentro de una {@link HistoriaClinica}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "anotacion")
public class Anotacion extends EntidadAuditable {

    /** Identificador unico de la anotacion. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Historia clinica a la que pertenece la anotacion. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historia_clinica_id", nullable = false)
    private HistoriaClinica historiaClinica;

    /** Veterinario que registra la anotacion. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    /** Fecha y hora de la anotacion. */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /** Descripcion de lo realizado en la consulta. */
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /** Observacion adicional. */
    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    /** Diagnostico o evolucion del paciente. */
    @Column(name = "diagnostico_evolucion", nullable = false, columnDefinition = "TEXT")
    private String diagnosticoEvolucion;
}
