package deusto.sd.ubesto.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import deusto.sd.ubesto.entity.Trip;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    // Busca los viajes asociados al ID del cliente (Passenger)
    List<Trip> findByClienteId(Long clienteId);
}