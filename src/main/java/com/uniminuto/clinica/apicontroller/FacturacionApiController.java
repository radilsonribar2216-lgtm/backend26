package com.uniminuto.clinica.apicontroller;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** API de prefacturas, facturas y pagos. */
@RestController
@RequestMapping("/facturacion")
public class FacturacionApiController {
    private final JdbcTemplate jdbc;

    public FacturacionApiController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/facturas")
    public List<Map<String, Object>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long clienteId) {
        String sql = """
            SELECT f.*, CONCAT(c.nombres,' ',c.apellidos) AS clienteNombre,
                   COALESCE((SELECT SUM(p.valor) FROM pago p WHERE p.factura_id=f.id),0) AS totalPagado
            FROM factura f JOIN cliente c ON c.id=f.cliente_id
            WHERE (? IS NULL OR f.estado=?) AND (? IS NULL OR f.cliente_id=?)
            ORDER BY f.fecha_emision DESC
            """;
        return jdbc.queryForList(sql, estado, estado, clienteId, clienteId);
    }

    @GetMapping("/facturas/{id}")
    public Map<String, Object> detalle(@PathVariable long id) {
        Map<String, Object> factura = jdbc.queryForMap("""
            SELECT f.*, CONCAT(c.nombres,' ',c.apellidos) AS clienteNombre,
                   COALESCE((SELECT SUM(p.valor) FROM pago p WHERE p.factura_id=f.id),0) AS totalPagado
            FROM factura f JOIN cliente c ON c.id=f.cliente_id WHERE f.id=?
            """, id);
        factura.put("detalles", jdbc.queryForList(
            "SELECT * FROM factura_detalle WHERE factura_id=? ORDER BY id", id));
        factura.put("pagos", jdbc.queryForList(
            "SELECT * FROM pago WHERE factura_id=? ORDER BY fecha_pago DESC", id));
        return factura;
    }

    @PostMapping("/facturas")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Map<String, Object> crearPrefactura(@RequestBody Map<String, Object> body) {
        long clienteId = Long.parseLong(requerido(body, "clienteId"));
        Long mascotaId = body.get("mascotaId") == null ? null : Long.valueOf(body.get("mascotaId").toString());
        List<Map<String, Object>> lineas = (List<Map<String, Object>>) body.get("detalles");
        if (lineas == null || lineas.isEmpty())
            throw new IllegalArgumentException("La factura debe contener al menos un concepto.");

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map<String, Object> linea : lineas) {
            BigDecimal cantidad = new BigDecimal(requerido(linea, "cantidad"));
            BigDecimal precio = new BigDecimal(requerido(linea, "precioUnitario"));
            BigDecimal descuento = new BigDecimal(linea.getOrDefault("descuento", "0").toString());
            if (cantidad.signum() <= 0 || precio.signum() < 0 || descuento.signum() < 0)
                throw new IllegalArgumentException("Revise cantidad, precio y descuento de cada concepto.");
            BigDecimal totalLinea = cantidad.multiply(precio).subtract(descuento);
            if (totalLinea.signum() < 0) throw new IllegalArgumentException("El descuento supera el valor de la línea.");
            linea.put("_total", totalLinea);
            subtotal = subtotal.add(totalLinea);
        }
        BigDecimal impuesto = new BigDecimal(body.getOrDefault("impuesto", "0").toString());
        BigDecimal descuentoGeneral = new BigDecimal(body.getOrDefault("descuento", "0").toString());
        if (impuesto.signum() < 0 || descuentoGeneral.signum() < 0)
            throw new IllegalArgumentException("Impuestos y descuentos no pueden ser negativos.");
        BigDecimal total = subtotal.add(impuesto).subtract(descuentoGeneral);
        if (total.signum() < 0) throw new IllegalArgumentException("El total no puede ser negativo.");

        String numero = "PV-" + System.currentTimeMillis();
        jdbc.update("""
            INSERT INTO factura(numero,cliente_id,mascota_id,subtotal,descuento,impuesto,total,estado,observaciones)
            VALUES(?,?,?,?,?,?,?,'PREFactura',?)
            """, numero, clienteId, mascotaId, subtotal, descuentoGeneral, impuesto, total,
                body.get("observaciones"));
        Long facturaId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        for (Map<String, Object> linea : lineas) {
            Long productoId = linea.get("productoId") == null ? null : Long.valueOf(linea.get("productoId").toString());
            jdbc.update("""
                INSERT INTO factura_detalle(factura_id,producto_id,concepto,cantidad,precio_unitario,descuento,total_linea)
                VALUES(?,?,?,?,?,?,?)
                """, facturaId, productoId, requerido(linea, "concepto"),
                new BigDecimal(requerido(linea, "cantidad")),
                new BigDecimal(requerido(linea, "precioUnitario")),
                new BigDecimal(linea.getOrDefault("descuento", "0").toString()), linea.get("_total"));
        }
        return detalle(facturaId);
    }

    @PostMapping("/facturas/{id}/emitir")
    @Transactional
    public Map<String, Object> emitir(@PathVariable long id) {
        int actualizadas = jdbc.update("""
            UPDATE factura SET estado='EMITIDA'
            WHERE id=? AND estado='PREFactura'
            """, id);
        if (actualizadas == 0) throw new IllegalArgumentException("Solo se pueden emitir prefacturas vigentes.");
        return detalle(id);
    }

    @PostMapping("/facturas/{id}/anular")
    @Transactional
    public Map<String, Object> anular(@PathVariable long id) {
        BigDecimal pagado = jdbc.queryForObject(
            "SELECT COALESCE(SUM(valor),0) FROM pago WHERE factura_id=?", BigDecimal.class, id);
        if (pagado != null && pagado.signum() > 0)
            throw new IllegalArgumentException("No se puede anular una factura con pagos registrados.");
        int actualizadas = jdbc.update("""
            UPDATE factura SET estado='ANULADA'
            WHERE id=? AND estado IN ('PREFactura','EMITIDA')
            """, id);
        if (actualizadas == 0) throw new IllegalArgumentException("La factura no se puede anular.");
        return detalle(id);
    }

    @PostMapping("/facturas/{id}/pagos")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Map<String, Object> registrarPago(@PathVariable long id,
                                              @RequestBody Map<String, Object> body) {
        BigDecimal valor = new BigDecimal(requerido(body, "valor"));
        String medio = requerido(body, "medioPago").toUpperCase();
        if (valor.signum() <= 0) throw new IllegalArgumentException("El pago debe ser mayor que cero.");
        if (!List.of("EFECTIVO","TRANSFERENCIA","TARJETA","OTRO").contains(medio))
            throw new IllegalArgumentException("Medio de pago no válido.");
        Map<String,Object> factura = jdbc.queryForMap("SELECT total,estado FROM factura WHERE id=? FOR UPDATE", id);
        if ("PREFactura".equals(factura.get("estado")) || "ANULADA".equals(factura.get("estado")))
            throw new IllegalArgumentException("La factura debe estar emitida para recibir pagos.");
        BigDecimal total = new BigDecimal(factura.get("total").toString());
        BigDecimal pagado = jdbc.queryForObject(
            "SELECT COALESCE(SUM(valor),0) FROM pago WHERE factura_id=?", BigDecimal.class, id);
        if (pagado.add(valor).compareTo(total) > 0)
            throw new IllegalArgumentException("El pago supera el saldo pendiente.");
        jdbc.update("""
            INSERT INTO pago(factura_id,valor,medio_pago,referencia,observaciones)
            VALUES(?,?,?,?,?)
            """, id, valor, medio, body.get("referencia"), body.get("observaciones"));
        if (pagado.add(valor).compareTo(total) == 0)
            jdbc.update("UPDATE factura SET estado='PAGADA' WHERE id=?", id);
        return detalle(id);
    }

    @GetMapping("/reportes/resumen")
    public Map<String, Object> resumen(
            @RequestParam String desde, @RequestParam String hasta) {
        return jdbc.queryForMap("""
            SELECT COUNT(*) AS cantidadFacturas,
                   COALESCE(SUM(total),0) AS totalFacturado,
                   COALESCE(SUM(CASE WHEN estado='PAGADA' THEN total ELSE 0 END),0) AS totalPagado,
                   COALESCE(SUM(CASE WHEN estado IN ('EMITIDA','PREFactura') THEN total ELSE 0 END),0) AS saldoPorCobrar
            FROM factura
            WHERE DATE(fecha_emision) BETWEEN ? AND ? AND estado <> 'ANULADA'
            """, desde, hasta);
    }

    private String requerido(Map<String, Object> body, String campo) {
        Object valor = body.get(campo);
        if (valor == null || valor.toString().isBlank())
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        return valor.toString().trim();
    }
}
