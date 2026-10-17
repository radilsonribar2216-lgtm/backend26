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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Formula medica emitida por un veterinario dentro de una {@link HistoriaClinica}.
 * No se elimina fisicamente: se inactiva con el campo {@code activo}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "formula_medica")
public class FormulaMedica extends EntidadAuditable {

    /** Identificador unico de la formula. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mascota para la que se emite la formula. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    /** Historia clinica (consulta) a la que se asocia la formula. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historia_clinica_id", nullable = false)
    private HistoriaClinica historiaClinica;

    /** Veterinario que emite la formula. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    /** Fecha y hora de emision (no puede ser anterior a la consulta). */
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    /** Nombre del medicamento. */
    @Column(name = "medicamento", nullable = false, length = 150)
    private String medicamento;

    /** Cantidad de la dosis (mayor que cero). */
    @Column(name = "dosis_cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal dosisCantidad;

    /** Unidad de la dosis (mg, ml, gotas, etc.). */
    @Column(name = "dosis_unidad", nullable = false, length = 20)
    private String dosisUnidad;

    /** Frecuencia de administracion (por ejemplo, "Cada 8 horas"). */
    @Column(name = "frecuencia", nullable = false, length = 100)
    private String frecuencia;

    /** Duracion del tratamiento en dias (mayor que cero). */
    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    /** Indicaciones para el cliente. */
    @Column(name = "indicaciones", nullable = false, columnDefinition = "TEXT")
    private String indicaciones;

    /** Indica si la formula esta activa (false = inactivada). */
    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}
