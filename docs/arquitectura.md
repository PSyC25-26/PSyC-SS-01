# Arquitectura

## Vista general

```mermaid
flowchart LR
    Swing[Cliente Swing] --> REST[Controladores REST]
    Swagger[Swagger UI] --> REST
    REST --> Services[Servicios de negocio]
    Services --> Repos[Repositorios Spring Data JPA]
    Repos --> H2[(H2 file / mem)]
    Services --> Simulator[TripSimulator daemon thread]
```

## Capas

### Presentación

- `swing/`: cliente pesado Java Swing.
- `controller/`: API REST usada por Swing, Swagger y tests MockMvc.

### Servicios

- `TripService`: solicitud, aceptación, simulación, cancelación y finalización de viajes.
- `PassengerService`: registro, login, historial, valoración, wallet y borrado lógico.
- `DriverService`: registro, login, historial, wallet, ganancias y borrado lógico.
- `VehicleService`: alta de vehículos y cambio de vehículo activo.

### Persistencia

- `PassengerRepository`
- `DriverRepository`
- `TripRepository`
- `VehicleRepository`
- `LoggedUserRepository`

## Modelo de dominio

```mermaid
classDiagram
    class User {
      Long id
      String nombre
      String email
      String password
      double monedero
      Posicion posicionActual
      boolean cuentaEliminada
      LocalDateTime fechaEliminacion
    }

    class Passenger {
      String metodoPago
    }

    class Driver {
      String licenciaConducir
      double calificacionMedia
      Long vehicleActiveId
    }

    class Vehicle {
      Long id
      String matricula
      String marca
      String modelo
      String color
      CategoriaVehiculo categoria
    }

    class Trip {
      Long id
      double precio
      EstadoViaje estado
      Integer rating
      String cancelReason
      String cancelledBy
      LocalDateTime cancelledAt
    }

    User <|-- Passenger
    User <|-- Driver
    Driver "1" --> "0..*" Vehicle
    Passenger "1" --> "0..*" Trip
    Driver "0..1" --> "0..*" Trip
    Vehicle "0..1" --> "0..*" Trip
```

## Estados de viaje

```mermaid
stateDiagram-v2
    [*] --> SOLICITADO
    SOLICITADO --> ACEPTADO: conductor acepta
    ACEPTADO --> EN_CURSO: simulador tras 5s
    EN_CURSO --> FINALIZADO: simulador tras 10s
    SOLICITADO --> CANCELADO: pasajero cancela
    ACEPTADO --> CANCELADO: pasajero/conductor cancela
    EN_CURSO --> CANCELADO: pasajero/conductor cancela
    FINALIZADO --> [*]
    CANCELADO --> [*]
```

## Decisiones importantes

- El borrado de cuenta es lógico, no físico. Motivo: los viajes históricos mantienen trazabilidad y no se rompen claves foráneas.
- En Docker se arranca sin Swing. Motivo: los contenedores estándar son headless.
- El hilo de simulación se marca como daemon. Motivo: no bloquea la JVM ni los tests si todavía está durmiendo.
- El enum de estado/categoría se persiste como `STRING`. Motivo: evita que cambiar el orden del enum corrompa datos.
