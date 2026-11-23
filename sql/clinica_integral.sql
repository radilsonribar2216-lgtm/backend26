-- Ampliacion funcional de la clinica veterinaria.
-- Ejecutar despues de sql/clinica.sql. No elimina ni recrea tablas existentes.
USE clinica;

CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    veterinario_id BIGINT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_email (email),
    UNIQUE KEY uk_usuario_veterinario (veterinario_id),
    CONSTRAINT fk_usuario_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario(id),
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('ADMINISTRADOR', 'RECEPCION', 'MEDICO'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS producto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(40) NOT NULL,
    nombre VARCHAR(160) NOT NULL,
    descripcion TEXT NULL,
    categoria VARCHAR(80) NOT NULL,
    precio_venta DECIMAL(12,2) NOT NULL,
    existencia INT NOT NULL DEFAULT 0,
    existencia_minima INT NOT NULL DEFAULT 0,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_producto_codigo (codigo),
    KEY idx_producto_nombre (nombre),
    CONSTRAINT ck_producto_precio CHECK (precio_venta >= 0),
    CONSTRAINT ck_producto_existencia CHECK (existencia >= 0 AND existencia_minima >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS lote_producto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    producto_id BIGINT NOT NULL,
    numero_lote VARCHAR(80) NOT NULL,
    registro_invima VARCHAR(100) NULL,
    fecha_vencimiento DATE NULL,
    cantidad_inicial INT NOT NULL,
    cantidad_disponible INT NOT NULL,
    costo_unitario DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_lote_producto (producto_id, numero_lote),
    KEY idx_lote_vencimiento (fecha_vencimiento),
    CONSTRAINT fk_lote_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT ck_lote_cantidades CHECK (cantidad_inicial >= 0 AND cantidad_disponible >= 0 AND cantidad_disponible <= cantidad_inicial),
    CONSTRAINT ck_lote_costo CHECK (costo_unitario >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS movimiento_inventario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    producto_id BIGINT NOT NULL,
    lote_id BIGINT NULL,
    tipo VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    referencia VARCHAR(100) NULL,
    usuario_id BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_movimiento_producto_fecha (producto_id, created_at),
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT fk_movimiento_lote FOREIGN KEY (lote_id) REFERENCES lote_producto(id),
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT ck_movimiento_tipo CHECK (tipo IN ('ENTRADA', 'SALIDA', 'AJUSTE')),
    CONSTRAINT ck_movimiento_cantidad CHECK (cantidad > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS factura (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(30) NOT NULL,
    cliente_id BIGINT NOT NULL,
    mascota_id BIGINT NULL,
    historia_clinica_id BIGINT NULL,
    fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12,2) NOT NULL,
    descuento DECIMAL(12,2) NOT NULL DEFAULT 0,
    impuesto DECIMAL(12,2) NOT NULL DEFAULT 0,
    total DECIMAL(12,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PREFactura',
    observaciones TEXT NULL,
    usuario_id BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_factura_numero (numero),
    KEY idx_factura_fecha (fecha_emision),
    KEY idx_factura_cliente (cliente_id),
    CONSTRAINT fk_factura_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_factura_mascota FOREIGN KEY (mascota_id) REFERENCES mascota(id),
    CONSTRAINT fk_factura_historia FOREIGN KEY (historia_clinica_id) REFERENCES historia_clinica(id),
    CONSTRAINT fk_factura_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT ck_factura_estado CHECK (estado IN ('PREFactura', 'EMITIDA', 'PAGADA', 'ANULADA')),
    CONSTRAINT ck_factura_valores CHECK (subtotal >= 0 AND descuento >= 0 AND impuesto >= 0 AND total >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS factura_detalle (
    id BIGINT NOT NULL AUTO_INCREMENT,
    factura_id BIGINT NOT NULL,
    producto_id BIGINT NULL,
    concepto VARCHAR(180) NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    descuento DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_linea DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_detalle_factura (factura_id),
    CONSTRAINT fk_detalle_factura FOREIGN KEY (factura_id) REFERENCES factura(id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT ck_detalle_valores CHECK (cantidad > 0 AND precio_unitario >= 0 AND descuento >= 0 AND total_linea >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pago (
    id BIGINT NOT NULL AUTO_INCREMENT,
    factura_id BIGINT NOT NULL,
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor DECIMAL(12,2) NOT NULL,
    medio_pago VARCHAR(30) NOT NULL,
    referencia VARCHAR(100) NULL,
    observaciones VARCHAR(255) NULL,
    usuario_id BIGINT NULL,
    PRIMARY KEY (id),
    KEY idx_pago_factura_fecha (factura_id, fecha_pago),
    CONSTRAINT fk_pago_factura FOREIGN KEY (factura_id) REFERENCES factura(id),
    CONSTRAINT fk_pago_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT ck_pago_valor CHECK (valor > 0),
    CONSTRAINT ck_pago_medio CHECK (medio_pago IN ('EFECTIVO', 'TRANSFERENCIA', 'TARJETA', 'OTRO'))
) ENGINE=InnoDB;

-- Vistas de consulta para reportes administrativos.
CREATE OR REPLACE VIEW vw_inventario_bajo AS
SELECT id, codigo, nombre, categoria, existencia, existencia_minima, precio_venta
FROM producto
WHERE activo = 1 AND existencia <= existencia_minima;

CREATE OR REPLACE VIEW vw_resumen_facturacion_diaria AS
SELECT DATE(fecha_emision) AS fecha,
       COUNT(*) AS cantidad_facturas,
       SUM(total) AS total_facturado,
       SUM(CASE WHEN estado = 'PAGADA' THEN total ELSE 0 END) AS total_pagado
FROM factura
WHERE estado <> 'ANULADA'
GROUP BY DATE(fecha_emision);
