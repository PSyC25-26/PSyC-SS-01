package deusto.sd.ubesto.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import deusto.sd.ubesto.dao.DriverRepository;
import deusto.sd.ubesto.dao.LoggedUserRepository; 
import deusto.sd.ubesto.dao.PassengerRepository;
import deusto.sd.ubesto.dao.TripRepository;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.PassengerDTO;
import deusto.sd.ubesto.entity.Driver;
import deusto.sd.ubesto.entity.Passenger;
import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Trip;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final LoggedUserRepository loggedUserRepository; 

    public PassengerService(PassengerRepository passengerRepository, 
                            TripRepository tripRepository, 
                            DriverRepository driverRepository,
                            LoggedUserRepository loggedUserRepository) { 
        this.passengerRepository = passengerRepository;
        this.tripRepository = tripRepository;
        this.driverRepository = driverRepository;
        this.loggedUserRepository = loggedUserRepository; 
    }

    public PassengerDTO registerPassenger(PassengerDTO passengerDTO) {
        Passenger passenger = new Passenger(passengerDTO.getNombre(),passengerDTO.getEmail(),passengerDTO.getPassword(),
        passengerDTO.getMetodoPago());

        if (passengerDTO.getLatitud() != 0.0 && passengerDTO.getLongitud() != 0.0) {
            passenger.setPosicionActual(new Posicion(passengerDTO.getLatitud(), passengerDTO.getLongitud()));
        }
        boolean esNuevo = verSiPasajeroEsNuevo(passengerDTO.getEmail());
        if(esNuevo){
            Passenger saved = passengerRepository.save(passenger);
            passengerDTO.setId(saved.getId());
            return passengerDTO;
        }else{
            return null;
        }
    }

    public boolean verSiPasajeroEsNuevo(String email){
        Optional<Passenger> pasajero_email=passengerRepository.findByEmail(email);
        if(!pasajero_email.isPresent()){ // si no encuentra el email, es que es nuevo
            return true;
        }else{
            return false;
        }

    }

    public Long loginPassenger(LoginDTO loginDTO) {
        Passenger passenger = passengerRepository.findByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        return (passenger != null) ? passenger.getId() : null;
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
        try {
            loggedUserRepository.deleteByUserid(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Método para UnitariosTest.java
    public boolean verificarPassword(LoginDTO loginDTO) {
        Optional<Passenger> passenger = passengerRepository.findByEmail(loginDTO.getEmail());
    
        // Si existe y la contraseña coincide, devolvemos true
        return passenger.isPresent() && passenger.get().getPassword().equals(loginDTO.getPassword());
    }

    // HISTORIAL DE VIAJES
    public List<Trip> getTripHistory(Long passengerId) {
        return tripRepository.findByClienteId(passengerId);
    }

    // VALORAR VIAJE Y ACTUALIZAR MEDIA DEL CONDUCTOR
    public boolean rateTrip(Long tripId, int estrellas) {
        if (estrellas < 1 || estrellas > 5) return false;

        Optional<Trip> optTrip = tripRepository.findById(tripId);
        if (optTrip.isPresent()) {
            Trip trip = optTrip.get();
            if(trip.getEstado() != Trip.EstadoViaje.FINALIZADO) return false;

            trip.setRating(estrellas);
            tripRepository.save(trip);

            Driver driver = trip.getConductor();
            if (driver != null) {
                List<Trip> viajesConductor = tripRepository.findByConductorId(driver.getId());
                double media = viajesConductor.stream()
                        .filter(t -> t.getRating() != null)
                        .mapToInt(Trip::getRating)
                        .average()
                        .orElse(0.0);
                
                driver.setCalificacionMedia(media);
                driverRepository.save(driver);
            }
            return true;
        }
        return false;
    }
    public Passenger getPassengerById(Long id) {
        return passengerRepository.findById(id).orElse(null);
    }
}