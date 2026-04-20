package deusto.sd.ubesto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.DriverRepository;
import deusto.sd.ubesto.dao.PassengerRepository;
import deusto.sd.ubesto.dao.TripRepository;
import deusto.sd.ubesto.dao.VehicleRepository;
import deusto.sd.ubesto.dto.TripRequestDTO;
import deusto.sd.ubesto.entity.Driver;
import deusto.sd.ubesto.entity.Passenger;
import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Trip;
import deusto.sd.ubesto.entity.Trip.EstadoViaje;
import deusto.sd.ubesto.entity.Vehicle;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;
import jakarta.persistence.EntityNotFoundException;

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

    /**
     * Crea una nueva solicitud de viaje
     */
    public Trip requestTrip(TripRequestDTO request) {
        Passenger passenger = passengerRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new EntityNotFoundException("Passenger not found with id: " + request.getPassengerId()));

        // Calculamos el precio incluyendo la categoría
        double price = calculatePrice(request.getOrigen(), request.getDestino(), request.getCategoria());

        Trip newTrip = new Trip();
        newTrip.setCliente(passenger);
        newTrip.setPosicionOrigen(request.getOrigen());
        newTrip.setPosicionDestino(request.getDestino());
        newTrip.setPrecio(price);
        newTrip.setEstado(EstadoViaje.SOLICITADO);

        return tripRepository.save(newTrip);
    }

    /**
     * Un conductor acepta un viaje e inicia la simulación.
     */
    public Trip acceptTrip(Long tripId, Long driverId) {

        // Comprobar si el conductor ya tiene un viaje activo (ACPETADO o EN_CURSO)
        boolean isBusy = tripRepository.findAll().stream()
            .anyMatch(t -> t.getConductor() != null && 
                        t.getConductor().getId().equals(driverId) && 
                        (t.getEstado() == EstadoViaje.ACEPTADO || t.getEstado() == EstadoViaje.EN_CURSO));

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
        
        Vehicle activeVehicle = vehicleRepository.findById(driver.getVehicleActiveId())
                .orElseThrow(() -> new EntityNotFoundException("Vehículo activo no encontrado para el conductor: " + driverId));

        trip.setConductor(driver);
        trip.setVehiculo(activeVehicle);
        trip.setEstado(EstadoViaje.ACEPTADO);
        
        Trip updatedTrip = tripRepository.save(trip);

        new Thread(new TripSimulator(updatedTrip.getId(), this)).start();

        return updatedTrip;
    }

    /**
     * Actualiza el estado del viaje a EN_CURSO (llamado desde el hilo).
     */
    public void startTrip(Long tripId) {
        tripRepository.findById(tripId).ifPresent(trip -> {
            trip.setEstado(EstadoViaje.EN_CURSO);
            tripRepository.save(trip);
        });
    }

    /**
     * Actualiza el estado del viaje a FINALIZADO (llamado desde el hilo).
     */
    public void finishTrip(Long tripId) {
    tripRepository.findById(tripId).ifPresent(trip -> {
        trip.setEstado(EstadoViaje.FINALIZADO);
        
        double precio = trip.getPrecio();
        Passenger p = trip.getCliente();
        Driver d = trip.getConductor();

        // 1. Transferencia de dinero
        p.setMonedero(p.getMonedero() - precio);
        d.setMonedero(d.getMonedero() + precio);

        // 2. Actualización de posición al DESTINO
        p.setPosicionActual(trip.getPosicionDestino());
        d.setPosicionActual(trip.getPosicionDestino());

        // 3. Persistencia
        passengerRepository.save(p);
        driverRepository.save(d);
        tripRepository.save(trip);
        
        System.out.println("LOGICA: Viaje terminado. Pasajero y Conductor movidos al destino.");
    });
}
    private double calculatePrice(Posicion origin, Posicion destination, CategoriaVehiculo category) {
        final int R = 6371; // Radio de la Tierra en km

        double lat1Rad = Math.toRadians(origin.getLatitud());
        double lat2Rad = Math.toRadians(destination.getLatitud());
        double deltaLat = Math.toRadians(destination.getLatitud() - origin.getLatitud());
        double deltaLon = Math.toRadians(destination.getLongitud() - origin.getLongitud());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distancia en km

        // Multiplicador basado en la categoría
        double categoryMultiplier;
        switch (category) {
            case BLACK:
                categoryMultiplier = 2.0;
                break;
            case XL:
                categoryMultiplier = 1.8;
                break;
            case UBERX:
            default:
                categoryMultiplier = 1.2;
                break;
        }

        // Precio = (distancia * multiplicador) + tarifa base
        double price = (distance * categoryMultiplier) + 2.0; // 2.0 es una tarifa base

        // Redondear a 2 decimales
        return Math.round(price * 100.0) / 100.0;
    }
}