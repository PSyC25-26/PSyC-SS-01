package deusto.sd.ubesto.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.RatingRequestDTO;
import deusto.sd.ubesto.dto.TripHistoryDTO;
import deusto.sd.ubesto.dto.TripRequestDTO;
import deusto.sd.ubesto.entity.Trip;
import deusto.sd.ubesto.service.PassengerService;
import deusto.sd.ubesto.service.TripService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final PassengerService passengerService;

    public TripController(TripService tripService, PassengerService passengerService) {
        this.tripService = tripService;
        this.passengerService = passengerService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestTrip(@RequestBody TripRequestDTO tripRequestDTO) {
        try {
            Trip newTrip = tripService.requestTrip(tripRequestDTO);
            return new ResponseEntity<>(newTrip, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno al solicitar el viaje.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{tripId}/accept/{driverId}")
    public ResponseEntity<?> acceptTrip(@PathVariable Long tripId, @PathVariable Long driverId) {
        try {
            Trip acceptedTrip = tripService.acceptTrip(tripId, driverId);
            System.out.println("CONTROLLER: Viaje " + tripId + " aceptado por conductor " + driverId + ". Simulación iniciada.");
            return ResponseEntity.ok(acceptedTrip);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno al aceptar el viaje.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{tripId}/cancel/passenger/{passengerId}")
    public ResponseEntity<?> cancelTripByPassenger(@PathVariable Long tripId, @PathVariable Long passengerId,
                                                   @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(tripService.cancelTripByPassenger(tripId, passengerId, reason));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{tripId}/cancel/driver/{driverId}")
    public ResponseEntity<?> cancelTripByDriver(@PathVariable Long tripId, @PathVariable Long driverId,
                                                @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(tripService.cancelTripByDriver(tripId, driverId, reason));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{tripId}/rate")
    public ResponseEntity<?> rateTrip(@PathVariable Long tripId, @RequestBody RatingRequestDTO ratingRequestDTO) {
        Long passengerId = ratingRequestDTO != null ? ratingRequestDTO.getPassengerId() : null;
        int estrellas = ratingRequestDTO != null ? ratingRequestDTO.getEstrellas() : 0;
        boolean success = passengerService.rateTrip(tripId, passengerId, estrellas);
        if (success) {
            return ResponseEntity.ok("Viaje valorado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al valorar el viaje.");
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTrip(@PathVariable Long tripId) {
        try {
            return ResponseEntity.ok(tripService.getTripById(tripId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/getAllTrips")
    public ResponseEntity<?> getAllTrips() {
        List<Trip> allTrips = tripService.getAllTrips();
        return ResponseEntity.ok(tripService.fromTripToString(allTrips));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<TripHistoryDTO>> getPassengerHistory(@PathVariable Long passengerId) {
        return ResponseEntity.ok(tripService.getPassengerHistory(passengerId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<TripHistoryDTO>> getDriverHistory(@PathVariable Long driverId) {
        return ResponseEntity.ok(tripService.getDriverHistory(driverId));
    }
}
