package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.TripRequestDTO;
import deusto.sd.ubesto.entity.Trip;
import deusto.sd.ubesto.service.TripService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    
    public TripController(TripService tripService){
        this.tripService = tripService;
    }

    /**
     * Endpoint para que un pasajero solicite un nuevo viaje.
     */
    @PostMapping("/request")
    public ResponseEntity<?> requestTrip(@RequestBody TripRequestDTO tripRequestDTO){ // CAMBIO: Usamos el DTO de solicitud
        try {
            // CAMBIO: Llamamos al nuevo método del servicio
            Trip newTrip = tripService.requestTrip(tripRequestDTO);
            return new ResponseEntity<>(newTrip, HttpStatus.CREATED);
            
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404 si el pasajero no existe
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno al solicitar el viaje.", HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    
    /**
     * Endpoint para que un conductor acepte un viaje.
     * Esto iniciará el hilo de simulación en el backend.
     */
    @PostMapping("/{tripId}/accept/{driverId}")
    public ResponseEntity<?> acceptTrip(@PathVariable Long tripId, @PathVariable Long driverId) {
        try {
            Trip acceptedTrip = tripService.acceptTrip(tripId, driverId);
            System.out.println("CONTROLLER: Viaje " + tripId + " aceptado por conductor " + driverId + ". Simulación iniciada.");
            return ResponseEntity.ok(acceptedTrip); // Devuelve 200 OK

        } catch (EntityNotFoundException e) {
            // Si el viaje o el conductor no existen
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            // Si el viaje ya fue aceptado o está en otro estado incorrecto
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno al aceptar el viaje.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}