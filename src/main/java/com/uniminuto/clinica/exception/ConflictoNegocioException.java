package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando la operacion es valida pero choca con el estado actual del
 * sistema (HTTP 409). Ejemplo: cruce de horarios, editar una cita cancelada.
 */
public class ConflictoNegocioException extends ApiException {

    /**
     * Crea la excepcion.
     *
     * @param mensaje mensaje para el usuario.
     */
    public ConflictoNegocioException(String mensaje) {
        super(HttpStatus.CONFLICT, mensaje);
    }
}
