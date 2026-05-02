package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.DriverDTO;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.service.DriverService;


@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverService getDriverService() {
        return driverService;
    } 
    
    public DriverController(DriverService DriverService){
        this.driverService=DriverService;
    }

    // @ApiResponse(responseCode = "201", description = "Driver registrado correctamente")
   @PostMapping("/registerDriver")
    public ResponseEntity<DriverDTO> registerDriver(@RequestBody DriverDTO driverDTO) {
        try {
            DriverDTO newDriver = driverService.registerDriver(driverDTO);
            return new ResponseEntity<>(newDriver, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/loginDriver")
    public ResponseEntity<?> loginDriver(@RequestBody LoginDTO loginDTO) {
        try {
            Long idDriver = driverService.loginDriver(loginDTO);
            if(idDriver != null){
                return ResponseEntity.ok(idDriver); // Devuelve HTTP 200 con el ID
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o password incorrectos.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallo en el servidor.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody DriverDTO driverDTO) {
        DriverDTO updated = driverService.updateDriver(id, driverDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conductor no encontrado.");
        }
    }
 
}
