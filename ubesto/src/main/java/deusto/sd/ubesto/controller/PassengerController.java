package deusto.sd.ubesto.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.PassengerDTO;
import deusto.sd.ubesto.entity.Trip;
import deusto.sd.ubesto.service.PassengerService;
import deusto.sd.ubesto.entity.Passenger;

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
            if(newPassenger!=null){
                return new ResponseEntity<>(newPassenger, HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/loginPassenger")
    public ResponseEntity<?> loginPassenger(@RequestBody LoginDTO loginDTO) {
        try {
            Long idPassenger = passengerService.loginPassenger(loginDTO);
            if(idPassenger != null){
                return ResponseEntity.ok(idPassenger); 
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o password incorrectos.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallo en el servidor.");
        }
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePassenger(@PathVariable Long id, @RequestBody PassengerDTO passengerDTO) {
        PassengerDTO updated = passengerService.updatePassenger(id, passengerDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pasajero no encontrado.");
        }
    }

    @DeleteMapping("/logout/{id}")
    public ResponseEntity<Boolean> deletePassenger(@PathVariable Long id) {
        boolean borrado = passengerService.deletePassenger(id);
        if (borrado) {
            return new ResponseEntity<>(borrado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(borrado, HttpStatus.NOT_FOUND);
        }
    }

    // ENDPOINT: Historial de viajes
    @GetMapping("/{id}/trips")
    public ResponseEntity<List<Trip>> getTripHistory(@PathVariable Long id) {
        return ResponseEntity.ok(passengerService.getTripHistory(id));
    }

    // ENDPOINT: Valorar viaje
    @PostMapping("/rateTrip/{tripId}")
    public ResponseEntity<String> rateTrip(@PathVariable Long tripId, @RequestParam int estrellas) {
        boolean success = passengerService.rateTrip(tripId, estrellas);
        if (success) {
            return ResponseEntity.ok("Viaje valorado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al valorar el viaje.");
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Long id) {
        // Buscamos el pasajero usando el servicio
        Passenger p = passengerService.getPassengerById(id);
        if (p != null) {
            return ResponseEntity.ok(p);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}