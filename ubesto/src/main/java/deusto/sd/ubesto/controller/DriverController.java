package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.*;
import deusto.sd.ubesto.service.*;


@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;
    
    public DriverController(DriverService DriverService){
        this.driverService=DriverService;
    }

    // @ApiResponse(responseCode = "201", description = "Driver registrado correctamente")
    @PostMapping("/registerDriver")
    public ResponseEntity<DriverDTO> registerDriver(@RequestBody DriverDTO driverDTO) {
        try {
            DriverDTO newDriver = driverService.registerDriver(driverDTO);
            if(newDriver==null){
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }else{
                return new ResponseEntity<DriverDTO>(newDriver, HttpStatus.CREATED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/loginDriver")
    public String loginDriver(@RequestBody LoginDTO loginDTO) {
        try {
            boolean loginCorrecto = driverService.loginDriver(loginDTO);
            if(loginCorrecto==true){
                return loginDTO.getEmail() +" ha iniciado sesión correctamente!";
            }else{
                return "Ha habido un error en el inicio de sesión. Revise los datos y vuelva a intentarlo.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ha habido un fallo en el inicio de sesión. Revise el error y vuelva a intentarlo.";
        }
    } 
}
