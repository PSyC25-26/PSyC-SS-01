package deusto.sd.ubesto.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.DriverRepository;
import deusto.sd.ubesto.dao.PassengerRepository;
import deusto.sd.ubesto.dao.TripRepository;
import deusto.sd.ubesto.dao.VehicleRepository;
import deusto.sd.ubesto.dto.TripHistoryDTO;
import deusto.sd.ubesto.dto.TripRequestDTO;
import deusto.sd.ubesto.entity.Driver;
import deusto.sd.ubesto.entity.Passenger;
import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Trip;
import deusto.sd.ubesto.entity.Trip.EstadoViaje;
import deusto.sd.ubesto.entity.Vehicle;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public TripService(TripRepository tripRepository, PassengerRepository passengerRepository,
                       DriverRepository driverRepository, VehicleRepository vehicleRepository) {
        this.tripRepository = tripRepository;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Trip requestTrip(TripRequestDTO request) {
        validateTripRequest(request);

        Passenger passenger = passengerRepository.findById(request.getPassengerId())
            .orElseThrow(() -> new EntityNotFoundException("Passenger not found with id: " + request.getPassengerId()));

        if (passenger.isCuentaEliminada()) {
            throw new EntityNotFoundException("Passenger not found with id: " + request.getPassengerId());
        }

        double price = calculatePrice(request.getOrigen(), request.getDestino(), request.getCategoria());

        Trip newTrip = new Trip();
        newTrip.setCliente(passenger);
        newTrip.setPosicionOrigen(request.getOrigen());
        newTrip.setPosicionDestino(request.getDestino());
        newTrip.setPrecio(price);
        newTrip.setEstado(EstadoViaje.SOLICITADO);

        return tripRepository.save(newTrip);
    }

    public Trip acceptTrip(Long tripId, Long driverId) {
        boolean isBusy = tripRepository.findAll().stream()
            .anyMatch(t -> t.getConductor() != null
                && t.getConductor().getId().equals(driverId)
                && (t.getEstado() == EstadoViaje.ACEPTADO || t.getEstado() == EstadoViaje.EN_CURSO));

        if (isBusy) {
            throw new IllegalStateException("El conductor ya tiene un viaje en curso.");
        }

        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found with id: " + tripId));

        if (trip.getEstado() != EstadoViaje.SOLICITADO) {
            throw new IllegalStateException("El viaje no puede ser aceptado, su estado es: " + trip.getEstado());
        }

        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + driverId));

        if (driver.isCuentaEliminada()) {
            throw new EntityNotFoundException("Driver not found with id: " + driverId);
        }
        if (driver.getVehicleActiveId() == null) {
            throw new EntityNotFoundException("El conductor " + driverId + " no tiene ningún vehículo activo asignado.");
        }

        Vehicle activeVehicle = vehicleRepository.findById(driver.getVehicleActiveId())
            .orElseThrow(() -> new EntityNotFoundException("Vehículo activo no encontrado para el conductor: " + driverId));

        trip.setConductor(driver);
        trip.setVehiculo(activeVehicle);
        trip.setEstado(EstadoViaje.ACEPTADO);

        Trip updatedTrip = tripRepository.save(trip);

        Thread simulatorThread = new Thread(new TripSimulator(updatedTrip.getId(), this), "trip-simulator-" + updatedTrip.getId());
        simulatorThread.setDaemon(true);
        simulatorThread.start();

        return updatedTrip;
    }

    @Transactional
    public void startTrip(Long tripId) {
        tripRepository.findById(tripId).ifPresent(trip -> {
            if (trip.getEstado() == EstadoViaje.ACEPTADO) {
                trip.setEstado(EstadoViaje.EN_CURSO);
                tripRepository.save(trip);
            }
        });
    }

    @Transactional
    public void finishTrip(Long tripId) {
        tripRepository.findById(tripId).ifPresent(trip -> {
            if (trip.getEstado() == EstadoViaje.CANCELADO || trip.getEstado() == EstadoViaje.FINALIZADO) {
                return;
            }

            trip.setEstado(EstadoViaje.FINALIZADO);

            Passenger passenger = trip.getCliente();
            Driver driver = trip.getConductor();
            double precio = trip.getPrecio();

            if (passenger != null) {
                passenger.setMonedero(round2(passenger.getMonedero() - precio));
                passenger.setPosicionActual(trip.getPosicionDestino());
                passengerRepository.save(passenger);
            }
            if (driver != null) {
                driver.setMonedero(round2(driver.getMonedero() + precio));
                driver.setPosicionActual(trip.getPosicionDestino());
                driverRepository.save(driver);
            }

            tripRepository.save(trip);
            System.out.println("LOGICA: Viaje terminado. Pasajero y Conductor movidos al destino.");
        });
    }

    @Transactional
    public Trip cancelTripByPassenger(Long tripId, Long passengerId, String reason) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found with id: " + tripId));

        if (trip.getCliente() == null || !trip.getCliente().getId().equals(passengerId)) {
            throw new IllegalStateException("El pasajero no puede cancelar un viaje que no es suyo.");
        }
        return cancelTrip(trip, "PASSENGER:" + passengerId, reason);
    }

    @Transactional
    public Trip cancelTripByDriver(Long tripId, Long driverId, String reason) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found with id: " + tripId));

        if (trip.getConductor() == null || !trip.getConductor().getId().equals(driverId)) {
            throw new IllegalStateException("El conductor no puede cancelar un viaje que no tiene asignado.");
        }
        return cancelTrip(trip, "DRIVER:" + driverId, reason);
    }

    public Trip getTripById(Long tripId) {
        return tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found with id: " + tripId));
    }

    public List<Trip> getAllTrips() {
        List<Trip> todosTrips = tripRepository.findAll();
        ArrayList<Trip> tripsSolicitados = new ArrayList<>();

        for (Trip trip : todosTrips) {
            if (trip.getEstado() == EstadoViaje.SOLICITADO) {
                tripsSolicitados.add(trip);
            }
        }
        return tripsSolicitados;
    }

    public List<TripHistoryDTO> getPassengerHistory(Long passengerId) {
        return tripRepository.findByClienteIdOrderByIdDesc(passengerId).stream()
            .map(TripHistoryDTO::new)
            .toList();
    }

    public List<TripHistoryDTO> getDriverHistory(Long driverId) {
        return tripRepository.findByConductorIdOrderByIdDesc(driverId).stream()
            .map(TripHistoryDTO::new)
            .toList();
    }

    public List<String> fromTripToString(List<Trip> allTrips) {
        List<String> listStrings = new ArrayList<>();
        for (Trip trip : allTrips) {
            String origen = formatPosition(trip.getPosicionOrigen());
            String destino = formatPosition(trip.getPosicionDestino());
            String s = trip.getId() + "__" + origen + "__" + destino + "__" + trip.getPrecio();
            listStrings.add(s);
        }
        return listStrings;
    }

    private Trip cancelTrip(Trip trip, String cancelledBy, String reason) {
        if (trip.getEstado() == EstadoViaje.FINALIZADO) {
            throw new IllegalStateException("No se puede cancelar un viaje finalizado.");
        }
        if (trip.getEstado() == EstadoViaje.CANCELADO) {
            throw new IllegalStateException("El viaje ya estaba cancelado.");
        }

        trip.setEstado(EstadoViaje.CANCELADO);
        trip.setCancelledBy(cancelledBy);
        trip.setCancelReason(reason == null || reason.isBlank() ? "Cancelado por usuario" : reason.trim());
        trip.setCancelledAt(LocalDateTime.now());
        return tripRepository.save(trip);
    }

    private void validateTripRequest(TripRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Datos de viaje no recibidos.");
        }
        if (request.getPassengerId() == null) {
            throw new IllegalArgumentException("El passengerId es obligatorio.");
        }
        if (request.getOrigen() == null || request.getDestino() == null) {
            throw new IllegalArgumentException("Origen y destino son obligatorios.");
        }
    }

    private double calculatePrice(Posicion origin, Posicion destination, CategoriaVehiculo category) {
        final int earthRadiusKm = 6371;
        CategoriaVehiculo selectedCategory = category != null ? category : CategoriaVehiculo.UBERX;

        double lat1Rad = Math.toRadians(origin.getLatitud());
        double lat2Rad = Math.toRadians(destination.getLatitud());
        double deltaLat = Math.toRadians(destination.getLatitud() - origin.getLatitud());
        double deltaLon = Math.toRadians(destination.getLongitud() - origin.getLongitud());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
            + Math.cos(lat1Rad) * Math.cos(lat2Rad)
            * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadiusKm * c;

        double categoryMultiplier = switch (selectedCategory) {
            case BLACK -> 2.0;
            case XL -> 1.8;
            case UBERX -> 1.2;
        };

        return round2((distance * categoryMultiplier) + 2.0);
    }

    private String formatPosition(Posicion posicion) {
        if (posicion == null) {
            return "(sin posicion)";
        }
        return "(" + posicion.getLongitud() + ", " + posicion.getLatitud() + ")";
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
