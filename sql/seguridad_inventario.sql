-- Ejecutar despues de clinica_integral.sql. Se puede ejecutar varias veces en MySQL 8.
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

DROP PROCEDURE IF EXISTS clinica_agregar_lote_factura;
DELIMITER //
CREATE PROCEDURE clinica_agregar_lote_factura()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'factura_detalle'
          AND COLUMN_NAME = 'lote_id'
    ) THEN
        ALTER TABLE factura_detalle ADD COLUMN lote_id BIGINT NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'factura_detalle'
          AND CONSTRAINT_NAME = 'fk_detalle_lote'
    ) THEN
        ALTER TABLE factura_detalle
            ADD CONSTRAINT fk_detalle_lote
            FOREIGN KEY (lote_id) REFERENCES lote_producto(id);
    END IF;
END//
DELIMITER ;

CALL clinica_agregar_lote_factura();
DROP PROCEDURE clinica_agregar_lote_factura;
