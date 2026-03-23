package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String loginPassenger(@RequestBody LoginDTO loginDTO) {
        try {
            boolean loginCorrecto = passengerService.loginPassenger(loginDTO);
            if(loginCorrecto){
                return loginDTO.getEmail() +" ha iniciado sesión correctamente!";
            }else{
                return "Ha habido un error en el inicio de sesión. Revise los datos y vuelva a intentarlo";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ha habido un error en el inicio de sesión. Revise los datos y vuelva a intentarlo";
        }
    }
    
}
