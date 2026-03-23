package deusto.sd.ubesto.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import deusto.sd.ubesto.entity.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
}
