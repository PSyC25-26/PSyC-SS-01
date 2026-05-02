# 📝 UBESTO - Sistema de Gestión de Movilidad

UBESTO es una plataforma backend desarrollada con **Spring Boot** que gestiona un ecosistema de transporte bajo demanda. El sistema implementa una arquitectura por capas para administrar conductores, pasajeros y vehículos, incluyendo un motor de simulación de trayectos en tiempo real y un cliente pesado nativo.

## 🚀 Nuevas Funcionalidades (Sprint Actual)

Se ha completado la integración total entre el **Frontend (Java Swing)** y el **Backend (API REST)**, cerrando el flujo completo de los usuarios. Las principales novedades incluyen:

* **Cliente HTTP Integrado:** La capa Swing ahora se comunica exclusivamente mediante peticiones HTTP (usando `java.net.http.HttpClient`) enviando y recibiendo datos en formato **JSON**, respetando la arquitectura Cliente-Servidor.
* **Gestión de Sesión por IDs:** Se ha modificado el sistema de autenticación. Ahora, tras un login exitoso, el backend devuelve el `ID` numérico del usuario. Este ID es capturado por Swing y propagado dinámicamente a todas las ventanas de acción para mantener el contexto del usuario.
* **Dashboards Basados en Roles:** Menús dinámicos (`DashboardFrame`) que adaptan sus opciones dependiendo de si el usuario logueado es `PASAJERO` o `CONDUCTOR`.
* **Flujo de Conductor:** Implementación de pantallas para añadir vehículos (`VentanaAñadirVehiculo`), aceptar viajes por ID (`VentanaRealizarViaje`) y editar el perfil (`VentanaEditarConductor`).
* **Flujo de Pasajero:** Pantalla interactiva para la solicitud de viajes especificando origen, destino y categoría del vehículo (`VentanaSolicitarViaje`) y editar el perfil (`VentanaEditarPasajero`).

---

## 🏗️ Arquitectura del Sistema

El proyecto sigue un patrón de **Arquitectura en Capas**, garantizando el desacoplamiento y la escalabilidad:

* **Capa de Presentación (Swing & REST):**
    * Interfaz gráfica en Java Swing para la interacción del usuario final.
    * Controladores REST (`TripController`, `VehicleController`, `DriverController`, `PassengerController`) que exponen la API para comunicaciones externas.
* **Capa de Servicio (Business Logic):**
    * `TripService`: Gestiona la lógica transaccional, estados de viaje y cálculos financieros.
    * `TripSimulator`: Implementa `Runnable` para ejecutar el avance de los viajes en hilos (`Threads`) independientes.
* **Capa de Datos (DAO/JPA):**
    * Repositorios que gestionan la persistencia en una base de datos **H2** persistente.
* **Modelo (Entities & DTOs):**
    * Uso de **DTOs** (`TripRequestDTO`, `LoginDTO`, `VehicleDTO`) para el intercambio de datos seguro.
    * Entidades JPA que mapean la realidad del negocio (Conductores, Pasajeros, Vehículos).

---

## ⚙️ Especificaciones Técnicas Clave

### 1. Motor de Geocalización y Tarifas
El sistema calcula el precio de los viajes utilizando la **Fórmula de Haversine**, que determina la distancia entre dos puntos sobre una esfera (la Tierra) a partir de sus coordenadas (latitud/longitud).
* **Tarificación Dinámica:** Se aplica un multiplicador según la categoría del vehículo (`UBERX`, `XL`, `BLACK`) sobre una tarifa base.

### 2. Gestión de Persistencia
A diferencia de los entornos de prueba volátiles, este proyecto utiliza un sistema de almacenamiento físico:
* **URL de conexión:** `jdbc:h2:file:./data/ubestodb`
* **Estrategia:** `spring.jpa.hibernate.ddl-auto=update`, lo que permite que los datos (saldos, perfiles, historial) sobrevivan a los reinicios del servidor.

### 3. El Vínculo Conductor-Vehículo
El sistema implementa una relación donde el `Driver` mantiene una referencia activa (`vehicleActiveId`) al vehículo que está operando. Esto permite:
* Validar la disponibilidad del conductor.
* Asignar automáticamente el tipo de tarifa al viaje según el coche activo.

---

## 🧪 Estrategia de Tests

El proyecto cuenta con **61 tests** organizados en cuatro niveles, ejecutables todos con un único comando:

```bash
mvn test
```

Para ejecutar únicamente la suite de unitarios e integración:

```bash
mvn test -Dtest=UbestoApplicationTests
```

Para generar el informe de cobertura JaCoCo:

```bash
mvn verify
```
El informe se genera en `target/site/jacoco/index.html`.

---

### 🔬 Tests Unitarios — `UnitariosTest` (11 tests)

**Ubicación:** `src/test/java/deusto/sd/ubesto/unitarios/`

No levantan Spring ni base de datos. Usan **Mockito** para simular los repositorios y verificar la lógica de negocio de forma aislada y muy rápida.

| ID | Descripción |
|----|-------------|
| U-01 | Login pasajero: contraseña correcta devuelve `true` |
| U-02 | Login pasajero: contraseña incorrecta devuelve `false` |
| U-03 | Login conductor: contraseña correcta devuelve `true` |
| U-04 | Login conductor: contraseña incorrecta devuelve `false` |
| U-05 | `requestTrip`: crea viaje correctamente con pasajero existente |
| U-06 | `requestTrip`: lanza `EntityNotFoundException` si el pasajero no existe |
| U-07 | `acceptTrip`: acepta viaje correctamente y cambia estado a `ACEPTADO` |
| U-08 | `acceptTrip`: lanza excepción si el viaje no está en estado `SOLICITADO` |
| U-09 | `acceptTrip`: lanza excepción si el conductor ya tiene un viaje activo |
| U-10 | `acceptTrip`: lanza excepción si el conductor no tiene vehículo asignado |
| U-11 | `finishTrip`: transfiere dinero y actualiza posición al finalizar |

---

### 🔗 Tests de Integración — `IntegrationTest` (8 tests)

**Ubicación:** `src/test/java/deusto/sd/ubesto/integration/`

Levantan Spring Boot completo con **H2 en memoria** y usan **MockMvc**. Cada test es `@Transactional`, por lo que la base de datos queda limpia entre tests.

| ID | Endpoint | Descripción |
|----|----------|-------------|
| I-01 | `POST /passengers/registerPassenger` | Devuelve 201 y el pasajero queda guardado en BD |
| I-02 | `POST /passengers/registerPassenger` | Devuelve 406 si el email ya existe |
| I-03 | `POST /passengers/loginPassenger` | Devuelve 200 con credenciales correctas |
| I-04 | `POST /passengers/loginPassenger` | Devuelve 401 con contraseña incorrecta |
| I-05 | `POST /drivers/registerDriver` | Devuelve 201 y el conductor queda guardado en BD |
| I-06 | `POST /drivers/loginDriver` | Devuelve 200 con credenciales correctas |
| I-07 | `POST /trips/request` | Devuelve 201 cuando el pasajero existe |
| I-08 | `POST /trips/request` | Devuelve 404 si el pasajero no existe |

---

### ✅ Tests de Aceptación — `AceptacionTest` (15 tests)

**Ubicación:** `src/test/java/deusto/sd/ubesto/aceptacion/`

Validan flujos de negocio completos de extremo a extremo, tal como los experimentaría un usuario real. Usan **MockMvc** con Spring Boot completo y emails únicos por ejecución para evitar colisiones entre suites.

| ID | Descripción |
|----|-------------|
| A-01 | Un nuevo pasajero puede registrarse correctamente |
| A-02 | Registrar un pasajero con email ya existente devuelve 406 |
| A-03 | Un pasajero registrado puede hacer login y recibe su ID |
| A-04 | Un nuevo conductor puede registrarse correctamente |
| A-04b | Un conductor registrado puede hacer login |
| A-05 | Un conductor puede añadir un vehículo a su perfil |
| A-06 | Un pasajero registrado puede solicitar un viaje (UBERX) |
| A-07 | Un conductor con vehículo puede aceptar un viaje SOLICITADO |
| A-08 | Aceptar un viaje inexistente devuelve 404 |
| A-09 | Aceptar un viaje ya ACEPTADO devuelve 409 Conflict |
| A-10 | Un pasajero puede actualizar su nombre y método de pago |
| A-11 | Un conductor puede actualizar su nombre y licencia |
| A-12 | Actualizar un conductor inexistente devuelve 404 |
| A-13 | Viaje con categoría BLACK tiene precio mayor que con UBERX |
| A-14 | Solicitar viaje con pasajero inexistente devuelve 404 |

---

### ⚡ Tests de Rendimiento — `RendimientoTest` (8 tests)

**Ubicación:** `src/test/java/deusto/sd/ubesto/rendimiento/`

Validan que los endpoints cumplen umbrales de tiempo aceptables y que el sistema responde correctamente bajo carga concurrente. Para un análisis detallado de CPU, heap y threads, conectar **VisualVM** al proceso JVM durante la ejecución.

| ID | Umbral | Descripción |
|----|--------|-------------|
| R-01 | < 500 ms | `POST /passengers/registerPassenger` responde en tiempo aceptable |
| R-02 | < 300 ms | `POST /passengers/loginPassenger` responde en tiempo aceptable |
| R-03 | < 500 ms | `POST /drivers/registerDriver` responde en tiempo aceptable |
| R-04 | < 400 ms | `POST /trips/request` responde en tiempo aceptable |
| R-05 | < 5 000 ms total | 50 registros de pasajeros en serie |
| R-06 | < 300 ms media | Media de 20 logins consecutivos |
| R-07 | 0 errores 5xx | 10 registros simultáneos sin errores de servidor |
| R-08 | 0 errores 5xx | 5 solicitudes de viaje concurrentes sin errores de servidor |

---

## 🛠️ Ejecución y Configuración

### Desde VS Code
1. Asegúrate de tener instalada la extensión **Extension Pack for Java**.
2. Localiza la clase principal: `deusto.sd.ubesto.UbestoApplication`.
3. Ejecuta mediante el enlace **Run** sobre el método `main`.

### Acceso a Swagger UI (Documentación API)
Para auditar las rutas REST y probar los endpoints manualmente:
1. Navega a `http://localhost:8080/swagger-ui/index.html`

### Acceso a la Consola de Datos (H2)
Para auditar los saldos y estados de los viajes en tiempo real:
1. Navega a `http://localhost:8080/h2-console`
2. **JDBC URL:** `jdbc:h2:file:./PSyC-SS-01/data/db`
3. **Usuario:** `db-user` | **Password:** `db-password`