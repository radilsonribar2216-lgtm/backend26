package com.uniminuto.clinica.models.rs;

import com.uniminuto.clinica.models.MiRespuestaRS;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Respuesta estandar de error. Reutiliza {@code status} y {@code message} de
 * {@link MiRespuestaRS} y agrega el detalle por campo.
 */
@Getter
@Setter
public class ErrorRS extends MiRespuestaRS {

    /** Descripcion corta del codigo HTTP (por ejemplo "Not Found"). */
    private String error;

    /** Detalle de errores por campo (nombre del campo, mensaje). Puede ser nulo. */
    private Map<String, String> errores;

    /** Momento en que ocurrio el error. */
    private LocalDateTime timestamp;
}
