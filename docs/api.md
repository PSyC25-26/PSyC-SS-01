# API REST

Base URL local:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Pasajeros

### Registrar pasajero

```http
POST /passengers/registerPassenger
Content-Type: application/json
```

```json
{
  "nombre": "María García",
  "email": "maria@ubesto.com",
  "password": "securePass1",
  "metodoPago": "tarjeta",
  "posicionActual": {"latitud": 43.2630, "longitud": -2.9350}
}
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "nombre": "María García",
  "email": "maria@ubesto.com",
  "password": "securePass1",
  "metodoPago": "tarjeta",
  "latitud": 43.263,
  "longitud": -2.935,
  "monedero": 100.0,
  "posicionActual": {"latitud": 43.263, "longitud": -2.935}
}
```

### Login pasajero

```http
POST /passengers/loginPassenger
Content-Type: application/json
```

```json
{"email":"maria@ubesto.com", "password":"securePass1"}
```

Respuesta `200 OK`: body numérico con el ID.

### Actualizar pasajero

```http
PUT /passengers/update/{id}
Content-Type: application/json
```

```json
{
  "nombre": "María Actualizada",
  "password": "newPass456",
  "metodoPago": "bizum"
}
```

### Eliminar cuenta de pasajero

```http
DELETE /passengers/{id}
```

Efecto: marca la cuenta como eliminada, anonimiza email, invalida login y cancela viajes no finalizados.

### Historial de pasajero

```http
GET /passengers/{id}/trips
```

Devuelve `TripHistoryDTO[]`.

### Wallet de pasajero

```http
GET /passengers/{id}/wallet
```

```json
{
  "userId": 1,
  "rol": "PASAJERO",
  "saldo": 87.5,
  "gananciasTotales": 0.0,
  "viajesFinalizados": 1,
  "viajesCancelados": 0,
  "valoracionesRecibidas": 0,
  "calificacionMedia": 0.0
}
```

## Conductores

### Registrar conductor

```http
POST /drivers/registerDriver
Content-Type: application/json
```

```json
{
  "nombre": "Pedro Rodríguez",
  "email": "pedro@ubesto.com",
  "password": "driverPass99",
  "licenciaConducir": "B-123456",
  "calificacionMedia": 5.0,
  "posicionActual": {"latitud": 43.2630, "longitud": -2.9350}
}
```

### Login conductor

```http
POST /drivers/loginDriver
Content-Type: application/json
```

```json
{"email":"pedro@ubesto.com", "password":"driverPass99"}
```

### Añadir vehículo

```http
POST /vehicles/create/{driverId}
Content-Type: application/json
```

```json
{
  "matricula": "1234-BCN",
  "marca": "Toyota",
  "modelo": "Corolla",
  "color": "Blanco",
  "categoria": "UBERX"
}
```

El vehículo creado pasa a ser el vehículo activo del conductor.

### Cambiar vehículo activo

```http
PUT /vehicles/driver/{driverId}/active?matricula=1234-BCN
```

### Historial de conductor

```http
GET /drivers/{id}/trips
```

### Wallet / ganancias del conductor

```http
GET /drivers/{id}/wallet
```

```json
{
  "userId": 2,
  "rol": "CONDUCTOR",
  "saldo": 12.5,
  "gananciasTotales": 12.5,
  "viajesFinalizados": 1,
  "viajesCancelados": 0,
  "valoracionesRecibidas": 1,
  "calificacionMedia": 5.0
}
```

## Viajes

### Solicitar viaje

```http
POST /trips/request
Content-Type: application/json
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

Reglas:

- El viaje debe estar en `SOLICITADO`.
- El conductor debe existir.
- El conductor debe tener `vehicleActiveId`.
- El conductor no puede tener otro viaje `ACEPTADO` o `EN_CURSO`.

### Cancelar viaje por pasajero

```http
POST /trips/{tripId}/cancel/passenger/{passengerId}?reason=Cambio%20de%20planes
```

### Cancelar viaje por conductor

```http
POST /trips/{tripId}/cancel/driver/{driverId}?reason=Averia
```

### Valorar viaje

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
- El pasajero debe ser el dueño del viaje.
- Actualiza la media del conductor con todos sus viajes valorados.
