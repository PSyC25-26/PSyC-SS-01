package deusto.sd.ubesto.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import deusto.sd.ubesto.entity.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
}