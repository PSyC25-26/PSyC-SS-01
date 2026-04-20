package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.VehicleDTO;
import deusto.sd.ubesto.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    
    public VehicleController(VehicleService vehicleService){
        this.vehicleService = vehicleService;
    }

    @PostMapping("/create/{driverId}")
    public ResponseEntity<?> createVehicle(@RequestBody VehicleDTO vehicleDTO, 
        @PathVariable("driverId") Long driverId) {
        try {
            VehicleDTO newVehicleDTO = vehicleService.createVehicle(vehicleDTO, driverId);
            return new ResponseEntity<>(newVehicleDTO, HttpStatus.CREATED);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Devuelve 404 Not Found
        } catch (Exception e) {
            // Capturamos cualquier otro error inesperado.
            e.printStackTrace();
            return new ResponseEntity<>("Error interno al crear el vehículo.", HttpStatus.INTERNAL_SERVER_ERROR); // Devuelve 500
        }
    }
}