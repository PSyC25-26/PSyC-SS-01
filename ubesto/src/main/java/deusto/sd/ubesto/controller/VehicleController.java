package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.VehicleDTO;
import deusto.sd.ubesto.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/create/{driverId}")
    public ResponseEntity<?> createVehicle(@RequestBody VehicleDTO vehicleDTO,
                                           @PathVariable("driverId") Long driverId) {
        try {
            VehicleDTO newVehicleDTO = vehicleService.createVehicle(vehicleDTO, driverId);
            return new ResponseEntity<>(newVehicleDTO, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno al crear el vehículo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/driver/{driverId}/active")
    public ResponseEntity<?> activateVehicle(@PathVariable Long driverId, @RequestParam String matricula) {
        try {
            return ResponseEntity.ok(vehicleService.activateVehicleByMatricula(driverId, matricula));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<?> getVehiclesByDriver(@PathVariable Long driverId) {
        try {
            return ResponseEntity.ok(vehicleService.getVehiclesByDriver(driverId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
