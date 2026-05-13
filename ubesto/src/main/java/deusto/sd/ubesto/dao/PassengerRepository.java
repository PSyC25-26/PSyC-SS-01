package deusto.sd.ubesto.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import deusto.sd.ubesto.entity.Passenger;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    // Usado por tu lógica principal
    Passenger findByEmailAndPassword(String email, String password);
    // Usado por tus pruebas en UnitariosTest.java
    Optional<Passenger> findByEmail(String email);
}