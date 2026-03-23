package deusto.sd.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.dto.DriverDTO;
import deusto.sd.service.*;


@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;
    
    public DriverController(DriverService DriverService){
        this.driverService=DriverService;
    }
    
    @PostMapping("/registerDriver")
    public ResponseEntity<DriverDTO> registerDriver(@RequestBody DriverDTO driverDTO) {
        try {
            DriverDTO newDriver = driverService.registerDriver(driverDTO);
            return new ResponseEntity<DriverDTO>(newDriver, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        
    }
    
}
