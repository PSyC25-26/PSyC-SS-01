package deusto.sd.ubesto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deusto.sd.ubesto.dto.*;
import deusto.sd.ubesto.service.*;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    
    public TripController(TripService tripService){
        this.tripService= tripService;
    }

    @PostMapping("/createTrip/{passenger_id}")
    public ResponseEntity<TripDTO> createTrip(@RequestBody TripDTO tripDTO,
        @PathVariable("passenger_id") Long passenger_id){
        try {
            TripDTO newTrip=tripService.createTrip(tripDTO,passenger_id);
            if(newTrip!=null){
                return new ResponseEntity<TripDTO>(newTrip, HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
}
