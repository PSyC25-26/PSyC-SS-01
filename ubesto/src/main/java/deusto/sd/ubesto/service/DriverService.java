package deusto.sd.ubesto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.DriverRepository;
import deusto.sd.ubesto.dao.LoggedUserRepository;
import deusto.sd.ubesto.dao.TripRepository;
import deusto.sd.ubesto.dao.VehicleRepository;
import deusto.sd.ubesto.dto.DriverDTO;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.TripHistoryDTO;
import deusto.sd.ubesto.dto.VehicleDTO;
import deusto.sd.ubesto.dto.WalletDTO;
import deusto.sd.ubesto.entity.Driver;
import deusto.sd.ubesto.entity.LoggedUser;
import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Trip;
import deusto.sd.ubesto.entity.Vehicle;
import deusto.sd.ubesto.entity.Trip.EstadoViaje;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final LoggedUserRepository loggedUserRepository;
    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;

    public DriverService(DriverRepository driverRepository, LoggedUserRepository loggedUserRepository,
                         TripRepository tripRepository, VehicleRepository vehicleRepository) {
        this.driverRepository = driverRepository;
        this.loggedUserRepository = loggedUserRepository;
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public DriverDTO registerDriver(DriverDTO driverDTO) {
        if (driverDTO == null) {
            throw new IllegalArgumentException("Datos de conductor no recibidos.");
        }
        if (isBlank(driverDTO.getNombre()) || isBlank(driverDTO.getEmail()) || isBlank(driverDTO.getPassword())) {
            throw new IllegalArgumentException("Nombre, email y password son obligatorios.");
        }
        if (driverRepository.findByEmail(driverDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email ya existe.");
        }

        Driver newDriver = new Driver(
            driverDTO.getNombre().trim(),
            driverDTO.getEmail().trim(),
            driverDTO.getPassword(),
            driverDTO.getLicenciaConducir(),
            driverDTO.getCalificacionMedia() > 0 ? driverDTO.getCalificacionMedia() : 5.0,
            null,
            driverDTO.getPosicionActual() != null ? driverDTO.getPosicionActual() : new Posicion(0.0, 0.0)
        );
        newDriver.setMonedero(driverDTO.getMonedero() > 0 ? driverDTO.getMonedero() : 0.0);

        Driver savedDriver = driverRepository.save(newDriver);
        return toDTO(savedDriver);
    }

    public Long loginDriver(LoginDTO loginDTO) {
        if (!verificarPassword(loginDTO)) {
            return null;
        }

        Driver driver = driverRepository.findByEmail(loginDTO.getEmail()).orElse(null);
        if (driver == null || driver.isCuentaEliminada()) {
            return null;
        }

        UUID token = UUID.randomUUID();
        LoggedUser loggedUser = new LoggedUser("DRIVER", driver.getId(), token.toString());
        loggedUserRepository.save(loggedUser);
        return driver.getId();
    }

    public boolean verificarPassword(LoginDTO loginDTO) {
        if (loginDTO == null || isBlank(loginDTO.getEmail()) || loginDTO.getPassword() == null) {
            return false;
        }

        Optional<Driver> driver = driverRepository.findByEmail(loginDTO.getEmail());
        return driver.isPresent()
            && !driver.get().isCuentaEliminada()
            && loginDTO.getPassword().equals(driver.get().getPassword());
    }

    public DriverDTO updateDriver(Long id, DriverDTO driverDTO) {
        if (driverDTO == null) {
            throw new IllegalArgumentException("Datos de conductor no recibidos.");
        }

        Driver driver = driverRepository.findById(id).orElse(null);
        if (driver == null || driver.isCuentaEliminada()) {
            return null;
        }

        if (driverDTO.getNombre() != null && !driverDTO.getNombre().isBlank()) {
            driver.setNombre(driverDTO.getNombre().trim());
        }
        if (driverDTO.getPassword() != null && !driverDTO.getPassword().isBlank()) {
            driver.setPassword(driverDTO.getPassword());
        }
        if (driverDTO.getEmail() != null && !driverDTO.getEmail().isBlank() && !driverDTO.getEmail().equals(driver.getEmail())) {
            if (driverRepository.findByEmail(driverDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email ya existe.");
            }
            driver.setEmail(driverDTO.getEmail().trim());
        }
        if (driverDTO.getLicenciaConducir() != null && !driverDTO.getLicenciaConducir().isBlank()) {
            driver.setLicenciaConducir(driverDTO.getLicenciaConducir().trim());
        }
        if (driverDTO.getPosicionActual() != null) {
            driver.setPosicionActual(driverDTO.getPosicionActual());
        }

        return toDTO(driverRepository.save(driver));
    }

    @Transactional
    public boolean logout(Long id) {
        loggedUserRepository.deleteByUserid(id);
        return loggedUserRepository.findByUserid(id).isEmpty();
    }

    /**
     * Nombre legado usado por el controlador antiguo: realmente cierra sesión, no borra cuenta.
     */
    public boolean deleteDriver(Long id) {
        return logout(id);
    }

    @Transactional
    public boolean deleteDriverAccount(Long id) {
        Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado con id: " + id));

        tripRepository.findByConductorId(id).stream()
            .filter(this::isCancellable)
            .forEach(trip -> {
                trip.setEstado(EstadoViaje.CANCELADO);
                trip.setCancelledBy("DRIVER:" + id);
                trip.setCancelReason("Cuenta de conductor eliminada");
                trip.setCancelledAt(LocalDateTime.now());
                tripRepository.save(trip);
            });

        driver.setNombre("Conductor eliminado");
        driver.setLicenciaConducir("");
        driver.setVehicleActiveId(null);
        driver.marcarCuentaEliminada("deleted-driver-" + id + "@ubesto.local");
        driverRepository.save(driver);
        loggedUserRepository.deleteByUserid(id);
        return true;
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id).orElse(null);
    }

    public List<TripHistoryDTO> getTripHistory(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new EntityNotFoundException("Conductor no encontrado con id: " + driverId);
        }
        return tripRepository.findByConductorIdOrderByIdDesc(driverId)
            .stream()
            .map(TripHistoryDTO::new)
            .toList();
    }

    public WalletDTO getWallet(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado con id: " + driverId));

        List<Trip> trips = tripRepository.findByConductorId(driverId);
        double ganancias = trips.stream()
            .filter(t -> t.getEstado() == EstadoViaje.FINALIZADO)
            .mapToDouble(Trip::getPrecio)
            .sum();
        long finalizados = trips.stream().filter(t -> t.getEstado() == EstadoViaje.FINALIZADO).count();
        long cancelados = trips.stream().filter(t -> t.getEstado() == EstadoViaje.CANCELADO).count();
        long valoraciones = trips.stream().filter(t -> t.getRating() != null).count();

        return new WalletDTO(driverId, "CONDUCTOR", round2(driver.getMonedero()), round2(ganancias),
            finalizados, cancelados, valoraciones, round2(driver.getCalificacionMedia()));
    }

    public List<VehicleDTO> getVehicles(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new EntityNotFoundException("Conductor no encontrado con id: " + driverId);
        }
        return vehicleRepository.findByDriver_Id(driverId).stream()
            .map(this::toVehicleDTO)
            .toList();
    }


    private VehicleDTO toVehicleDTO(Vehicle vehicle) {
        Driver driver = vehicle.getDriver();
        DriverDTO driverDTO = driver != null ? toDTO(driver) : null;
        return new VehicleDTO(
            vehicle.getId(),
            vehicle.getMatricula(),
            vehicle.getMarca(),
            vehicle.getModelo(),
            vehicle.getColor(),
            vehicle.getCategoria(),
            driverDTO
        );
    }

    private DriverDTO toDTO(Driver driver) {
        DriverDTO dto = new DriverDTO(
            driver.getId(),
            driver.getNombre(),
            driver.getEmail(),
            driver.getPassword(),
            driver.getLicenciaConducir(),
            driver.getCalificacionMedia(),
            driver.getVehicleActiveId(),
            driver.getPosicionActual()
        );
        dto.setMonedero(driver.getMonedero());
        return dto;
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
