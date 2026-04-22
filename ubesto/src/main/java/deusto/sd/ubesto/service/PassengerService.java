package deusto.sd.ubesto.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.LoggedUserRepository;
import deusto.sd.ubesto.dao.PassengerRepository;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.PassengerDTO;
import deusto.sd.ubesto.entity.LoggedUser;
import deusto.sd.ubesto.entity.Passenger;

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
    
            Passenger newPassenger = new Passenger(passengerDTO.getNombre(), passengerDTO.getEmail(), passengerDTO.getPassword(), passengerDTO.getPosicionActual(), passengerDTO.getMetodoPago());
            Passenger savedPassenger = passengerRepository.save(newPassenger);
    
            passengerDTO.setId(savedPassenger.getId());
    
            return passengerDTO;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long loginPassenger(LoginDTO loginDTO){
        try {
            boolean correcto = verificarPassword(loginDTO);
            if(correcto){
                Passenger passenger = passengerRepository.findByEmail(loginDTO.getEmail()).get();
                UUID token = UUID.randomUUID();
                LoggedUser loggedUser = new LoggedUser("PASSENGER", passenger.getId(), token.toString());
                loggedUserRepository.save(loggedUser);
                
                return passenger.getId(); // Devolvemos el ID
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public PassengerDTO updatePassenger(Long id, PassengerDTO passengerDTO) {
        try {
            Passenger passenger = passengerRepository.findById(id).orElse(null);
            if (passenger == null) return null;

            if (passengerDTO.getNombre() != null && !passengerDTO.getNombre().isBlank())
                passenger.setNombre(passengerDTO.getNombre());

            if (passengerDTO.getPassword() != null && !passengerDTO.getPassword().isBlank())
                passenger.setPassword(passengerDTO.getPassword());

            if (passengerDTO.getMetodoPago() != null && !passengerDTO.getMetodoPago().isBlank())
                passenger.setMetodoPago(passengerDTO.getMetodoPago());

            passengerRepository.save(passenger);
            return passengerDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
