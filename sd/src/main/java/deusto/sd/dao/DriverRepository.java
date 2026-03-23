package deusto.sd.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import deusto.sd.entity.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    
}


