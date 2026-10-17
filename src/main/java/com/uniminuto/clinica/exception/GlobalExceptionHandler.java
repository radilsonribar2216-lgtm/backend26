package com.uniminuto.clinica.exception;

import com.uniminuto.clinica.models.rs.ErrorRS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de errores. Convierte cualquier excepcion en una respuesta
 * {@link ErrorRS} consistente, con el codigo HTTP correcto y sin exponer
 * informacion tecnica al usuario.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Errores de negocio controlados (404, 400, 409).
     *
     * @param ex excepcion de la aplicacion.
     * @return respuesta de error.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorRS> manejarApi(ApiException ex) {
        return construir(ex.getStatus(), ex.getMessage(), null);
    }

    /**
     * Errores de validacion de campos obligatorios o con formato invalido.
     *
     * @param ex excepcion de validacion del cuerpo.
     * @return respuesta 400 con el detalle por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRS> manejarValidacion(MethodArgumentNotValidException ex) {
        return respuestaValidacion(ex.getBindingResult());
    }

    /**
     * Errores al enlazar parametros de la URL (filtros) con formato invalido.
     *
     * @param ex excepcion de enlace.
     * @return respuesta 400 con el detalle por campo.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorRS> manejarEnlace(BindException ex) {
        return respuestaValidacion(ex.getBindingResult());
    }

    /**
     * Cuerpo JSON mal formado o con tipos incorrectos (fechas, numeros).
     *
     * @param ex excepcion de lectura.
     * @return respuesta 400.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRS> manejarCuerpoIlegible(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST,
                "Los datos enviados no tienen un formato válido. Revise las fechas, horas y números.", null);
    }

    /**
     * Parametro de ruta o consulta con un tipo incorrecto.
     *
     * @param ex excepcion de tipo.
     * @return respuesta 400.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRS> manejarTipoIncorrecto(MethodArgumentTypeMismatchException ex) {
        return construir(HttpStatus.BAD_REQUEST,
                "El valor del parámetro '" + ex.getName() + "' no es válido.", null);
    }

    /**
     * Violacion de integridad en base de datos (duplicados, llaves foraneas).
     *
     * @param ex excepcion de integridad.
     * @return respuesta 409.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRS> manejarIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de integridad: {}", ex.getMostSpecificCause().getMessage());
        return construir(HttpStatus.CONFLICT,
                "La operación no se pudo completar porque los datos entran en conflicto con información existente.",
                null);
    }

    /**
     * Cualquier otra excepcion. Las excepciones propias de Spring MVC (ruta
     * inexistente, metodo no permitido, etc.) conservan su codigo HTTP; el
     * resto se responde como 500 sin exponer detalles tecnicos.
     *
     * @param ex excepcion no controlada.
     * @return respuesta de error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRS> manejarGenerica(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatus status = HttpStatus.resolve(errorResponse.getStatusCode().value());
            if (status == null) {
                status = HttpStatus.BAD_REQUEST;
            }
            return construir(status, mensajeParaEstado(status), null);
        }
        log.error("Error no controlado", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Intente nuevamente más tarde.", null);
    }

    /**
     * Arma la respuesta 400 con el detalle de los campos invalidos.
     *
     * @param resultado resultado del enlace/validacion.
     * @return respuesta de error.
     */
    private ResponseEntity<ErrorRS> respuestaValidacion(BindingResult resultado) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : resultado.getFieldErrors()) {
            campos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return construir(HttpStatus.BAD_REQUEST,
                "Hay campos con errores. Revise la información ingresada.", campos);
    }

    /**
     * Mensaje amigable segun el codigo HTTP.
     *
     * @param status codigo HTTP.
     * @return mensaje para el usuario.
     */
    private String mensajeParaEstado(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "El recurso solicitado no existe.";
            case METHOD_NOT_ALLOWED -> "La operación solicitada no está permitida.";
            case UNSUPPORTED_MEDIA_TYPE -> "El tipo de contenido enviado no es soportado.";
            default -> "La solicitud no es válida.";
        };
    }

    /**
     * Construye el cuerpo de error estandar.
     *
     * @param status  codigo HTTP.
     * @param mensaje mensaje para el usuario.
     * @param campos  detalle por campo (puede ser null).
     * @return respuesta lista para devolver.
     */
    private ResponseEntity<ErrorRS> construir(HttpStatus status, String mensaje, Map<String, String> campos) {
        ErrorRS cuerpo = new ErrorRS();
        cuerpo.setStatus(status.value());
        cuerpo.setMessage(mensaje);
        cuerpo.setError(status.getReasonPhrase());
        cuerpo.setErrores(campos);
        cuerpo.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(status).body(cuerpo);
    }
}
