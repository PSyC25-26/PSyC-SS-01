package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.PassengerDTO;
import deusto.sd.ubesto.service.PassengerService;
import deusto.sd.ubesto.entity.Trip;
import java.util.List;

@RestController
@RequestMapping("/passengers")
public class PassengerController {
    private final PassengerService passengerService;
    
    public PassengerController(PassengerService passengerService){
        this.passengerService = passengerService;
    }

    @PostMapping("/registerPassenger")
    public ResponseEntity<PassengerDTO> registerPassenger(@RequestBody PassengerDTO passengerDTO) {
        try {
            PassengerDTO newPassenger = passengerService.registerPassenger(passengerDTO);
            return new ResponseEntity<>(newPassenger, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/{id}/trips")
    public ResponseEntity<List<Trip>> getTripHistory(@PathVariable Long id) {
        return ResponseEntity.ok(passengerService.getTripHistory(id));
    }

    // ... (Mantén aquí tus métodos loginPassenger, updatePassenger y deletePassenger originales)
}