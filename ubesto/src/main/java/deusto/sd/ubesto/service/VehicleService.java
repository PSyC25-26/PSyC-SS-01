package deusto.sd.ubesto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.DriverRepository;
import deusto.sd.ubesto.dao.VehicleRepository;
import deusto.sd.ubesto.dto.DriverDTO;
import deusto.sd.ubesto.dto.VehicleDTO;
import deusto.sd.ubesto.entity.Driver;
import deusto.sd.ubesto.entity.Vehicle;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public VehicleService(VehicleRepository vehicleRepository, DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public VehicleDTO createVehicle(VehicleDTO vehicleDTO, Long driverId) {
        if (vehicleDTO == null) {
            throw new IllegalArgumentException("Datos de vehículo no recibidos.");
        }
        if (vehicleDTO.getMatricula() == null || vehicleDTO.getMatricula().isBlank()) {
            throw new IllegalArgumentException("La matrícula es obligatoria.");
        }

        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado con id: " + driverId));

        if (driver.isCuentaEliminada()) {
            throw new EntityNotFoundException("Conductor no encontrado con id: " + driverId);
        }

        String matricula = vehicleDTO.getMatricula().trim().toUpperCase();
        vehicleRepository.findByMatricula(matricula)
            .ifPresent(v -> {
                throw new IllegalArgumentException("Ya existe un vehículo con esa matrícula.");
            });

        CategoriaVehiculo categoria = vehicleDTO.getCategoria() != null ? vehicleDTO.getCategoria() : CategoriaVehiculo.UBERX;

        Vehicle newVehicle = new Vehicle(
            matricula,
            trimOrNull(vehicleDTO.getMarca()),
            trimOrNull(vehicleDTO.getModelo()),
            trimOrNull(vehicleDTO.getColor()),
            categoria,
            driver
        );
        Vehicle savedVehicle = vehicleRepository.save(newVehicle);

        driver.setVehicleActiveId(savedVehicle.getId());
        driverRepository.save(driver);

        return toDTO(savedVehicle, driver);
    }

    @Transactional
    public VehicleDTO activateVehicleByMatricula(Long driverId, String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new EntityNotFoundException("Matrícula no recibida.");
        }

        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado con id: " + driverId));

        String matriculaNormalizada = matricula.trim().toUpperCase();
        Vehicle vehicle = vehicleRepository.findByDriver_IdAndMatricula(driverId, matriculaNormalizada)
            .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado para el conductor con matrícula: " + matriculaNormalizada));

        driver.setVehicleActiveId(vehicle.getId());
        driverRepository.save(driver);
        return toDTO(vehicle, driver);
    }

    public List<VehicleDTO> getVehiclesByDriver(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new EntityNotFoundException("Conductor no encontrado con id: " + driverId);
        }
        return vehicleRepository.findByDriver_Id(driverId).stream()
            .map(vehicle -> toDTO(vehicle, vehicle.getDriver()))
            .toList();
    }


    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }

    private VehicleDTO toDTO(Vehicle vehicle, Driver driver) {
        DriverDTO driverDTO = new DriverDTO(
            driver.getId(),
            driver.getNombre(),
            driver.getEmail(),
            driver.getPassword(),
            driver.getLicenciaConducir(),
            driver.getCalificacionMedia(),
            driver.getVehicleActiveId(),
            driver.getPosicionActual()
        );
        driverDTO.setMonedero(driver.getMonedero());

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
}
