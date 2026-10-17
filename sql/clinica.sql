-- =====================================================================
--  Clinica veterinaria - Segundo parcial Programacion Web
--  Motor: MySQL 8.x (InnoDB, utf8mb4)
--  Uso:   mysql -u root -p clinica < clinica.sql
--  Es re-ejecutable: borra y recrea las tablas.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS clinica
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clinica;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS formula_medica;
DROP TABLE IF EXISTS anotacion;
DROP TABLE IF EXISTS historia_clinica;
DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS mascota;
DROP TABLE IF EXISTS veterinario;
DROP TABLE IF EXISTS cliente;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- CLIENTE
-- ---------------------------------------------------------------------
CREATE TABLE cliente (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    documento   VARCHAR(20)  NOT NULL,
    nombres     VARCHAR(100) NOT NULL,
    apellidos   VARCHAR(100) NOT NULL,
    telefono    VARCHAR(20)  NULL,
    email       VARCHAR(120) NULL,
    direccion   VARCHAR(200) NULL,
    activo      TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cliente_documento (documento)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- MASCOTA (1 cliente : N mascotas)
-- ---------------------------------------------------------------------
CREATE TABLE mascota (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    cliente_id       BIGINT       NOT NULL,
    nombre           VARCHAR(80)  NOT NULL,
    especie          VARCHAR(40)  NOT NULL,
    raza             VARCHAR(60)  NULL,
    sexo             VARCHAR(10)  NULL,
    fecha_nacimiento DATE         NULL,
    activo           TINYINT(1)   NOT NULL DEFAULT 1,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_mascota_cliente (cliente_id),
    -- Evita duplicar la misma mascota para el mismo cliente
    UNIQUE KEY uk_mascota_cliente_nombre (cliente_id, nombre),
    CONSTRAINT fk_mascota_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- VETERINARIO
-- ---------------------------------------------------------------------
CREATE TABLE veterinario (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    documento     VARCHAR(20)  NOT NULL,
    nombres       VARCHAR(100) NOT NULL,
    apellidos     VARCHAR(100) NOT NULL,
    especialidad  VARCHAR(80)  NULL,
    telefono      VARCHAR(20)  NULL,
    email         VARCHAR(120) NULL,
    activo        TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_veterinario_documento (documento)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- CITA (mascota 1:N citas, veterinario 1:N citas)
-- El cliente NO se guarda aqui: se obtiene a traves de la mascota, asi
-- es imposible que una cita tenga una mascota ajena al cliente.
-- ---------------------------------------------------------------------
CREATE TABLE cita (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    mascota_id      BIGINT       NOT NULL,
    veterinario_id  BIGINT       NOT NULL,
    fecha_hora      DATETIME     NOT NULL,
    motivo          VARCHAR(255) NOT NULL,
    estado          VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cita_fecha (fecha_hora),
    KEY idx_cita_veterinario_fecha (veterinario_id, fecha_hora),
    KEY idx_cita_mascota_fecha (mascota_id, fecha_hora),
    CONSTRAINT fk_cita_mascota     FOREIGN KEY (mascota_id)     REFERENCES mascota (id),
    CONSTRAINT fk_cita_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario (id),
    CONSTRAINT ck_cita_estado CHECK (estado IN
        ('PENDIENTE', 'CONFIRMADA', 'ATENDIDA', 'CANCELADA', 'PROGRAMADA'))
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- HISTORIA CLINICA (mascota 1:N historias; cada historia nace de una cita)
-- uk_historia_cita: una cita genera como maximo una historia clinica.
-- uk_historia_id_mascota: permite la FK compuesta de formula_medica.
-- ---------------------------------------------------------------------
CREATE TABLE historia_clinica (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    mascota_id       BIGINT       NOT NULL,
    cita_id          BIGINT       NOT NULL,
    veterinario_id   BIGINT       NOT NULL,
    fecha_hora       DATETIME     NOT NULL,
    motivo_consulta  VARCHAR(255) NOT NULL,
    diagnostico      TEXT         NOT NULL,
    observaciones    TEXT         NULL,
    estado           VARCHAR(20)  NOT NULL DEFAULT 'ABIERTA',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_historia_cita (cita_id),
    UNIQUE KEY uk_historia_id_mascota (id, mascota_id),
    KEY idx_historia_fecha (fecha_hora),
    KEY idx_historia_mascota (mascota_id),
    KEY idx_historia_veterinario (veterinario_id),
    CONSTRAINT fk_historia_mascota     FOREIGN KEY (mascota_id)     REFERENCES mascota (id),
    CONSTRAINT fk_historia_cita        FOREIGN KEY (cita_id)        REFERENCES cita (id),
    CONSTRAINT fk_historia_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario (id),
    CONSTRAINT ck_historia_estado CHECK (estado IN ('ABIERTA', 'CERRADA'))
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- ANOTACION (historia clinica 1:N anotaciones)
-- ---------------------------------------------------------------------
CREATE TABLE anotacion (
    id                   BIGINT   NOT NULL AUTO_INCREMENT,
    historia_clinica_id  BIGINT   NOT NULL,
    veterinario_id       BIGINT   NOT NULL,
    fecha_hora           DATETIME NOT NULL,
    descripcion          TEXT     NOT NULL,
    observacion          TEXT     NULL,
    diagnostico_evolucion TEXT    NOT NULL,
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_anotacion_historia_fecha (historia_clinica_id, fecha_hora),
    CONSTRAINT fk_anotacion_historia     FOREIGN KEY (historia_clinica_id) REFERENCES historia_clinica (id),
    CONSTRAINT fk_anotacion_veterinario  FOREIGN KEY (veterinario_id)      REFERENCES veterinario (id)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- FORMULA MEDICA (historia clinica 1:N formulas)
-- La FK compuesta (historia_clinica_id, mascota_id) garantiza a nivel de
-- base de datos que la formula no se asocie a una consulta de OTRA
-- mascota (regla de negocio 7).
-- "activo" permite inactivar en lugar de eliminar fisicamente (regla 8).
-- ---------------------------------------------------------------------
CREATE TABLE formula_medica (
    id                   BIGINT        NOT NULL AUTO_INCREMENT,
    mascota_id           BIGINT        NOT NULL,
    historia_clinica_id  BIGINT        NOT NULL,
    veterinario_id       BIGINT        NOT NULL,
    fecha_emision        DATETIME      NOT NULL,
    medicamento          VARCHAR(150)  NOT NULL,
    dosis_cantidad       DECIMAL(10,2) NOT NULL,
    dosis_unidad         VARCHAR(20)   NOT NULL,
    frecuencia           VARCHAR(100)  NOT NULL,
    duracion_dias        INT           NOT NULL,
    indicaciones         TEXT          NOT NULL,
    activo               TINYINT(1)    NOT NULL DEFAULT 1,
    created_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_formula_created (created_at),
    KEY idx_formula_mascota (mascota_id),
    KEY idx_formula_veterinario (veterinario_id),
    CONSTRAINT fk_formula_historia_mascota FOREIGN KEY (historia_clinica_id, mascota_id)
        REFERENCES historia_clinica (id, mascota_id),
    CONSTRAINT fk_formula_mascota     FOREIGN KEY (mascota_id)     REFERENCES mascota (id),
    CONSTRAINT fk_formula_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario (id),
    CONSTRAINT ck_formula_dosis    CHECK (dosis_cantidad > 0),
    CONSTRAINT ck_formula_duracion CHECK (duracion_dias > 0)
) ENGINE = InnoDB;

-- =====================================================================
--  DATOS DE PRUEBA
-- =====================================================================
INSERT INTO cliente (documento, nombres, apellidos, telefono, email, direccion) VALUES
 ('1010101010', 'Laura',  'Gomez Ruiz',   '3001112233', 'laura.gomez@mail.com',  'Cra 10 # 20-30, Bogota'),
 ('2020202020', 'Carlos', 'Perez Diaz',   '3012223344', 'carlos.perez@mail.com', 'Cll 45 # 12-08, Bogota'),
 ('3030303030', 'Marta',  'Rojas Silva',  '3023334455', 'marta.rojas@mail.com',  'Av 68 # 30-15, Bogota');

INSERT INTO mascota (cliente_id, nombre, especie, raza, sexo, fecha_nacimiento) VALUES
 (1, 'Max',   'Perro', 'Labrador',  'Macho',  '2020-03-15'),
 (1, 'Luna',  'Gato',  'Siames',    'Hembra', '2021-07-02'),
 (2, 'Rocky', 'Perro', 'Bulldog',   'Macho',  '2019-11-20'),
 (3, 'Nina',  'Gato',  'Criollo',   'Hembra', '2022-01-10');

INSERT INTO veterinario (documento, nombres, apellidos, especialidad, telefono, email) VALUES
 ('9001', 'Andrea',  'Salazar',  'Medicina general', '3105550001', 'andrea.salazar@clinica.com'),
 ('9002', 'Ricardo', 'Medina',   'Cirugia',          '3105550002', 'ricardo.medina@clinica.com'),
 ('9003', 'Sofia',   'Cardenas', 'Dermatologia',     '3105550003', 'sofia.cardenas@clinica.com');

-- Citas futuras (para probar listados y reglas) y una ya atendida
INSERT INTO cita (mascota_id, veterinario_id, fecha_hora, motivo, estado) VALUES
 (1, 1, '2026-09-10 09:00:00', 'Control general',          'ATENDIDA'),
 (3, 2, '2026-09-12 10:30:00', 'Cojera en pata trasera',    'ATENDIDA'),
 (2, 1, '2026-10-20 08:00:00', 'Vacunacion anual',          'PENDIENTE'),
 (4, 3, '2026-10-21 15:00:00', 'Revision de piel',          'CONFIRMADA'),
 (1, 1, '2026-10-22 11:00:00', 'Seguimiento tratamiento',   'PROGRAMADA');

INSERT INTO historia_clinica
    (mascota_id, cita_id, veterinario_id, fecha_hora, motivo_consulta, diagnostico, observaciones, estado) VALUES
 (1, 1, 1, '2026-09-10 09:00:00', 'Control general',       'Otitis externa leve', 'Se recomienda limpieza semanal', 'ABIERTA'),
 (3, 2, 2, '2026-09-12 10:30:00', 'Cojera en pata trasera','Esguince grado I',    'Reposo relativo 10 dias',       'ABIERTA');

INSERT INTO anotacion
    (historia_clinica_id, veterinario_id, fecha_hora, descripcion, observacion, diagnostico_evolucion) VALUES
 (1, 1, '2026-09-10 09:20:00', 'Examen otoscopico', 'Eritema en oido izquierdo', 'Otitis externa leve'),
 (1, 1, '2026-09-24 09:00:00', 'Control a los 14 dias', 'Sin secrecion', 'Evolucion favorable'),
 (2, 2, '2026-09-12 10:45:00', 'Palpacion y radiografia', 'Sin fractura visible', 'Esguince grado I');

INSERT INTO formula_medica
    (mascota_id, historia_clinica_id, veterinario_id, fecha_emision, medicamento,
     dosis_cantidad, dosis_unidad, frecuencia, duracion_dias, indicaciones) VALUES
 (1, 1, 1, '2026-09-10 09:30:00', 'Gotas oticas con gentamicina', 4.00, 'gotas', 'Cada 12 horas', 7,
  'Aplicar en el oido izquierdo despues de limpiar'),
 (3, 2, 2, '2026-09-12 11:00:00', 'Meloxicam', 1.50, 'mg/kg', 'Cada 24 horas', 5,
  'Administrar con comida');
