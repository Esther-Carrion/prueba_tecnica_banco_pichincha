# 📋 Colección de Postman - Banco Pichincha API

Esta colección contiene todas las pruebas necesarias para validar el funcionamiento del microservicio de cuentas del Banco Pichincha.

## 📁 Archivos incluidos

- `Banco_Pichincha_API_COMPLETA.postman_collection.json` - Colección principal con todas las pruebas
- `Banco_Pichincha_Local.postman_environment.json` - Variables de entorno para desarrollo local

## 🚀 Cómo usar la colección

### 1. Importar en Postman

1. Abrir Postman
2. Hacer clic en **Import**
3. Seleccionar ambos archivos JSON
4. Confirmar la importación

### 2. Configurar el entorno

1. En Postman, seleccionar el environment **"Banco Pichincha - Local Development"**
2. Verificar que la variable `baseUrl` apunte a `http://localhost:8081`
3. Asegurarse que el microservicio esté corriendo en el puerto 8081

### 3. Orden de ejecución recomendado

#### 📊 Paso 1: Verificar que la API esté funcionando
```
1. Health Check → Health Check
```

#### 👥 Paso 2: Crear clientes de prueba
```
2. Clientes → Crear Cliente - Jose Lema
2. Clientes → Crear Cliente - Marianela Montalvo  
2. Clientes → Crear Cliente - Juan Osorio
```

#### 🏦 Paso 3: Crear cuentas para los clientes
```
3. Cuentas → Crear Cuenta Ahorros - Jose Lema
3. Cuentas → Crear Cuenta Corriente - Marianela Montalvo
3. Cuentas → Crear Cuenta Ahorros - Juan Osorio
3. Cuentas → Crear Cuenta Corriente - Juan Osorio
```

#### 💰 Paso 4: Realizar movimientos
```
4. Movimientos → Depósito +600 - Cuenta Corriente Marianela
4. Movimientos → Retiro -575 - Cuenta Ahorros Jose
4. Movimientos → Depósito +1000 - Cuenta Ahorros Juan
4. Movimientos → Retiro -540 - Cuenta Corriente Juan
```

#### 📈 Paso 5: Generar reportes
```
5. Reportes → Generar Reporte JSON - Jose Lema
5. Reportes → Generar Reporte JSON - Marianela Montalvo
5. Reportes → Generar Reporte JSON - Juan Osorio
5. Reportes → Generar Reporte Formateado - Jose Lema
5. Reportes → Generar Reporte PDF Base64 - Jose Lema
```

## 📋 Estructura de la colección

### 1. Health Check
- Verificar estado del microservicio

### 2. Clientes
- ✅ Crear cliente
- 📋 Obtener todos los clientes
- 🔍 Obtener cliente por ID
- 🔍 Obtener cliente por ClientId
- ✏️ Actualizar cliente
- ❓ Verificar existencia por ClientId
- ❓ Verificar existencia por identificación

### 3. Cuentas
- ✅ Crear cuenta (Ahorros/Corriente)
- 📋 Obtener todas las cuentas
- 🔍 Obtener cuenta por ID
- 🔍 Obtener cuenta por número
- 🔍 Obtener cuentas por cliente
- ✏️ Actualizar cuenta
- ❓ Verificar existencia por número

### 4. Movimientos
- ✅ Crear movimiento (Depósito/Retiro)
- 📋 Obtener todos los movimientos
- 🔍 Obtener movimiento por ID
- 🔍 Obtener movimientos por cuenta
- 🔍 Obtener movimientos por rango de fechas

### 5. Reportes
- 📊 Reporte JSON completo
- 📊 Reporte formateado
- 📄 Reporte PDF
- 📄 Reporte PDF en Base64

### 6. Casos de Error
- ❌ Cliente con identificación duplicada
- ❌ Cuenta con número duplicado
- ❌ Retiro superior al saldo
- ❌ Buscar entidades inexistentes

### 7. Cleanup
- 🗑️ Eliminar datos de prueba

## 🔧 Variables automáticas

La colección utiliza **scripts de test** que automáticamente:

- Capturan IDs generados (clientId1, accountId1, etc.)
- Validan códigos de respuesta HTTP
- Verifican estructura de respuestas
- Configuran variables para requests posteriores

## 🧪 Datos de prueba

### Clientes creados:
1. **Jose Lema** (jlema001) - Identificación: 1234567890
2. **Marianela Montalvo** (mmontalvo002) - Identificación: 0987654321
3. **Juan Osorio** (josorio003) - Identificación: 1122334455

### Cuentas creadas:
1. **478758** - Ahorros - Jose Lema - $2000.00
2. **225487** - Corriente - Marianela Montalvo - $100.00
3. **495878** - Ahorros - Juan Osorio - $0.00
4. **496825** - Corriente - Juan Osorio - $540.00

### Movimientos realizados:
1. **+$600** - Depósito en cuenta 225487 (Marianela)
2. **-$575** - Retiro en cuenta 478758 (Jose)
3. **+$1000** - Depósito en cuenta 495878 (Juan)
4. **-$540** - Retiro en cuenta 496825 (Juan)

## 📝 Notas importantes

1. **Orden de ejecución**: Seguir el orden recomendado para que las variables se configuren correctamente
2. **Variables automáticas**: Los IDs se capturan automáticamente mediante scripts
3. **Validaciones**: Cada request incluye validaciones básicas
4. **Casos de error**: Incluye pruebas para validar manejo de errores
5. **Cleanup**: Usar la sección 7 para limpiar datos de prueba

## 🔍 Endpoints principales

```
GET  /actuator/health                    - Health check
POST /api/clientes                       - Crear cliente
GET  /api/clientes                       - Listar clientes
GET  /api/clientes/{id}                  - Obtener cliente por ID
PUT  /api/clientes/{id}                  - Actualizar cliente
DELETE /api/clientes/{id}                - Eliminar cliente

POST /api/cuentas                        - Crear cuenta
GET  /api/cuentas                        - Listar cuentas
GET  /api/cuentas/{id}                   - Obtener cuenta por ID
GET  /api/cuentas/number/{number}        - Obtener cuenta por número
GET  /api/cuentas/client/{clientId}      - Obtener cuentas por cliente
PUT  /api/cuentas/{id}                   - Actualizar cuenta
DELETE /api/cuentas/{id}                 - Eliminar cuenta

POST /api/movimientos                    - Crear movimiento
GET  /api/movimientos                    - Listar movimientos
GET  /api/movimientos/{id}               - Obtener movimiento por ID
GET  /api/movimientos/account/{accountId} - Obtener movimientos por cuenta

GET  /api/reportes                       - Generar reporte JSON
GET  /api/reportes/formatted             - Generar reporte formateado
GET  /api/reportes/pdf                   - Generar reporte PDF
GET  /api/reportes/pdf/base64            - Generar reporte PDF Base64
```

## 🏃‍♂️ Ejecución rápida

Para una prueba rápida completa:

1. Ejecutar **Runner** en Postman
2. Seleccionar la colección completa
3. Seleccionar el environment "Banco Pichincha - Local Development"
4. Ejecutar todas las carpetas en orden (1 a 5)
5. Revisar resultados en la pestaña de tests

¡La colección está lista para usar! 🚀