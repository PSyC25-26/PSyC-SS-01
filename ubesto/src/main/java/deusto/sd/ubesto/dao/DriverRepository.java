package deusto.sd.ubesto.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import deusto.sd.ubesto.entity.Driver;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    // Método necesario para DriverService y UnitariosTest
    Optional<Driver> findByEmail(String email);
}