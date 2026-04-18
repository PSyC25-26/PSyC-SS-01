package deusto.sd.ubesto.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.PassengerRepository;
import deusto.sd.ubesto.dao.TripRepository;
import deusto.sd.ubesto.dto.TripDTO;
import deusto.sd.ubesto.entity.Passenger;
import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Trip;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final PassengerRepository passengerRepository;

    public TripService(TripRepository tripRepository, PassengerRepository passengerRepository) {
        this.tripRepository = tripRepository;
        this.passengerRepository =passengerRepository;
    }

    public TripDTO createTrip(TripDTO tripDTO, Long passenger_id){
        try {
            Optional<Passenger> p1 = passengerRepository.findById(passenger_id);
            if(p1.isPresent()){
                Trip newTrip = new Trip(p1.get(), null,null,tripDTO.getPosicionOrigen(), 
                tripDTO.getPosicionDestino(), 0, Trip.EstadoViaje.SOLICITADO);

                tripDTO.setPrecio(calcularPrecioTrip(tripDTO.getPosicionOrigen(), tripDTO.getPosicionDestino()));

                Trip savedTrip = tripRepository.save(newTrip);
                tripDTO.setId(savedTrip.getId());
                tripDTO.setCliente(p1.get());
                tripDTO.setEstado(savedTrip.getEstado());
                return tripDTO;
            }else{
                return null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }
    public double calcularPrecioTrip(Posicion ori, Posicion des){
        double y1= ori.getLatitud();
        double x1= ori.getLongitud();
        double y2= des.getLatitud();
        double x2= des.getLongitud();

        final int R = 6371; // Radio de la Tierra en km
 //                                                                                 EUR/Km
        // 1. Convertimos las latitudes y longitudes de grados a radianes
        double lat1Rad = Math.toRadians(x1);
        double lat2Rad = Math.toRadians(x2);
        double deltaLat = Math.toRadians(x2 - x1);
        double deltaLon = Math.toRadians(y2 - y1);

        // 2. Aplicamos la fórmula de Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 3. Resultado final
        double distancia = R * c;

        return distancia * 1.3;
    }
}
