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

    // Constructor que inyecta ambos repositorios
    public PassengerService(PassengerRepository passengerRepository, TripRepository tripRepository) {
        this.passengerRepository = passengerRepository;
        this.tripRepository = tripRepository;
    }

    // 1. FUNCIONALIDAD NUEVA: Historial de viajes
    public List<Trip> getTripHistory(Long passengerId) {
        return tripRepository.findByClienteId(passengerId);
    }

    // 2. Registro de Pasajero
    public PassengerDTO registerPassenger(PassengerDTO passengerDTO) {
        Passenger passenger = new Passenger();
        passenger.setNombre(passengerDTO.getNombre());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setPassword(passengerDTO.getPassword());
        passenger.setMetodoPago(passengerDTO.getMetodoPago());
        
        // Se guarda en la base de datos
        Passenger saved = passengerRepository.save(passenger);
        passengerDTO.setId(saved.getId());
        return passengerDTO;
    }

    // 3. Login de Pasajero
    public Long loginPassenger(LoginDTO loginDTO) {
        Passenger passenger = passengerRepository.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        if (passenger != null) {
            return passenger.getId();
        }
        return null; 
    }

    // 4. Actualizar Pasajero
    public PassengerDTO updatePassenger(Long id, PassengerDTO passengerDTO) {
        Optional<Passenger> opt = passengerRepository.findById(id);
        if (opt.isPresent()) {
            Passenger p = opt.get();
            p.setNombre(passengerDTO.getNombre());
            p.setEmail(passengerDTO.getEmail());
            p.setMetodoPago(passengerDTO.getMetodoPago());
            passengerRepository.save(p);
            return passengerDTO;
        }
        return null;
    }

    // 5. Borrar Pasajero / Logout
    public boolean deletePassenger(Long id) {
        if (passengerRepository.existsById(id)) {
            passengerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 6. Método necesario para tus Tests Unitarios
    public boolean verificarPassword(LoginDTO loginDTO) {
        Passenger passenger = passengerRepository.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        return passenger != null;
    }
}