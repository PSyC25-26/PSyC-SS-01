package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.PassengerDTO;
import deusto.sd.ubesto.dto.TripHistoryDTO;
import deusto.sd.ubesto.dto.WalletDTO;
import deusto.sd.ubesto.entity.Passenger;
import deusto.sd.ubesto.service.PassengerService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/passengers")
public class PassengerController {
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping("/registerPassenger")
    public ResponseEntity<PassengerDTO> registerPassenger(@RequestBody PassengerDTO passengerDTO) {
        try {
            PassengerDTO newPassenger = passengerService.registerPassenger(passengerDTO);
            if (newPassenger != null) {
                return new ResponseEntity<>(newPassenger, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/loginPassenger")
    public ResponseEntity<?> loginPassenger(@RequestBody LoginDTO loginDTO) {
        try {
            Long idPassenger = passengerService.loginPassenger(loginDTO);
            if (idPassenger != null) {
                return ResponseEntity.ok(idPassenger);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o password incorrectos.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallo en el servidor.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePassenger(@PathVariable Long id, @RequestBody PassengerDTO passengerDTO) {
        try {
            PassengerDTO updated = passengerService.updatePassenger(id, passengerDTO);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pasajero no encontrado.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @DeleteMapping("/logout/{id}")
    public ResponseEntity<Boolean> logoutPassenger(@PathVariable Long id) {
        boolean borrado = passengerService.logout(id);
        return new ResponseEntity<>(borrado, borrado ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePassengerAccount(@PathVariable Long id) {
        try {
            passengerService.deletePassengerAccount(id);
            return ResponseEntity.ok(true);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<?> deletePassengerAccountLegacy(@PathVariable Long id) {
        return deletePassengerAccount(id);
    }

    @GetMapping("/{id}/trips")
    public ResponseEntity<?> getTripHistory(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(passengerService.getTripHistoryDTO(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/rateTrip/{tripId}")
    public ResponseEntity<String> rateTrip(@PathVariable Long tripId, @RequestParam int estrellas) {
        boolean success = passengerService.rateTrip(tripId, estrellas);
        if (success) {
            return ResponseEntity.ok("Viaje valorado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al valorar el viaje.");
    }

    @GetMapping("/{id}/wallet")
    public ResponseEntity<?> getWallet(@PathVariable Long id) {
        try {
            WalletDTO wallet = passengerService.getWallet(id);
            return ResponseEntity.ok(wallet);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Long id) {
        Passenger p = passengerService.getPassengerById(id);
        if (p != null) {
            return ResponseEntity.ok(p);
        }
        return ResponseEntity.notFound().build();
    }
}
