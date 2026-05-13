package deusto.sd.ubesto.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import deusto.sd.ubesto.entity.Passenger;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    // Usado por PassengerService.loginPassenger
    Passenger findByEmailAndPassword(String email, String password);

    // Modificado para devolver Optional<Passenger> según lo requieren tus tests
    Optional<Passenger> findByEmail(String email);
}