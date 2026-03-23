package deusto.sd.ubesto.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import deusto.sd.ubesto.entity.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long>  {
     Optional<Passenger> findByEmail(String passengerEmail);
}
