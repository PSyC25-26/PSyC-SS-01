package deusto.sd.ubesto.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import deusto.sd.ubesto.entity.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    // Para el login
    Passenger findByEmailAndPassword(String email, String password);
    // Para tus pruebas unitarias (UnitariosTest.java)
    Passenger findByEmail(String email);
}