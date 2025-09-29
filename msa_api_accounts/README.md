# API Bancaria - Ejercicio Técnico Banco Pichincha

## Descripción
API REST para gestión de clientes, cuentas bancarias y movimientos financieros. Implementa arquitectura hexagonal con Spring Boot, JPA, PostgreSQL y generación de reportes en PDF y JSON.

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.5.6
- Spring Data JPA
- PostgreSQL
- MapStruct para mapeos
- OpenAPI 3.0 para documentación
- Docker y Docker Compose
- iText para generación de PDFs
- JUnit 5 para pruebas

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/com/pichincha/accounts/
│   │   ├── domain/                 # Entidades de dominio
│   │   ├── application/
│   │   │   ├── port/              # Puertos (interfaces)
│   │   │   └── service/           # Servicios de aplicación
│   │   └── infrastructure/
│   │       ├── adapter/
│   │       │   ├── persistence/   # Adaptadores JPA
│   │       │   └── rest/          # Controladores REST
│   │       ├── entity/            # Entidades JPA
│   │       ├── mapper/            # Mappers MapStruct
│   │       └── repository/        # Repositorios JPA
│   └── resources/
│       └── application.yml        # Configuración
├── test/                          # Pruebas unitarias
├── BaseDatos.sql                  # Script de base de datos
├── docker-compose.yml             # Orquestación de contenedores
├── Dockerfile                     # Imagen Docker
└── openapi.yml                    # Especificación OpenAPI
```

## Entidades del Dominio

### Person (Clase Base)
- ID, nombre, género, edad, identificación, dirección, teléfono

### Client (Hereda de Person)
- Username, contraseña, estado

### Account
- ID, número de cuenta, tipo (AHORRO/CORRIENTE), saldo inicial, saldo actual, estado, cliente

### Movement
- ID, fecha, tipo de movimiento, valor, saldo, cuenta

## Endpoints Principales

### Clientes (/api/clientes)
- `GET /api/clientes` - Listar todos los clientes
- `POST /api/clientes` - Crear cliente
- `GET /api/clientes/{id}` - Obtener cliente por ID
- `PUT /api/clientes/{id}` - Actualizar cliente
- `DELETE /api/clientes/{id}` - Eliminar cliente

### Cuentas (/api/cuentas)
- `GET /api/cuentas` - Listar todas las cuentas
- `POST /api/cuentas` - Crear cuenta
- `GET /api/cuentas/{id}` - Obtener cuenta por ID
- `PUT /api/cuentas/{id}` - Actualizar cuenta
- `DELETE /api/cuentas/{id}` - Eliminar cuenta
- `GET /api/cuentas/cliente/{clientId}` - Cuentas por cliente

### Movimientos (/api/movimientos)
- `GET /api/movimientos` - Listar movimientos
- `POST /api/movimientos` - Crear movimiento
- `GET /api/movimientos/{id}` - Obtener movimiento por ID
- `GET /api/movimientos/cuenta/{accountId}` - Movimientos por cuenta

### Reportes (/api/reportes)
- `GET /api/reportes?clientId={id}&startDate={date}&endDate={date}&format={JSON|PDF}` - Generar reporte

## Instrucciones de Despliegue

### Prerequisitos
- Docker y Docker Compose instalados
- Java 17 (para desarrollo local)
- Git

### Opción 1: Despliegue con Docker Compose (Recomendado)

1. **Clonar el repositorio**
```bash
git clone <url-del-repositorio>
cd accounts
```

2. **Desplegar con Docker Compose**
```bash
docker-compose up --build
```

Esto desplegará:
- PostgreSQL en puerto 5432
- Aplicación Spring Boot en puerto 8080

3. **Verificar despliegue**
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

### Opción 2: Despliegue Manual

1. **Configurar PostgreSQL**
```sql
CREATE DATABASE accounts_db;
-- Ejecutar BaseDatos.sql
```

2. **Configurar variables de entorno**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/accounts_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres123
```

3. **Ejecutar aplicación**
```bash
./gradlew clean build
java -jar build/libs/accounts-0.0.1-SNAPSHOT.jar
```

## Datos de Prueba

El script `BaseDatos.sql` incluye datos de prueba según el ejercicio:

### Clientes
- Jose Lema (ID: 550e8400-e29b-41d4-a716-446655440001)
- Marianela Montalvo (ID: 550e8400-e29b-41d4-a716-446655440002)
- Juan Osorio (ID: 550e8400-e29b-41d4-a716-446655440003)

### Cuentas
- 478758 (Ahorro) - Jose Lema
- 225487 (Corriente) - Marianela Montalvo
- 495878 (Ahorro) - Juan Osorio
- 496825 (Ahorro) - Marianela Montalvo
- 585545 (Corriente) - Jose Lema

## Validación con Postman

1. **Importar colección**
```
Archivo: Banco_Pichincha_API_Tests.postman_collection.json
```

2. **Configurar variables**
- baseUrl: http://localhost:8080
- clientId: 550e8400-e29b-41d4-a716-446655440001
- accountId: 660e8400-e29b-41d4-a716-446655440001

3. **Ejecutar pruebas**
- Crear clientes, cuentas y movimientos
- Generar reportes en JSON y PDF
- Validar reglas de negocio

## Casos de Uso de Ejemplo

### 1. Crear Movimiento de Depósito
```json
POST /api/movimientos
{
  "tipo": "CREDITO",
  "valor": 600.00,
  "numeroCuenta": "225487"
}
```

### 2. Crear Movimiento de Retiro
```json
POST /api/movimientos
{
  "tipo": "DEBITO",
  "valor": -575.00,
  "numeroCuenta": "478758"
}
```

### 3. Generar Reporte
```
GET /api/reportes?clientId=550e8400-e29b-41d4-a716-446655440002&startDate=2022-02-01&endDate=2022-02-28&format=PDF
```

## Reglas de Negocio Implementadas

1. **Validación de Saldo**: No permitir retiros que excedan el saldo disponible
2. **Cuentas Activas**: Solo permitir movimientos en cuentas activas
3. **Tipos de Movimiento**: 
   - Valores positivos = Créditos (depósitos)
   - Valores negativos = Débitos (retiros)
4. **Generación Automática**: Números de cuenta y usernames únicos
5. **Reportes**: Estados de cuenta con totales de créditos y débitos

## Pruebas Unitarias

Ejecutar pruebas:
```bash
./gradlew test
```

Las pruebas cubren:
- Controladores REST (ClientController, MovementController)
- Servicios de aplicación
- Mappers
- Validaciones de negocio

## Monitoreo

- Health Check: `/actuator/health`
- Métricas: `/actuator/metrics`
- Info: `/actuator/info`

## Arquitectura

El proyecto implementa **Arquitectura Hexagonal (Ports & Adapters)**:

- **Dominio**: Entidades y reglas de negocio
- **Aplicación**: Casos de uso y puertos
- **Infraestructura**: Adaptadores para REST, JPA, etc.

Esta arquitectura permite:
- Testabilidad
- Flexibilidad
- Separación de responsabilidades
- Fácil mantenimiento