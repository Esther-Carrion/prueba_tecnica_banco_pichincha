-- BaseDatos.sql
-- Script de base de datos para el ejercicio técnico Banco Pichincha

-- Crear extensión para UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabla clients (incluye los datos de Person)
CREATE TABLE IF NOT EXISTS clients (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(20) NOT NULL CHECK (gender IN ('MASCULINO', 'FEMENINO')),
    age INTEGER,
    identification VARCHAR(50) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(500),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    state BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla accounts
CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_number VARCHAR(20) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('AHORRO', 'CORRIENTE')),
    initial_balance DECIMAL(15,2) NOT NULL,
    current_balance DECIMAL(15,2) NOT NULL,
    state BOOLEAN NOT NULL DEFAULT true,
    client_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- Tabla movements
CREATE TABLE IF NOT EXISTS movements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    movement_type VARCHAR(20) NOT NULL CHECK (movement_type IN ('CREDITO', 'DEBITO')),
    value DECIMAL(15,2) NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    balance_before DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    account_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Índices para mejor rendimiento
CREATE INDEX IF NOT EXISTS idx_clients_identification ON clients(identification);
CREATE INDEX IF NOT EXISTS idx_clients_username ON clients(username);
CREATE INDEX IF NOT EXISTS idx_accounts_number ON accounts(account_number);
CREATE INDEX IF NOT EXISTS idx_accounts_client ON accounts(client_id);
CREATE INDEX IF NOT EXISTS idx_movements_account ON movements(account_id);
CREATE INDEX IF NOT EXISTS idx_movements_date ON movements(date);
CREATE INDEX IF NOT EXISTS idx_movements_account_date ON movements(account_id, date);

-- Limpiar datos existentes
DELETE FROM movements;
DELETE FROM accounts;
DELETE FROM clients;

-- Datos de prueba según el ejercicio
-- 1. Creación de Usuarios
INSERT INTO clients (id, name, gender, age, identification, phone, address, username, password, state) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Jose Lema', 'MASCULINO', 35, '1234567890', '098254785', 'Otavalo sn y principal', 'jlema', '1234', true),
('550e8400-e29b-41d4-a716-446655440002', 'Marianela Montalvo', 'FEMENINO', 28, '0987654321', '097548965', 'Amazonas y NNUU', 'mmontalvo', '5678', true),
('550e8400-e29b-41d4-a716-446655440003', 'Juan Osorio', 'MASCULINO', 42, '1122334455', '098874587', '13 junio y Equinoccial', 'josorio', '1245', true);

-- 2. Creación de Cuentas de Usuario
INSERT INTO accounts (id, account_number, type, initial_balance, current_balance, state, client_id) VALUES
('660e8400-e29b-41d4-a716-446655440001', '478758', 'AHORRO', 2000.00, 2000.00, true, '550e8400-e29b-41d4-a716-446655440001'),
('660e8400-e29b-41d4-a716-446655440002', '225487', 'CORRIENTE', 100.00, 100.00, true, '550e8400-e29b-41d4-a716-446655440002'),
('660e8400-e29b-41d4-a716-446655440003', '495878', 'AHORRO', 0.00, 0.00, true, '550e8400-e29b-41d4-a716-446655440003'),
('660e8400-e29b-41d4-a716-446655440004', '496825', 'AHORRO', 540.00, 540.00, true, '550e8400-e29b-41d4-a716-446655440002'),
('660e8400-e29b-41d4-a716-446655440005', '585545', 'CORRIENTE', 1000.00, 1000.00, true, '550e8400-e29b-41d4-a716-446655440001');

-- 3. Movimientos iniciales
INSERT INTO movements (id, date, movement_type, value, balance, balance_before, balance_after, account_id) VALUES
('770e8400-e29b-41d4-a716-446655440001', '2022-02-10 10:00:00', 'DEBITO', -575.00, 1425.00, 2000.00, 1425.00, '660e8400-e29b-41d4-a716-446655440001'),
('770e8400-e29b-41d4-a716-446655440002', '2022-02-10 11:00:00', 'CREDITO', 600.00, 700.00, 100.00, 700.00, '660e8400-e29b-41d4-a716-446655440002'),
('770e8400-e29b-41d4-a716-446655440003', '2022-02-08 09:00:00', 'CREDITO', 150.00, 150.00, 0.00, 150.00, '660e8400-e29b-41d4-a716-446655440003'),
('770e8400-e29b-41d4-a716-446655440004', '2022-02-08 14:00:00', 'DEBITO', -540.00, 0.00, 540.00, 0.00, '660e8400-e29b-41d4-a716-446655440004');

-- Actualizar saldos actuales en las cuentas
UPDATE accounts SET current_balance = 1425.00 WHERE id = '660e8400-e29b-41d4-a716-446655440001';
UPDATE accounts SET current_balance = 700.00 WHERE id = '660e8400-e29b-41d4-a716-446655440002';
UPDATE accounts SET current_balance = 150.00 WHERE id = '660e8400-e29b-41d4-a716-446655440003';
UPDATE accounts SET current_balance = 0.00 WHERE id = '660e8400-e29b-41d4-a716-446655440004';