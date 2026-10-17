package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando un registro referenciado no existe (HTTP 404).
 */
public class RecursoNoEncontradoException extends ApiException {

    /**
     * Crea la excepcion.
     *
     * @param mensaje mensaje para el usuario.
     */
    public RecursoNoEncontradoException(String mensaje) {
        super(HttpStatus.NOT_FOUND, mensaje);
    }
}
