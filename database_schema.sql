-- =============================================================================
-- SCRIPT DE BASE DE DATOS POSTGRESQL - SISTEMA BANCARIO
-- Proyecto: Banco Pichincha - Arquitectura Hexagonal
-- Autor: Sistema de Cuentas Bancarias
-- Fecha: 2025-09-28
-- =============================================================================

-- Configuración inicial
SET client_min_messages = WARNING;

-- =============================================================================
-- ELIMINAR TABLAS EXISTENTES (SI EXISTEN)
-- =============================================================================

-- Eliminar restricciones de clave foránea
ALTER TABLE IF EXISTS movimiento DROP CONSTRAINT IF EXISTS FK4ea11fe7p3xa1kwwmdgi9f2fi;
ALTER TABLE IF EXISTS cuenta DROP CONSTRAINT IF EXISTS FK4p224uogyy5hmxvn8fwa2jlug;

-- Eliminar tablas en orden correcto
DROP TABLE IF EXISTS movimiento CASCADE;
DROP TABLE IF EXISTS cuenta CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;

-- =============================================================================
-- CREAR TABLAS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- TABLA: cliente
-- Descripción: Almacena información de clientes (hereda de Persona)
-- -----------------------------------------------------------------------------
CREATE TABLE cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    genero VARCHAR(20) CHECK (genero IN ('MASCULINO', 'FEMENINO', 'OTRO')),
    edad INTEGER,
    identificacion VARCHAR(50) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(500),
    password VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para cliente
CREATE INDEX idx_cliente_cliente_id ON cliente(cliente_id);
CREATE INDEX idx_cliente_identificacion ON cliente(identificacion);
CREATE INDEX idx_cliente_estado ON cliente(estado);

-- -----------------------------------------------------------------------------
-- TABLA: cuenta
-- Descripción: Almacena información de cuentas bancarias
-- -----------------------------------------------------------------------------
CREATE TABLE cuenta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('AHORROS', 'CORRIENTE')),
    saldo_inicial NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    saldo_actual NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT chk_cuenta_saldo_inicial CHECK (saldo_inicial >= 0),
    CONSTRAINT chk_cuenta_saldo_actual CHECK (saldo_actual >= 0)
);

-- Índices para cuenta
CREATE INDEX idx_cuenta_numero ON cuenta(numero_cuenta);
CREATE INDEX idx_cuenta_cliente_id ON cuenta(cliente_id);
CREATE INDEX idx_cuenta_estado ON cuenta(estado);
CREATE INDEX idx_cuenta_tipo ON cuenta(tipo);

-- -----------------------------------------------------------------------------
-- TABLA: movimiento
-- Descripción: Almacena movimientos bancarios (transacciones)
-- -----------------------------------------------------------------------------
CREATE TABLE movimiento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cuenta_id UUID NOT NULL,
    fecha TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(30) NOT NULL CHECK (tipo_movimiento IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA_IN', 'TRANSFERENCIA_OUT')),
    valor NUMERIC(15,2) NOT NULL,
    saldo_despues NUMERIC(15,2) NOT NULL,
    descripcion VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT chk_movimiento_valor CHECK (valor <> 0),
    CONSTRAINT chk_movimiento_saldo_despues CHECK (saldo_despues >= 0)
);

-- Índices para movimiento
CREATE INDEX idx_movimiento_cuenta_id ON movimiento(cuenta_id);
CREATE INDEX idx_movimiento_fecha ON movimiento(fecha);
CREATE INDEX idx_movimiento_tipo ON movimiento(tipo_movimiento);
CREATE INDEX idx_movimiento_fecha_cuenta ON movimiento(cuenta_id, fecha);

-- =============================================================================
-- CREAR RELACIONES (CLAVES FORÁNEAS)
-- =============================================================================

-- Relación cuenta -> cliente
ALTER TABLE cuenta 
ADD CONSTRAINT FK4p224uogyy5hmxvn8fwa2jlug 
FOREIGN KEY (cliente_id) REFERENCES cliente(id) 
ON DELETE RESTRICT ON UPDATE CASCADE;

-- Relación movimiento -> cuenta
ALTER TABLE movimiento 
ADD CONSTRAINT FK4ea11fe7p3xa1kwwmdgi9f2fi 
FOREIGN KEY (cuenta_id) REFERENCES cuenta(id) 
ON DELETE RESTRICT ON UPDATE CASCADE;

-- =============================================================================
-- CREAR FUNCIONES Y TRIGGERS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- FUNCIÓN: Actualizar timestamp de updated_at
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- -----------------------------------------------------------------------------
-- TRIGGERS: Para actualizar automáticamente updated_at
-- -----------------------------------------------------------------------------
CREATE TRIGGER update_cliente_updated_at 
    BEFORE UPDATE ON cliente 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_cuenta_updated_at 
    BEFORE UPDATE ON cuenta 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- -----------------------------------------------------------------------------
-- FUNCIÓN: Validar saldo antes de movimiento
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION validar_saldo_movimiento()
RETURNS TRIGGER AS $$
DECLARE
    saldo_actual_cuenta NUMERIC(15,2);
    cliente_activo BOOLEAN;
    cuenta_activa BOOLEAN;
BEGIN
    -- Obtener información de la cuenta y cliente
    SELECT c.saldo_actual, c.estado, cl.estado
    INTO saldo_actual_cuenta, cuenta_activa, cliente_activo
    FROM cuenta c
    JOIN cliente cl ON c.cliente_id = cl.id
    WHERE c.id = NEW.cuenta_id;
    
    -- Validar que el cliente esté activo
    IF NOT cliente_activo THEN
        RAISE EXCEPTION 'No se puede realizar movimientos en cuentas de clientes inactivos';
    END IF;
    
    -- Validar que la cuenta esté activa
    IF NOT cuenta_activa THEN
        RAISE EXCEPTION 'No se puede realizar movimientos en cuentas inactivas';
    END IF;
    
    -- Validar saldo para débitos (valores negativos)
    IF NEW.valor < 0 AND ABS(NEW.valor) > saldo_actual_cuenta THEN
        RAISE EXCEPTION 'Saldo no disponible. Saldo actual: %, Valor solicitado: %', 
                        saldo_actual_cuenta, ABS(NEW.valor);
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- -----------------------------------------------------------------------------
-- TRIGGER: Validar saldo antes de insertar movimiento
-- -----------------------------------------------------------------------------
CREATE TRIGGER trigger_validar_saldo_movimiento
    BEFORE INSERT ON movimiento
    FOR EACH ROW EXECUTE FUNCTION validar_saldo_movimiento();

-- =============================================================================
-- DATOS DE PRUEBA SEGÚN CUSTOM INSTRUCTIONS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- CLIENTES DE PRUEBA
-- -----------------------------------------------------------------------------
INSERT INTO cliente (cliente_id, nombre, genero, edad, identificacion, telefono, direccion, password, estado) 
VALUES 
    ('CLI001', 'Jose Lema', 'MASCULINO', 35, '1234567890', '098254785', 'Otavalo sn y principal', '$2a$10$encrypted_password_1', TRUE),
    ('CLI002', 'Marianela Montalvo', 'FEMENINO', 28, '0987654321', '097548965', 'Amazonas y NNUU', '$2a$10$encrypted_password_2', TRUE),
    ('CLI003', 'Juan Osorio', 'MASCULINO', 42, '1122334455', '098765432', '13 junio y Equinoccial', '$2a$10$encrypted_password_3', TRUE);

-- -----------------------------------------------------------------------------
-- CUENTAS DE PRUEBA
-- -----------------------------------------------------------------------------
INSERT INTO cuenta (cliente_id, numero_cuenta, tipo, saldo_inicial, saldo_actual, estado) 
VALUES 
    ((SELECT id FROM cliente WHERE cliente_id = 'CLI001'), '478758', 'AHORROS', 2000.00, 2000.00, TRUE),
    ((SELECT id FROM cliente WHERE cliente_id = 'CLI002'), '225487', 'CORRIENTE', 100.00, 100.00, TRUE),
    ((SELECT id FROM cliente WHERE cliente_id = 'CLI002'), '495878', 'AHORROS', 0.00, 0.00, TRUE),
    ((SELECT id FROM cliente WHERE cliente_id = 'CLI003'), '496825', 'AHORROS', 1000.00, 1000.00, TRUE),
    ((SELECT id FROM cliente WHERE cliente_id = 'CLI003'), '588545', 'CORRIENTE', 1000.00, 1000.00, TRUE);

-- -----------------------------------------------------------------------------
-- MOVIMIENTOS DE PRUEBA
-- -----------------------------------------------------------------------------
-- Movimientos para Jose Lema (Cuenta 478758)
INSERT INTO movimiento (cuenta_id, fecha, tipo_movimiento, valor, saldo_despues, descripcion) 
VALUES 
    ((SELECT id FROM cuenta WHERE numero_cuenta = '478758'), '2022-02-10 09:00:00', 'RETIRO', -575.00, 1425.00, 'Retiro en cajero automático');

-- Movimientos para Marianela Montalvo (Cuenta 225487)
INSERT INTO movimiento (cuenta_id, fecha, tipo_movimiento, valor, saldo_despues, descripcion) 
VALUES 
    ((SELECT id FROM cuenta WHERE numero_cuenta = '225487'), '2022-02-10 10:00:00', 'DEPOSITO', 600.00, 700.00, 'Depósito en ventanilla'),
    ((SELECT id FROM cuenta WHERE numero_cuenta = '225487'), '2022-02-10 15:30:00', 'RETIRO', -200.00, 500.00, 'Retiro por transferencia');

-- Movimientos para Juan Osorio (Cuenta 496825)
INSERT INTO movimiento (cuenta_id, fecha, tipo_movimiento, valor, saldo_despues, descripcion) 
VALUES 
    ((SELECT id FROM cuenta WHERE numero_cuenta = '496825'), '2022-02-10 11:00:00', 'DEPOSITO', 150.00, 1150.00, 'Depósito desde otra cuenta'),
    ((SELECT id FROM cuenta WHERE numero_cuenta = '588545'), '2022-02-10 14:00:00', 'RETIRO', -540.00, 460.00, 'Pago de servicios');

-- Actualizar saldos actuales en las cuentas según los movimientos
UPDATE cuenta SET saldo_actual = 1425.00 WHERE numero_cuenta = '478758';
UPDATE cuenta SET saldo_actual = 500.00 WHERE numero_cuenta = '225487';
UPDATE cuenta SET saldo_actual = 1150.00 WHERE numero_cuenta = '496825';
UPDATE cuenta SET saldo_actual = 460.00 WHERE numero_cuenta = '588545';

-- =============================================================================
-- CREAR VISTAS PARA REPORTES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- VISTA: Resumen de cuentas por cliente
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vista_resumen_cliente AS
SELECT 
    cl.cliente_id,
    cl.nombre AS nombre_cliente,
    cl.identificacion,
    COUNT(c.id) AS total_cuentas,
    SUM(c.saldo_actual) AS saldo_total,
    COUNT(CASE WHEN c.tipo = 'AHORROS' THEN 1 END) AS cuentas_ahorros,
    COUNT(CASE WHEN c.tipo = 'CORRIENTE' THEN 1 END) AS cuentas_corrientes
FROM cliente cl
LEFT JOIN cuenta c ON cl.id = c.cliente_id AND c.estado = TRUE
WHERE cl.estado = TRUE
GROUP BY cl.id, cl.cliente_id, cl.nombre, cl.identificacion;

-- -----------------------------------------------------------------------------
-- VISTA: Estado de cuenta detallado
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vista_estado_cuenta AS
SELECT 
    cl.cliente_id,
    cl.nombre AS nombre_cliente,
    c.numero_cuenta,
    c.tipo AS tipo_cuenta,
    c.saldo_inicial,
    c.saldo_actual,
    c.estado AS cuenta_activa,
    m.fecha,
    m.tipo_movimiento,
    m.valor,
    m.saldo_despues,
    m.descripcion
FROM cliente cl
JOIN cuenta c ON cl.id = c.cliente_id
LEFT JOIN movimiento m ON c.id = m.cuenta_id
ORDER BY cl.cliente_id, c.numero_cuenta, m.fecha DESC;

-- =============================================================================
-- FUNCIONES DE UTILIDAD PARA REPORTES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- FUNCIÓN: Generar reporte por cliente y rango de fechas
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION generar_reporte_cliente(
    p_cliente_id VARCHAR(255),
    p_fecha_inicio DATE,
    p_fecha_fin DATE
)
RETURNS TABLE (
    cliente VARCHAR(255),
    numero_cuenta VARCHAR(20),
    tipo_cuenta VARCHAR(20),
    saldo_inicial NUMERIC(15,2),
    fecha TIMESTAMP,
    tipo_movimiento VARCHAR(30),
    valor NUMERIC(15,2),
    saldo_disponible NUMERIC(15,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        cl.nombre,
        c.numero_cuenta,
        c.tipo,
        c.saldo_inicial,
        m.fecha,
        m.tipo_movimiento,
        m.valor,
        m.saldo_despues
    FROM cliente cl
    JOIN cuenta c ON cl.id = c.cliente_id
    LEFT JOIN movimiento m ON c.id = m.cuenta_id
    WHERE cl.cliente_id = p_cliente_id
        AND (m.fecha IS NULL OR (m.fecha >= p_fecha_inicio::timestamp AND m.fecha <= (p_fecha_fin + INTERVAL '1 day')::timestamp))
    ORDER BY c.numero_cuenta, m.fecha DESC;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- COMENTARIOS EN TABLAS Y COLUMNAS
-- =============================================================================

-- Comentarios en tablas
COMMENT ON TABLE cliente IS 'Tabla de clientes del sistema bancario';
COMMENT ON TABLE cuenta IS 'Tabla de cuentas bancarias';
COMMENT ON TABLE movimiento IS 'Tabla de movimientos/transacciones bancarias';

-- Comentarios en columnas principales
COMMENT ON COLUMN cliente.cliente_id IS 'Identificador único del cliente generado por el sistema';
COMMENT ON COLUMN cliente.identificacion IS 'Número de identificación oficial (cédula, pasaporte, etc.)';
COMMENT ON COLUMN cuenta.numero_cuenta IS 'Número único de la cuenta bancaria';
COMMENT ON COLUMN cuenta.saldo_inicial IS 'Saldo con el que se abrió la cuenta';
COMMENT ON COLUMN cuenta.saldo_actual IS 'Saldo actual de la cuenta';
COMMENT ON COLUMN movimiento.tipo_movimiento IS 'Tipo de movimiento: DEPOSITO, RETIRO, TRANSFERENCIA_IN, TRANSFERENCIA_OUT';
COMMENT ON COLUMN movimiento.valor IS 'Valor del movimiento (positivo para créditos, negativo para débitos)';
COMMENT ON COLUMN movimiento.saldo_despues IS 'Saldo de la cuenta después del movimiento';

-- =============================================================================
-- PERMISOS Y SEGURIDAD
-- =============================================================================

-- Crear usuario para la aplicación (opcional - ajustar según necesidades)
-- CREATE USER app_accounts WITH PASSWORD 'secure_password_2025';
-- GRANT CONNECT ON DATABASE arquitecturaut_bdd_accounts TO app_accounts;
-- GRANT USAGE ON SCHEMA public TO app_accounts;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_accounts;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_accounts;

-- =============================================================================
-- CONSULTAS DE VERIFICACIÓN
-- =============================================================================

-- Verificar creación de tablas
SELECT 
    table_name, 
    table_type
FROM information_schema.tables 
WHERE table_schema = 'public' 
    AND table_name IN ('cliente', 'cuenta', 'movimiento')
ORDER BY table_name;

-- Verificar datos de prueba
SELECT 'CLIENTES' as tabla, COUNT(*) as registros FROM cliente
UNION ALL
SELECT 'CUENTAS' as tabla, COUNT(*) as registros FROM cuenta
UNION ALL
SELECT 'MOVIMIENTOS' as tabla, COUNT(*) as registros FROM movimiento;

-- Verificar relaciones
SELECT 
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
    AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
    AND tc.table_name IN ('cuenta', 'movimiento');

-- =============================================================================
-- FIN DEL SCRIPT
-- =============================================================================

SELECT 'Script de base de datos ejecutado correctamente.' as mensaje,
       CURRENT_TIMESTAMP as fecha_ejecucion;