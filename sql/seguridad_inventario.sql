-- Seguridad de acceso y soporte para consumo de inventario desde facturas.
USE clinica;

CREATE TABLE IF NOT EXISTS sesion_usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    token_hash CHAR(64) NOT NULL,
    expira_en DATETIME NOT NULL,
    revocada TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_sesion_token_hash (token_hash),
    KEY idx_sesion_usuario_expira (usuario_id, expira_en),
    CONSTRAINT fk_sesion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

ALTER TABLE factura_detalle
    ADD COLUMN IF NOT EXISTS lote_id BIGINT NULL,
    ADD CONSTRAINT fk_detalle_lote FOREIGN KEY (lote_id) REFERENCES lote_producto(id);
