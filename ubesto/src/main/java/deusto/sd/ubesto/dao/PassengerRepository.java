package deusto.sd.ubesto.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import deusto.sd.ubesto.entity.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    // Método para buscar un pasajero por sus credenciales (Login)
    Passenger findByEmailAndPassword(String email, String password);

    // Método para buscar un pasajero solo por su email (necesario para UnitariosTest)
    Passenger findByEmail(String email);
}