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
    public ResponseEntity<String> loginPassenger(@RequestBody LoginDTO loginDTO) {
        try {
            boolean loginCorrecto = passengerService.loginPassenger(loginDTO);
            if(loginCorrecto){
                return  new ResponseEntity<>(loginDTO.getEmail() +" ha iniciado sesión correctamente!",
                HttpStatus.ACCEPTED);
            }else{
                return new ResponseEntity<>("Ha habido un error en el inicio de sesión. Revise los datos y vuelva a intentarlo",
                    HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Ha habido un fallo en el inicio de sesión. Revise el error y vuelva a intentarlo.",
                HttpStatus.BAD_REQUEST);
        }
    }
    
}
