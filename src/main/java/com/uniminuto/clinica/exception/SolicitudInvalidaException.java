package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando los datos enviados incumplen una regla de validacion
 * (HTTP 400). Ejemplo: mascota que no pertenece al cliente, fecha pasada.
 */
public class SolicitudInvalidaException extends ApiException {

    /**
     * Crea la excepcion.
     *
     * @param mensaje mensaje para el usuario.
     */
    public SolicitudInvalidaException(String mensaje) {
        super(HttpStatus.BAD_REQUEST, mensaje);
    }
}
