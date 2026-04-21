Python
markdown_content = """# PSyC-SS-01

# 📝 UBESTO - Sistema de Gestión de Movilidad

UBESTO es una plataforma backend desarrollada con **Spring Boot** que gestiona un ecosistema de transporte bajo demanda. El sistema implementa una arquitectura por capas para administrar conductores, pasajeros y vehículos, incluyendo un motor de simulación de trayectos en tiempo real y un cliente pesado nativo.

## 🚀 Nuevas Funcionalidades (Sprint Actual)

Se ha completado la integración total entre el **Frontend (Java Swing)** y el **Backend (API REST)**, cerrando el flujo completo de los usuarios. Las principales novedades incluyen:

* **Cliente HTTP Integrado:** La capa Swing ahora se comunica exclusivamente mediante peticiones HTTP (usando `java.net.http.HttpClient`) enviando y recibiendo datos en formato **JSON**, respetando la arquitectura Cliente-Servidor.
* **Gestión de Sesión por IDs:** Se ha modificado el sistema de autenticación. Ahora, tras un login exitoso, el backend devuelve el `ID` numérico del usuario. Este ID es capturado por Swing y propagado dinámicamente a todas las ventanas de acción para mantener el contexto del usuario.
* **Dashboards Basados en Roles:** Menús dinámicos (`DashboardFrame`) que adaptan sus opciones dependiendo de si el usuario logueado es `PASAJERO` o `CONDUCTOR`.
* **Flujo de Conductor:** Implementación de pantallas para añadir vehículos (`VentanaAñadirVehiculo`), aceptar viajes por ID (`VentanaRealizarViaje`) y editar el perfil (`VentanaEditarConductor`).
* **Flujo de Pasajero:** Pantalla interactiva para la solicitud de viajes especificando origen, destino y categoría del vehículo (`VentanaSolicitarViaje`).

---

## 🏗️ Arquitectura del Sistema

El proyecto sigue un patrón de **Arquitectura en Capas**, garantizando el desacoplamiento y la escalabilidad:

* **Capa de Presentación (Swing & REST):** * Interfaz gráfica en Java Swing para la interacción del usuario final.
    * Controladores REST (`TripController`, `VehicleController`, `DriverController`, `PassengerController`) que exponen la API para comunicaciones externas.
* **Capa de Servicio (Business Logic):** * `TripService`: Gestiona la lógica transaccional, estados de viaje y cálculos financieros.
    * `TripSimulator`: Implementa `Runnable` para ejecutar el avance de los viajes en hilos (`Threads`) independientes.
* **Capa de Datos (DAO/JPA):** * Repositorios que gestionan la persistencia en una base de datos **H2** persistente.
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

## 🛠️ Ejecución y Configuración

### Desde VS Code
1.  Asegúrate de tener instalada la extensión **Extension Pack for Java**.
2.  Localiza la clase principal: `deusto.sd.ubesto.UbestoApplication`.
3.  Ejecuta mediante el enlace **Run** sobre el método `main`.

### Acceso a Swagger UI (Documentación API)
Para auditar las rutas REST y probar los endpoints manualmente:
1.  Navega a `http://localhost:8080/swagger-ui/index.html`

### Acceso a la Consola de Datos (H2)
Para auditar los saldos y estados de los viajes en tiempo real:
1.  Navega a `http://localhost:8080/h2-console`
2.  **JDBC URL:** `jdbc:h2:file:./PSyC-SS-01/data/db`
3.  **Usuario:** `db-user` | **Password:** `db-password`

---

## 📂 Estructura del Proyecto