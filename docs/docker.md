# Docker

## Archivos añadidos

```text
Dockerfile                  # en ubesto/
docker-compose.yml          # en raíz del proyecto
ubesto/.dockerignore
```

## Construcción y arranque

Desde la raíz del proyecto:

```bash
docker compose up --build
```

API:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

H2 console:

```text
http://localhost:8080/h2-console
```

Credenciales Docker:

```text
JDBC URL: jdbc:h2:file:/app/data/db;AUTO_SERVER=FALSE
User: db-user
Password: db-password
```

## Persistencia

`docker-compose.yml` usa un volumen nombrado:

```yaml
volumes:
  - ubesto_data:/app/data
```

La base queda persistida en el volumen Docker `ubesto_data`. Para borrarla:

```bash
docker compose down -v
```

## Por qué se desactiva Swing en Docker

La aplicación original mezcla API REST y Swing. En un contenedor normal no hay display gráfico. Por eso se arranca con:

```yaml
environment:
  APP_UI_ENABLED: "false"
```

El backend queda operativo y la UI puede usarse arrancando localmente con `./mvnw spring-boot:run`.

## JMX en Docker

El compose ya expone `9010` y arranca la JVM con JMX para VisualVM:

```yaml
JAVA_OPTS: >-
  -Djava.awt.headless=true
  -Dcom.sun.management.jmxremote
  -Dcom.sun.management.jmxremote.port=9010
  -Dcom.sun.management.jmxremote.rmi.port=9010
  -Dcom.sun.management.jmxremote.authenticate=false
  -Dcom.sun.management.jmxremote.ssl=false
  -Dcom.sun.management.jmxremote.local.only=false
  -Djava.rmi.server.hostname=127.0.0.1
```

Luego conecta VisualVM a `localhost:9010`.
