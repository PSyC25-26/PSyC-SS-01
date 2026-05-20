package deusto.sd.ubesto.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import deusto.sd.ubesto.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByMatricula(String matricula);
    Optional<Vehicle> findByDriver_IdAndMatricula(Long driverId, String matricula);
    List<Vehicle> findByDriver_Id(Long driverId);
}
