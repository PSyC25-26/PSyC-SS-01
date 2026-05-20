# UBESTO - Sistema de Gestión de Movilidad

Proyecto de la asignatura **Procesos de Software y Calidad**. Backend Spring Boot + JPA/H2 con cliente Java Swing para gestionar pasajeros, conductores, vehículos y viajes.

Esta versión cierra las user stories que quedaban en backlog, añade Docker, mejora los tests de rendimiento con soporte VisualVM y deja preparada la documentación para GitHub Pages.

## Funcionalidades implementadas

| User story | Estado | Implementación |
|---|---:|---|
| US07 - Ver historial de viajes | ✅ | `GET /passengers/{id}/trips`, `GET /drivers/{id}/trips`, Swing con tabla de historial |
| US08 - Cancelar viaje o eliminar cuenta | ✅ | Cancelación por pasajero/conductor y borrado lógico de cuenta |
| US10 - Rendimiento de peticiones | ✅ | `RendimientoTest`, perfiles Maven `performance` y `visualvm-performance` |
| US11 - Valorar con estrella el servicio de viaje | ✅ | `POST /trips/{tripId}/rate`, validación 1..5 y media del conductor |
| US19 - Billetera y ganancias del conductor | ✅ | Wallet de pasajero/conductor, transferencia al finalizar, ganancias acumuladas |
| Dockerización | ✅ | `ubesto/Dockerfile` + `docker-compose.yml` |
| Documentación y GitHub Pages | ✅ | Carpeta `docs/` + workflow `.github/workflows/maven-site-integration.yml` |

## Arranque local

```bash
cd ubesto
./mvnw spring-boot:run
```

La aplicación arranca la API REST y, si hay entorno gráfico, también la UI Swing.

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 console: `http://localhost:8080/h2-console`

H2 local:

```text
JDBC URL: jdbc:h2:file:./PSyC-SS-01/data/db;AUTO_SERVER=TRUE
User: db-user
Password: db-password
```

## Docker

Desde la raíz:

```bash
docker compose up --build
```

La API queda en:

```text
http://localhost:8080
```

En Docker la UI Swing se desactiva con `APP_UI_ENABLED=false` porque el contenedor es headless. La base H2 se persiste en el volumen Docker `ubesto_data`.

## Tests

Desde `ubesto/`:

```bash
./mvnw test
```

Suite principal:

```bash
./mvnw test -Dtest=UbestoApplicationTests
```

Solo integración/aceptación/backlog:

```bash
./mvnw test -Pintegration
```

Solo rendimiento:

```bash
./mvnw test -Pperformance
```

Cobertura:

```bash
./mvnw verify
```

Informe:

```text
ubesto/target/site/jacoco/index.html
```

## VisualVM con tests de rendimiento

```bash
cd ubesto
./mvnw test -Pvisualvm-performance -Dvisualvm.wait.seconds=30
```

Luego en VisualVM:

1. `Remote` → `Add JMX Connection`.
2. Conexión: `localhost:9010`.
3. Sin autenticación y sin SSL.
4. Mira CPU, heap, threads y sampler mientras se ejecuta `RendimientoTest`.

## Documentación

La documentación para GitHub Pages está en `docs/`:

- `docs/index.md`
- `docs/arquitectura.md`
- `docs/api.md`
- `docs/user-stories.md`
- `docs/docker.md`
- `docs/tests-visualvm.md`
- `docs/reports.md`

El workflow construye GitHub Pages con Jekyll y añade el reporte JaCoCo si se genera.

## Endpoints clave

### Solicitar viaje

```http
POST /trips/request
```

```json
{
  "passengerId": 1,
  "origen":  {"latitud": 43.2630, "longitud": -2.9350},
  "destino": {"latitud": 43.3200, "longitud": -1.9800},
  "categoria": "UBERX"
}
```

### Aceptar viaje

```http
POST /trips/{tripId}/accept/{driverId}
```

### Cancelar viaje

```http
POST /trips/{tripId}/cancel/passenger/{passengerId}?reason=Cambio%20de%20planes
POST /trips/{tripId}/cancel/driver/{driverId}?reason=Averia
```

### Valorar viaje

```http
POST /trips/{tripId}/rate
```

```json
{"passengerId": 1, "estrellas": 5}
```

### Wallet

```http
GET /passengers/{id}/wallet
GET /drivers/{id}/wallet
```

### Eliminar cuenta

```http
DELETE /passengers/{id}
DELETE /drivers/{id}
```

El borrado es lógico: se anonimiza la cuenta y se cancelan viajes activos sin romper el historial.
