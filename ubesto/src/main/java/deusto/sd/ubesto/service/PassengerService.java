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
    private final TripRepository tripRepository; // AÑADIDO: Repositorio de viajes

    // Constructor con inyección de dependencias (AÑADIDO el TripRepository)
    public PassengerService(PassengerRepository passengerRepository, TripRepository tripRepository) {
        this.passengerRepository = passengerRepository;
        this.tripRepository = tripRepository;
    }

    // --- MÉTODOS ANTERIORES (Inferidos por el Controlador) ---

    public PassengerDTO registerPassenger(PassengerDTO passengerDTO) {
        Passenger passenger = new Passenger();
        passenger.setNombre(passengerDTO.getNombre());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setPassword(passengerDTO.getPassword());
        
        if (passengerDTO.getLatitud() != 0.0 && passengerDTO.getLongitud() != 0.0) {
            passenger.setPosicionActual(new Posicion(passengerDTO.getLatitud(), passengerDTO.getLongitud()));
        }
        passenger.setMetodoPago(passengerDTO.getMetodoPago());

        Passenger savedPassenger = passengerRepository.save(passenger);
        passengerDTO.setId(savedPassenger.getId());
        return passengerDTO;
    }

    public Long loginPassenger(LoginDTO loginDTO) {
        // Asumiendo que tienes un método findByEmailAndPassword en tu PassengerRepository
        Passenger passenger = passengerRepository.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        if (passenger != null) {
            return passenger.getId();
        }
        return null; 
    }

    public PassengerDTO updatePassenger(Long id, PassengerDTO passengerDTO) {
        Optional<Passenger> optionalPassenger = passengerRepository.findById(id);
        if (optionalPassenger.isPresent()) {
            Passenger passenger = optionalPassenger.get();
            passenger.setNombre(passengerDTO.getNombre());
            passenger.setEmail(passengerDTO.getEmail());
            passenger.setPassword(passengerDTO.getPassword());
            passenger.setMetodoPago(passengerDTO.getMetodoPago());
            
            if (passengerDTO.getLatitud() != 0.0 && passengerDTO.getLongitud() != 0.0) {
                 passenger.setPosicionActual(new Posicion(passengerDTO.getLatitud(), passengerDTO.getLongitud()));
            }

            passengerRepository.save(passenger);
            passengerDTO.setId(passenger.getId());
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

    // --- NUEVO MÉTODO IMPLEMENTADO ---
    
    public List<Trip> getTripHistory(Long passengerId) {
        // Llama al TripRepository para buscar los viajes del pasajero
        return tripRepository.findByClienteId(passengerId);
    }
}