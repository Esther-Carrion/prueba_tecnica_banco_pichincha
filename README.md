# Sistema Bancario - Banco Pichincha

## 📋 Descripción General

Este proyecto es un sistema bancario completo que incluye tanto el backend (microservicio) como el frontend (Single Page Application). Permite la gestión integral de clientes, cuentas bancarias, movimientos y generación de reportes.

## 🏗️ Arquitectura del Sistema

### Backend - Microservicio (MSA)
- **Tecnología**: Java + Spring Boot
- **Base de Datos**: Configuración con esquemas SQL
- **API**: RESTful endpoints
- **Contenedores**: Docker support

### Frontend - SPA (Single Page Application)
- **Framework**: Angular 17+
- **Estilos**: SCSS con diseño responsivo
- **UI**: Material Icons integration
- **Arquitectura**: Componentes modulares

## 🚀 Funcionalidades Principales

### 👥 Gestión de Clientes
- ✅ Crear nuevos clientes
- ✅ Visualizar lista de clientes
- ✅ Editar información de clientes
- ✅ Eliminar clientes (con validaciones)
- ✅ Búsqueda y filtrado

### 🏦 Gestión de Cuentas
- ✅ Crear cuentas bancarias
- ✅ Asociar cuentas a clientes
- ✅ Diferentes tipos de cuenta (Ahorros, Corriente)
- ✅ Visualizar saldos y estados
- ✅ Activar/Desactivar cuentas

### 💰 Gestión de Movimientos
- ✅ Registrar depósitos y retiros
- ✅ Validaciones de saldo disponible
- ✅ Historial de transacciones
- ✅ Estados de movimientos
- ✅ Filtrado por fecha y tipo

### 📊 Reportes
- ✅ Reportes por cliente y período
- ✅ Resumen de ingresos y egresos
- ✅ Saldo inicial, movimientos y saldo final
- ✅ Exportación a PDF
- ✅ Filtros avanzados

## 🛠️ Requisitos del Sistema

### Backend
- Java 11 o superior
- Gradle
- Base de datos compatible (PostgreSQL/MySQL)
- Docker (opcional)

### Frontend
- Node.js 18 o superior
- npm o yarn
- Angular CLI