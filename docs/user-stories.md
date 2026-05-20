# User stories cerradas

## US07 — Ver historial de viajes

### Funcionalidad

El pasajero puede consultar todos sus viajes, con estado, origen, destino, precio, conductor y valoración. También se añade historial para conductor.

### Backend

- `GET /passengers/{id}/trips`
- `GET /drivers/{id}/trips`
- `GET /trips/passenger/{passengerId}`
- `GET /trips/driver/{driverId}`

### Frontend Swing

- `VentanaHistorialViajes`
- Tabla con ID, origen, destino, precio, estado, estrellas y conductor.

### Persistencia

Se consulta por `TripRepository.findByClienteIdOrderByIdDesc` y `findByConductorIdOrderByIdDesc`.

---

## US08 — Cancelar viaje o eliminar cuenta

### Cancelar viaje

Endpoints:

```http
POST /trips/{tripId}/cancel/passenger/{passengerId}?reason=...
POST /trips/{tripId}/cancel/driver/{driverId}?reason=...
```

Reglas:

- No se puede cancelar un viaje `FINALIZADO`.
- No se puede cancelar un viaje ya `CANCELADO`.
- Un pasajero solo cancela sus viajes.
- Un conductor solo cancela viajes asignados a él.
- El simulador no finaliza ni cobra un viaje cancelado.

### Eliminar cuenta

Endpoints:

```http
DELETE /passengers/{id}
DELETE /drivers/{id}
```

Decisión: borrado lógico.

Motivo: borrar físicamente rompería el historial de viajes o las claves foráneas. Por eso se marca `cuentaEliminada=true`, se anonimiza email/password y se cancelan viajes activos.

---

## US10 — Rendimiento de peticiones

### Suite

`src/test/java/deusto/sd/ubesto/rendimiento/RendimientoTest.java`

Casos:

- Registro de pasajero individual.
- Login de pasajero.
- Registro de conductor.
- Solicitud de viaje.
- 50 registros en serie.
- 20 logins consecutivos.
- 10 registros concurrentes.
- 5 solicitudes de viaje concurrentes.

### Ejecución

```bash
cd ubesto
./mvnw test -Pperformance
```

### Perfil VisualVM

```bash
./mvnw test -Pvisualvm-performance -Dvisualvm.wait.seconds=30
```

---

## US11 — Valorar con estrella el servicio de viaje

Endpoint nuevo:

```http
POST /trips/{tripId}/rate
Content-Type: application/json
```

```json
{"passengerId": 1, "estrellas": 5}
```

Reglas:

- Solo viajes `FINALIZADO`.
- Estrellas entre 1 y 5.
- El pasajero debe coincidir con el dueño del viaje.
- La media del conductor se recalcula con todos sus viajes valorados.

Endpoint legado mantenido:

```http
POST /passengers/rateTrip/{tripId}?estrellas=5
```

---

## US19 — Billetera y ganancias del conductor

### Funcionalidad

Cuando un viaje finaliza:

- Se descuenta `precio` del monedero del pasajero.
- Se suma `precio` al monedero del conductor.
- Se actualiza posición del pasajero y conductor al destino.
- Las ganancias del conductor se calculan como suma de viajes `FINALIZADO`.

### Endpoints

```http
GET /drivers/{id}/wallet
GET /passengers/{id}/wallet
```

### Swing

- Dashboard muestra saldo.
- Conductor ve también ganancias acumuladas.
- `VentanaBilletera` muestra métricas completas.
