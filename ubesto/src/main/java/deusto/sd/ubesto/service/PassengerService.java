package deusto.sd.ubesto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.DriverRepository;
import deusto.sd.ubesto.dao.LoggedUserRepository;
import deusto.sd.ubesto.dao.PassengerRepository;
import deusto.sd.ubesto.dao.TripRepository;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.PassengerDTO;
import deusto.sd.ubesto.dto.TripHistoryDTO;
import deusto.sd.ubesto.dto.WalletDTO;
import deusto.sd.ubesto.entity.Driver;
import deusto.sd.ubesto.entity.LoggedUser;
import deusto.sd.ubesto.entity.Passenger;
import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Trip;
import deusto.sd.ubesto.entity.Trip.EstadoViaje;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final LoggedUserRepository loggedUserRepository;

    public PassengerService(PassengerRepository passengerRepository,
                            TripRepository tripRepository,
                            DriverRepository driverRepository,
                            LoggedUserRepository loggedUserRepository) {
        this.passengerRepository = passengerRepository;
        this.tripRepository = tripRepository;
        this.driverRepository = driverRepository;
        this.loggedUserRepository = loggedUserRepository;
    }

    public PassengerDTO registerPassenger(PassengerDTO passengerDTO) {
        if (passengerDTO == null) {
            throw new IllegalArgumentException("Datos de pasajero no recibidos.");
        }
        if (isBlank(passengerDTO.getNombre()) || isBlank(passengerDTO.getEmail()) || isBlank(passengerDTO.getPassword())) {
            throw new IllegalArgumentException("Nombre, email y password son obligatorios.");
        }
        if (!verSiPasajeroEsNuevo(passengerDTO.getEmail())) {
            return null;
        }

        Passenger passenger = new Passenger(
            passengerDTO.getNombre().trim(),
            passengerDTO.getEmail().trim(),
            passengerDTO.getPassword(),
            resolvePosition(passengerDTO),
            passengerDTO.getMetodoPago() != null ? passengerDTO.getMetodoPago() : "efectivo"
        );
        if (passengerDTO.getMonedero() > 0) {
            passenger.setMonedero(passengerDTO.getMonedero());
        }

        Passenger saved = passengerRepository.save(passenger);
        return toDTO(saved);
    }

    public boolean verSiPasajeroEsNuevo(String email) {
        Optional<Passenger> pasajeroEmail = passengerRepository.findByEmail(email);
        return pasajeroEmail.isEmpty();
    }

    public Long loginPassenger(LoginDTO loginDTO) {
        if (loginDTO == null || isBlank(loginDTO.getEmail()) || loginDTO.getPassword() == null) {
            return null;
        }

        Optional<Passenger> authenticated = passengerRepository.findByEmail(loginDTO.getEmail())
            .filter(passenger -> !passenger.isCuentaEliminada())
            .filter(passenger -> loginDTO.getPassword().equals(passenger.getPassword()));

        if (authenticated.isEmpty()) {
            return null;
        }

        Passenger passenger = authenticated.get();
        loggedUserRepository.save(new LoggedUser("PASSENGER", passenger.getId(), UUID.randomUUID().toString()));
        return passenger.getId();
    }

    public PassengerDTO updatePassenger(Long id, PassengerDTO passengerDTO) {
        if (passengerDTO == null) {
            throw new IllegalArgumentException("Datos de pasajero no recibidos.");
        }

        Optional<Passenger> opt = passengerRepository.findById(id);
        if (opt.isEmpty() || opt.get().isCuentaEliminada()) {
            return null;
        }

        Passenger p = opt.get();
        if (passengerDTO.getNombre() != null && !passengerDTO.getNombre().isBlank()) {
            p.setNombre(passengerDTO.getNombre().trim());
        }
        if (passengerDTO.getPassword() != null && !passengerDTO.getPassword().isBlank()) {
            p.setPassword(passengerDTO.getPassword());
        }
        if (passengerDTO.getMetodoPago() != null && !passengerDTO.getMetodoPago().isBlank()) {
            p.setMetodoPago(passengerDTO.getMetodoPago().trim());
        }
        if (passengerDTO.getEmail() != null && !passengerDTO.getEmail().isBlank() && !passengerDTO.getEmail().equals(p.getEmail())) {
            if (passengerRepository.findByEmail(passengerDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email ya existe.");
            }
            p.setEmail(passengerDTO.getEmail().trim());
        }
        Posicion nuevaPosicion = resolveNullablePosition(passengerDTO);
        if (nuevaPosicion != null) {
            p.setPosicionActual(nuevaPosicion);
        }

        return toDTO(passengerRepository.save(p));
    }

    @Transactional
    public boolean logout(Long id) {
        loggedUserRepository.deleteByUserid(id);
        return loggedUserRepository.findByUserid(id).isEmpty();
    }

    /**
     * Nombre legado usado por el controlador antiguo: realmente cierra sesión, no borra cuenta.
     */
    public boolean deletePassenger(Long id) {
        return logout(id);
    }

    @Transactional
    public boolean deletePassengerAccount(Long id) {
        Passenger passenger = passengerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pasajero no encontrado con id: " + id));

        tripRepository.findByClienteId(id).stream()
            .filter(this::isCancellable)
            .forEach(trip -> {
                trip.setEstado(EstadoViaje.CANCELADO);
                trip.setCancelledBy("PASSENGER:" + id);
                trip.setCancelReason("Cuenta de pasajero eliminada");
                trip.setCancelledAt(LocalDateTime.now());
                tripRepository.save(trip);
            });

        passenger.setNombre("Pasajero eliminado");
        passenger.setMetodoPago(null);
        passenger.marcarCuentaEliminada("deleted-passenger-" + id + "@ubesto.local");
        passengerRepository.save(passenger);
        loggedUserRepository.deleteByUserid(id);
        return true;
    }

    public boolean verificarPassword(LoginDTO loginDTO) {
        if (loginDTO == null || isBlank(loginDTO.getEmail()) || loginDTO.getPassword() == null) {
            return false;
        }

        Optional<Passenger> passenger = passengerRepository.findByEmail(loginDTO.getEmail());
        return passenger.isPresent()
            && !passenger.get().isCuentaEliminada()
            && passenger.get().getPassword().equals(loginDTO.getPassword());
    }

    public List<Trip> getTripHistory(Long passengerId) {
        if (!passengerRepository.existsById(passengerId)) {
            throw new EntityNotFoundException("Pasajero no encontrado con id: " + passengerId);
        }
        return tripRepository.findByClienteIdOrderByIdDesc(passengerId);
    }

    public List<TripHistoryDTO> getTripHistoryDTO(Long passengerId) {
        return getTripHistory(passengerId).stream().map(TripHistoryDTO::new).toList();
    }

    public boolean rateTrip(Long tripId, int estrellas) {
        return rateTrip(tripId, null, estrellas);
    }

    public boolean rateTrip(Long tripId, Long passengerId, int estrellas) {
        if (estrellas < 1 || estrellas > 5) {
            return false;
        }

        Optional<Trip> optTrip = tripRepository.findById(tripId);
        if (optTrip.isEmpty()) {
            return false;
        }

        Trip trip = optTrip.get();
        if (trip.getEstado() != EstadoViaje.FINALIZADO) {
            return false;
        }
        if (trip.getCliente() == null) {
            return false;
        }
        if (passengerId != null && !trip.getCliente().getId().equals(passengerId)) {
            return false;
        }

        trip.setRating(estrellas);
        tripRepository.save(trip);
        updateDriverRatingAverage(trip.getConductor());
        return true;
    }

    public Passenger getPassengerById(Long id) {
        return passengerRepository.findById(id).orElse(null);
    }

    public WalletDTO getWallet(Long passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
            .orElseThrow(() -> new EntityNotFoundException("Pasajero no encontrado con id: " + passengerId));

        List<Trip> trips = tripRepository.findByClienteId(passengerId);
        long finalizados = trips.stream().filter(t -> t.getEstado() == EstadoViaje.FINALIZADO).count();
        long cancelados = trips.stream().filter(t -> t.getEstado() == EstadoViaje.CANCELADO).count();

        return new WalletDTO(passengerId, "PASAJERO", round2(passenger.getMonedero()), 0.0,
            finalizados, cancelados, 0, 0.0);
    }

    private void updateDriverRatingAverage(Driver driver) {
        if (driver == null) {
            return;
        }

        List<Trip> viajesConductor = tripRepository.findByConductorId(driver.getId());
        double media = viajesConductor.stream()
            .filter(t -> t.getRating() != null)
            .mapToInt(Trip::getRating)
            .average()
            .orElse(0.0);

        driver.setCalificacionMedia(round2(media));
        driverRepository.save(driver);
    }

    private PassengerDTO toDTO(Passenger passenger) {
        PassengerDTO dto = new PassengerDTO();
        dto.setId(passenger.getId());
        dto.setNombre(passenger.getNombre());
        dto.setEmail(passenger.getEmail());
        dto.setPassword(passenger.getPassword());
        dto.setMetodoPago(passenger.getMetodoPago());
        dto.setMonedero(passenger.getMonedero());
        dto.setPosicionActual(passenger.getPosicionActual());
        if (passenger.getPosicionActual() != null) {
            dto.setLatitud(passenger.getPosicionActual().getLatitud());
            dto.setLongitud(passenger.getPosicionActual().getLongitud());
        }
        return dto;
    }

    private Posicion resolvePosition(PassengerDTO passengerDTO) {
        Posicion posicion = resolveNullablePosition(passengerDTO);
        return posicion != null ? posicion : new Posicion(0.0, 0.0);
    }

    private Posicion resolveNullablePosition(PassengerDTO passengerDTO) {
        if (passengerDTO.getPosicionActual() != null) {
            return passengerDTO.getPosicionActual();
        }
        if (passengerDTO.getLatitud() != 0.0 || passengerDTO.getLongitud() != 0.0) {
            return new Posicion(passengerDTO.getLatitud(), passengerDTO.getLongitud());
        }
        return null;
    }

    private boolean isCancellable(Trip trip) {
        return trip.getEstado() != EstadoViaje.FINALIZADO && trip.getEstado() != EstadoViaje.CANCELADO;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
