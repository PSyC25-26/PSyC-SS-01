package deusto.sd.ubesto.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import deusto.sd.ubesto.entity.Trip;
import deusto.sd.ubesto.entity.Trip.EstadoViaje;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByClienteId(Long clienteId);
    List<Trip> findByConductorId(Long conductorId);
    List<Trip> findByClienteIdOrderByIdDesc(Long clienteId);
    List<Trip> findByConductorIdOrderByIdDesc(Long conductorId);
    List<Trip> findByClienteIdAndEstadoIn(Long clienteId, Collection<EstadoViaje> estados);
    List<Trip> findByConductorIdAndEstadoIn(Long conductorId, Collection<EstadoViaje> estados);
}
