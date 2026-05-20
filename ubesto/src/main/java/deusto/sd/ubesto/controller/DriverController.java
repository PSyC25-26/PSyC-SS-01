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
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.DriverDTO;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.TripHistoryDTO;
import deusto.sd.ubesto.dto.WalletDTO;
import deusto.sd.ubesto.entity.Driver;
import deusto.sd.ubesto.service.DriverService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    public DriverService getDriverService() {
        return driverService;
    }

    @PostMapping("/registerDriver")
    public ResponseEntity<DriverDTO> registerDriver(@RequestBody DriverDTO driverDTO) {
        try {
            DriverDTO newDriver = driverService.registerDriver(driverDTO);
            return new ResponseEntity<>(newDriver, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/loginDriver")
    public ResponseEntity<?> loginDriver(@RequestBody LoginDTO loginDTO) {
        try {
            Long idDriver = driverService.loginDriver(loginDTO);
            if (idDriver != null) {
                return ResponseEntity.ok(idDriver);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o password incorrectos.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallo en el servidor.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody DriverDTO driverDTO) {
        try {
            DriverDTO updated = driverService.updateDriver(id, driverDTO);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conductor no encontrado.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @DeleteMapping("/logout/{id}")
    public ResponseEntity<Boolean> logoutDriver(@PathVariable Long id) {
        boolean borrado = driverService.logout(id);
        return new ResponseEntity<>(borrado, borrado ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriverAccount(@PathVariable Long id) {
        try {
            driverService.deleteDriverAccount(id);
            return ResponseEntity.ok(true);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<?> deleteDriverAccountLegacy(@PathVariable Long id) {
        return deleteDriverAccount(id);
    }

    @GetMapping("/{id}/trips")
    public ResponseEntity<?> getTripHistory(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(driverService.getTripHistory(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/wallet")
    public ResponseEntity<?> getWallet(@PathVariable Long id) {
        try {
            WalletDTO wallet = driverService.getWallet(id);
            return ResponseEntity.ok(wallet);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/vehicles")
    public ResponseEntity<?> getVehicles(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(driverService.getVehicles(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
        Driver d = driverService.getDriverById(id);
        if (d != null) {
            return ResponseEntity.ok(d);
        }
        return ResponseEntity.notFound().build();
    }
}
