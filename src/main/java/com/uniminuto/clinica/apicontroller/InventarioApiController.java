package com.uniminuto.clinica.apicontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * API inicial de inventario. Las operaciones de stock se registran siempre
 * como movimientos y se ejecutan dentro de una transaccion.
 */
@RestController
@RequestMapping("/inventario")
public class InventarioApiController {
    private final JdbcTemplate jdbc;

    public InventarioApiController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/productos")
    public List<Map<String, Object>> listarProductos(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Boolean bajoMinimo) {
        String termino = texto == null ? "" : texto.trim();
        String filtroMinimo = Boolean.TRUE.equals(bajoMinimo)
                ? " AND existencia <= existencia_minima " : "";
        return jdbc.queryForList("""
            SELECT id, codigo, nombre, descripcion, categoria, precio_venta,
                   existencia, existencia_minima, activo
            FROM producto
            WHERE activo = 1 AND (codigo LIKE ? OR nombre LIKE ?)
            """ + filtroMinimo + " ORDER BY nombre",
            "%" + termino + "%", "%" + termino + "%");
    }

    @GetMapping("/productos/{id}")
    public Map<String, Object> consultarProducto(@PathVariable long id) {
        return jdbc.queryForMap("""
            SELECT id, codigo, nombre, descripcion, categoria, precio_venta,
                   existencia, existencia_minima, activo
            FROM producto WHERE id = ?
            """, id);
    }

    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> crearProducto(@RequestBody Map<String, Object> body) {
        String codigo = requerido(body, "codigo");
        String nombre = requerido(body, "nombre");
        String categoria = requerido(body, "categoria");
        BigDecimal precio = decimal(body, "precioVenta");
        int minimo = entero(body, "existenciaMinima", 0);
        jdbc.update("""
            INSERT INTO producto(codigo,nombre,descripcion,categoria,precio_venta,
                                 existencia,existencia_minima)
            VALUES(?,?,?,?,?,0,?)
            """, codigo, nombre, body.get("descripcion"), categoria, precio, minimo);
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return consultarProducto(id);
    }

    @PutMapping("/productos/{id}")
    public Map<String, Object> actualizarProducto(@PathVariable long id,
                                                    @RequestBody Map<String, Object> body) {
        jdbc.update("""
            UPDATE producto SET nombre=?, descripcion=?, categoria=?,
                precio_venta=?, existencia_minima=? WHERE id=? AND activo=1
            """, requerido(body, "nombre"), body.get("descripcion"),
                requerido(body, "categoria"), decimal(body, "precioVenta"),
                entero(body, "existenciaMinima", 0), id);
        return consultarProducto(id);
    }

    @DeleteMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivarProducto(@PathVariable long id) {
        jdbc.update("UPDATE producto SET activo=0 WHERE id=?", id);
    }

    @GetMapping("/lotes")
    public List<Map<String, Object>> listarLotes(@RequestParam(required = false) Long productoId) {
        if (productoId == null) {
            return jdbc.queryForList("""
                SELECT l.*, p.codigo, p.nombre AS producto
                FROM lote_producto l JOIN producto p ON p.id=l.producto_id
                ORDER BY l.fecha_vencimiento
                """);
        }
        return jdbc.queryForList("""
            SELECT l.*, p.codigo, p.nombre AS producto
            FROM lote_producto l JOIN producto p ON p.id=l.producto_id
            WHERE l.producto_id=? ORDER BY l.fecha_vencimiento
            """, productoId);
    }

    @PostMapping("/lotes")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Map<String, Object> crearLote(@RequestBody Map<String, Object> body) {
        long productoId = Long.parseLong(requerido(body, "productoId"));
        String numero = requerido(body, "numeroLote");
        int cantidad = entero(body, "cantidad", -1);
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad del lote debe ser mayor que cero.");
        jdbc.update("""
            INSERT INTO lote_producto(producto_id,numero_lote,registro_invima,
                fecha_vencimiento,cantidad_inicial,cantidad_disponible,costo_unitario)
            VALUES(?,?,?,?,?,?,?)
            """, productoId, numero, body.get("registroInvima"),
                body.get("fechaVencimiento"), cantidad, cantidad,
                decimal(body, "costoUnitario"));
        Long loteId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbc.update("UPDATE producto SET existencia=existencia+? WHERE id=?", cantidad, productoId);
        jdbc.update("""
            INSERT INTO movimiento_inventario(producto_id,lote_id,tipo,cantidad,motivo)
            VALUES(?,?,'ENTRADA',?,'Ingreso de lote')
            """, productoId, loteId, cantidad);
        return jdbc.queryForMap("SELECT * FROM lote_producto WHERE id=?", loteId);
    }

    @PostMapping("/movimientos")
    @Transactional
    public Map<String, Object> movimiento(@RequestBody Map<String, Object> body) {
        long productoId = Long.parseLong(requerido(body, "productoId"));
        long loteId = Long.parseLong(requerido(body, "loteId"));
        String tipo = requerido(body, "tipo").toUpperCase();
        int cantidad = entero(body, "cantidad", 0);
        String motivo = requerido(body, "motivo");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        if (!List.of("ENTRADA", "SALIDA").contains(tipo))
            throw new IllegalArgumentException("El tipo debe ser ENTRADA o SALIDA.");

        Integer stock = jdbc.queryForObject("SELECT existencia FROM producto WHERE id=? AND activo=1 FOR UPDATE",
                Integer.class, productoId);
        if (stock == null) throw new IllegalArgumentException("Producto no encontrado.");
        if ("SALIDA".equals(tipo) && stock < cantidad)
            throw new IllegalArgumentException("No hay existencias suficientes.");

        {
            Integer disponible = jdbc.queryForObject(
                "SELECT cantidad_disponible FROM lote_producto WHERE id=? AND producto_id=? FOR UPDATE",
                Integer.class, loteId, productoId);
            if (disponible == null) throw new IllegalArgumentException("El lote no pertenece al producto.");
            if ("SALIDA".equals(tipo) && disponible < cantidad)
                throw new IllegalArgumentException("El lote no tiene unidades suficientes.");
            jdbc.update("UPDATE lote_producto SET cantidad_disponible=cantidad_disponible+? WHERE id=?",
                    "ENTRADA".equals(tipo) ? cantidad : -cantidad, loteId);
        }
        jdbc.update("UPDATE producto SET existencia=existencia+? WHERE id=?",
                "ENTRADA".equals(tipo) ? cantidad : -cantidad, productoId);
        jdbc.update("""
            INSERT INTO movimiento_inventario(producto_id,lote_id,tipo,cantidad,motivo)
            VALUES(?,?,?,?,?)
            """, productoId, loteId, tipo, cantidad, motivo);
        Long movimientoId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return jdbc.queryForMap("SELECT * FROM movimiento_inventario WHERE id=?", movimientoId);
    }

    @GetMapping("/movimientos")
    public List<Map<String, Object>> listarMovimientos(
            @RequestParam(required = false) Long productoId) {
        if (productoId == null) return jdbc.queryForList("""
            SELECT m.*, p.codigo, p.nombre AS producto, l.numero_lote
            FROM movimiento_inventario m JOIN producto p ON p.id=m.producto_id
            LEFT JOIN lote_producto l ON l.id=m.lote_id
            ORDER BY m.created_at DESC
            """);
        return jdbc.queryForList("""
            SELECT m.*, p.codigo, p.nombre AS producto, l.numero_lote
            FROM movimiento_inventario m JOIN producto p ON p.id=m.producto_id
            LEFT JOIN lote_producto l ON l.id=m.lote_id
            WHERE m.producto_id=? ORDER BY m.created_at DESC
            """, productoId);
    }

    private String requerido(Map<String, Object> body, String campo) {
        Object valor = body.get(campo);
        if (valor == null || valor.toString().isBlank())
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        return valor.toString().trim();
    }

    private BigDecimal decimal(Map<String, Object> body, String campo) {
        BigDecimal valor = new BigDecimal(requerido(body, campo));
        if (valor.signum() < 0) throw new IllegalArgumentException(campo + " no puede ser negativo.");
        return valor;
    }

    private int entero(Map<String, Object> body, String campo, int defecto) {
        Object valor = body.get(campo);
        if (valor == null) return defecto;
        int numero = Integer.parseInt(valor.toString());
        if (numero < 0) throw new IllegalArgumentException(campo + " no puede ser negativo.");
        return numero;
    }
}
