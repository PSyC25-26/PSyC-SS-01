# Tests y VisualVM

## Tests principales

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

Cobertura JaCoCo:

```bash
./mvnw verify
```

Informe:

```text
target/site/jacoco/index.html
```

## Conectar tests de rendimiento con VisualVM

La forma limpia es usar el perfil `visualvm-performance`, que arranca los tests con JMX en el puerto `9010` y espera unos segundos antes de empezar para que te dé tiempo a conectar.

### Paso 1 — Lanzar tests con espera

```bash
cd ubesto
./mvnw test -Pvisualvm-performance -Dvisualvm.wait.seconds=30
```

Qué hace:

- Ejecuta solo `RendimientoTest`.
- Abre JMX en `9010`.
- Espera 30 segundos antes de lanzar las peticiones.

### Paso 2 — Abrir VisualVM

```bash
visualvm
```

### Paso 3 — Añadir conexión JMX

En VisualVM:

1. Panel izquierdo → botón derecho en `Remote`.
2. `Add JMX Connection`.
3. Connection: `localhost:9010`.
4. Authentication: desactivada.
5. SSL: desactivado.
6. Aceptar.

### Paso 4 — Qué mirar

Durante `RendimientoTest` mira:

- **CPU**: picos al registrar usuarios y solicitar viajes.
- **Heap**: crecimiento estable, sin subida continua.
- **Threads**: hilos del pool de tests y posibles `trip-simulator-*`.
- **Sampler CPU**: métodos calientes de controladores/servicios/repositorios.
- **Sampler Memory**: asignaciones excesivas de DTOs/JSON.

## Alternativa: attach local sin JMX

Si trabajas en local y VisualVM detecta procesos Java automáticamente:

```bash
./mvnw test -Pperformance
```

Abre VisualVM y selecciona el proceso Maven/Surefire. Es peor porque el proceso dura poco; por eso el perfil JMX con espera es más cómodo.

## VisualVM con aplicación arrancada

Para perfilar la app real, no los tests:

```bash
cd ubesto
JAVA_OPTS="-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=9010 \
-Dcom.sun.management.jmxremote.rmi.port=9010 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.local.only=false \
-Djava.rmi.server.hostname=127.0.0.1" \
./mvnw spring-boot:run
```

Conecta VisualVM a `localhost:9010` y lanza peticiones con Swagger o curl.
