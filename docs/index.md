# UBESTO — Procesos de Software y Calidad

UBESTO es una aplicación de movilidad tipo Uber desarrollada con Spring Boot, JPA/H2 y cliente Swing. Esta entrega cierra las historias que quedaban en backlog, añade Docker, deja preparado VisualVM para rendimiento y publica documentación en GitHub Pages.

## Estado de entrega

| Área | Estado | Resultado |
|---|---:|---|
| Dockerización | ✅ | `Dockerfile`, `docker-compose.yml`, ejecución headless sin Swing en contenedor |
| US07 Historial de viajes | ✅ | Historial por pasajero y conductor con DTO seguro |
| US08 Cancelar viaje / eliminar cuenta | ✅ | Cancelación de viajes activos y borrado lógico de cuenta |
| US10 Rendimiento de peticiones | ✅ | Suite `RendimientoTest`, perfil Maven y guía VisualVM/JMX |
| US11 Valorar con estrellas | ✅ | Endpoint nuevo por viaje, validación 1..5 y media del conductor |
| US19 Billetera y ganancias | ✅ | Wallet de pasajero/conductor y ganancias acumuladas |
| GitHub Pages | ✅ | Workflow automático con documentación y JaCoCo |

## Arranque rápido

```bash
cd ubesto
./mvnw spring-boot:run
```

Swagger queda disponible en:

```text
http://localhost:8080/swagger-ui/index.html
```

H2 console queda disponible en:

```text
http://localhost:8080/h2-console
```

Credenciales por defecto:

```text
JDBC URL: jdbc:h2:file:./PSyC-SS-01/data/db;AUTO_SERVER=TRUE
User: db-user
Password: db-password
```

## Docker

```bash
docker compose up --build
```

La API queda en `http://localhost:8080`. En Docker la UI Swing se desactiva con `APP_UI_ENABLED=false` porque un contenedor normal no tiene servidor gráfico.

## Tests

```bash
cd ubesto
./mvnw test
./mvnw test -Pperformance
./mvnw test -Pvisualvm-performance -Dvisualvm.wait.seconds=30
./mvnw verify
```

El informe JaCoCo se genera en:

```text
ubesto/target/site/jacoco/index.html
```

## Secciones

- [Arquitectura](arquitectura.md)
- [API REST](api.md)
- [User stories cerradas](user-stories.md)
- [Docker](docker.md)
- [Tests y VisualVM](tests-visualvm.md)
- [Reportes](reports.md)
