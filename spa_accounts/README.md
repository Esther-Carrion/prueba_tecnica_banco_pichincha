# Sistema Bancario - Frontend (SPA)

## 📋 Descripción General

SPA desarrollada en Angular 20+ para la gestión integral de clientes, cuentas y movimientos bancarios, con generación de reportes y exportación a PDF. Consume la API REST del backend y está optimizada para escalabilidad y mantenibilidad.

## 🏗️ Arquitectura del Sistema

- **Framework:** Angular 20+
- **Estilos:** SCSS modular y responsivo
- **UI:** Material Icons
- **Testing:** Jest + Angular Testing Utilities
- **Integración:** API REST en `http://localhost:8081/api/`

### Estructura del Proyecto
```
spa_sistema_bancario/
├── src/
│   ├── app/
│   │   ├── components/         # Componentes reutilizables (formularios, modales, sidebar, etc.)
│   │   ├── models/             # Interfaces y DTOs TypeScript
│   │   ├── pages/              # Vistas principales (Clientes, Cuentas, Movimientos, Reportes)
│   │   ├── services/           # Servicios centralizados para API REST
│   │   ├── app.*               #  configuración principal
│   ├── assets/                 # Recursos estáticos
│   └── styles.scss             # Estilos globales
├── angular.json                # Configuración Angular
├── jest.config.js              # Configuración Jest
├── package.json                # Dependencias y scripts
├── README.md                   # Documentación técnica
```

## 🚀 Funcionalidades Principales

- 👥 Gestión de Clientes: Alta, edición, listado y validaciones.
- 🏦 Gestión de Cuentas: Creación, edición, asociación y estados.
- 💰 Movimientos: Depósitos, retiros, historial y filtrado.
- 📊 Reportes: Por cliente y período, exportación PDF, filtros avanzados.

## 🛠️ Instalación y Ejecución

```bash
# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
ng serve

# Acceder a la app
http://localhost:4200
```

## 🧪 Testing

```bash
# Ejecutar tests unitarios
npx jest --coverage


```

## 🎨 Características de Diseño

- Paleta de colores corporativa
- Responsive design y optimización móvil
- Material Icons y estados visuales claros
- Sidebar colapsible y tablas con scroll

## 🧹 Estado de Optimización

- Limpieza y modularización de estilos SCSS
- Componentes desacoplados y reutilizables
- Documentación técnica completa

## 🤝 Recomendaciones y Buenas Prácticas

- Usa `ng generate` para nuevos componentes y servicios
- Mantén la lógica en servicios y la presentación en componentes
- Valida datos y usa tipado estricto
- Revisa los modelos antes de consumir/crear datos
- Sigue el patrón de servicios y DTOs para integraciones

## 📦 Integración con Backend

- API REST esperada en `http://localhost:8081/api/`
- Compatibilidad asegurada revisando DTOs y endpoints

## 📄 Recursos Útiles

- [Angular CLI Reference](https://angular.dev/tools/cli)
- [Angular Signals](https://angular.dev/reference/signals)
- [Jest Testing](https://jestjs.io/)

---

