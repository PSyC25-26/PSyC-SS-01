package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.*;
import deusto.sd.ubesto.service.*;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    
    public VehicleController(VehicleService vehicleService){
        this.vehicleService=vehicleService;
    }

    @PostMapping("/createVehicle/{driver_id}")
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO, 
        @PathVariable("driver_id") Long driver_id) {
        try {
            VehicleDTO newVehicle = vehicleService.createVehicle(vehicleDTO, driver_id);
            if(newVehicle==null){
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }else{
                return new ResponseEntity<VehicleDTO>(newVehicle, HttpStatus.CREATED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
