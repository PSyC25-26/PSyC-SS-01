package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.PassengerDTO;
import deusto.sd.ubesto.service.PassengerService;

@RestController
@RequestMapping("/passengers")
public class PassengerController {
    private final PassengerService passengerService;
    
    public PassengerController(PassengerService passengerService){
        this.passengerService=passengerService;
    }

    @PostMapping("/registerPassenger")
    public ResponseEntity<PassengerDTO> registerPassenger(@RequestBody PassengerDTO passengerDTO) {
        try {
            PassengerDTO newPassenger = passengerService.registerPassenger(passengerDTO);
            if(newPassenger==null){
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }else{
                return new ResponseEntity<PassengerDTO>(newPassenger, HttpStatus.CREATED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/loginPassenger")
    public ResponseEntity<?> loginPassenger(@RequestBody LoginDTO loginDTO) {
        try {
            Long idPassenger = passengerService.loginPassenger(loginDTO);
            if(idPassenger != null){
                return ResponseEntity.ok(idPassenger); // Devuelve HTTP 200 con el ID
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
}
