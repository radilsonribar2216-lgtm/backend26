package com.uniminuto.clinica.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Clase base con los campos de auditoria comunes a todas las entidades.
 * Se llenan automaticamente al insertar o actualizar un registro.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class EntidadAuditable {

    /** Fecha y hora de creacion del registro. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Fecha y hora de la ultima modificacion del registro. */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Asigna las fechas de creacion y modificacion antes de insertar.
     */
    @PrePersist
    protected void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /**
     * Actualiza la fecha de modificacion antes de cada update.
     */
    @PreUpdate
    protected void alActualizar() {
        this.updatedAt = LocalDateTime.now();
    }
}
