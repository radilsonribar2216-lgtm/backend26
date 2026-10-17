package com.uniminuto.clinica.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excepcion base de la aplicacion. Lleva consigo el codigo HTTP que debe
 * devolverse al cliente y un mensaje comprensible para el usuario final.
 */
@Getter
public abstract class ApiException extends RuntimeException {

    /** Codigo HTTP asociado al error. */
    private final HttpStatus status;

    /**
     * Crea la excepcion.
     *
     * @param status  codigo HTTP a responder.
     * @param mensaje mensaje para el usuario.
     */
    protected ApiException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }
}
