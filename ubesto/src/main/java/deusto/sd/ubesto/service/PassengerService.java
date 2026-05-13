package deusto.sd.ubesto.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import deusto.sd.ubesto.dao.PassengerRepository;
import deusto.sd.ubesto.dao.TripRepository;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.PassengerDTO;
import deusto.sd.ubesto.entity.Passenger;
import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Trip;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final TripRepository tripRepository;

    public PassengerService(PassengerRepository passengerRepository, TripRepository tripRepository) {
        this.passengerRepository = passengerRepository;
        this.tripRepository = tripRepository;
    }

    public List<Trip> getTripHistory(Long passengerId) {
        return tripRepository.findByClienteId(passengerId);
    }

    public PassengerDTO registerPassenger(PassengerDTO passengerDTO) {
        Passenger passenger = new Passenger();
        passenger.setNombre(passengerDTO.getNombre());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setPassword(passengerDTO.getPassword());
        passenger.setMetodoPago(passengerDTO.getMetodoPago());
        
        if (passengerDTO.getLatitud() != 0.0 && passengerDTO.getLongitud() != 0.0) {
            passenger.setPosicionActual(new Posicion(passengerDTO.getLatitud(), passengerDTO.getLongitud()));
        }

        Passenger saved = passengerRepository.save(passenger);
        passengerDTO.setId(saved.getId());
        return passengerDTO;
    }

    public Long loginPassenger(LoginDTO loginDTO) {
        Passenger passenger = passengerRepository.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        if (passenger != null) {
            return passenger.getId();
        }
        return null; 
    }

    public PassengerDTO updatePassenger(Long id, PassengerDTO passengerDTO) {
        Optional<Passenger> opt = passengerRepository.findById(id);
        if (opt.isPresent()) {
            Passenger p = opt.get();
            p.setNombre(passengerDTO.getNombre());
            p.setEmail(passengerDTO.getEmail());
            p.setMetodoPago(passengerDTO.getMetodoPago());
            
            if (passengerDTO.getLatitud() != 0.0 && passengerDTO.getLongitud() != 0.0) {
                p.setPosicionActual(new Posicion(passengerDTO.getLatitud(), passengerDTO.getLongitud()));
            }

            passengerRepository.save(p);
            passengerDTO.setId(p.getId());
            return passengerDTO;
        }
        return null;
    }

    public boolean deletePassenger(Long id) {
        if (passengerRepository.existsById(id)) {
            passengerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Método añadido para solucionar el error en UnitariosTest.java
    public boolean verificarPassword(LoginDTO loginDTO) {
        Passenger passenger = passengerRepository.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        return passenger != null;
    }
}