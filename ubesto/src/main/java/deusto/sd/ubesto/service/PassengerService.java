package deusto.sd.ubesto.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.PassengerRepository;
import deusto.sd.ubesto.dto.*;
import deusto.sd.ubesto.entity.LoggedUser;
import deusto.sd.ubesto.entity.Passenger;
import deusto.sd.ubesto.dao.LoggedUserRepository;

@Service
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final LoggedUserRepository loggedUserRepository;

    public PassengerService(PassengerRepository passengerRepository, LoggedUserRepository loggedUserRepository) {
        this.passengerRepository = passengerRepository;
        this.loggedUserRepository = loggedUserRepository;
    }

    public PassengerDTO registerPassenger(PassengerDTO passengerDTO){
        try {
            if(passengerDTO.getId()!=null){
                passengerDTO.setId(null);
            }
    
            Passenger newPassenger = new Passenger(passengerDTO.getNombre(), passengerDTO.getEmail(), passengerDTO.getPassword(), passengerDTO.getMetodoPago(), 
            passengerDTO.getPosicionActual());
            Passenger savedPassenger = passengerRepository.save(newPassenger);
    
            passengerDTO.setId(savedPassenger.getId());
    
            return passengerDTO;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean loginPassenger(LoginDTO loginDTO){
        try {
            boolean correcto = verificarPassword(loginDTO);
            if(correcto){
                UUID token = UUID.randomUUID();
                Passenger passenger = passengerRepository.findByEmail(loginDTO.getEmail()).get();
                LoggedUser loggedUser = new LoggedUser("PASSENGER", passenger.getId(), token.toString());

                loggedUserRepository.save(loggedUser);
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarPassword(LoginDTO loginDTO){
        String real_pw=passengerRepository.findByEmail(loginDTO.getEmail()).get().getPassword();
        if(loginDTO.getPassword().equals(real_pw)){
            return true;
        }else{
            return false;
        }
    }

    
}
