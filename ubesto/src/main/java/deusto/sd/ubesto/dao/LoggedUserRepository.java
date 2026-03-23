package deusto.sd.ubesto.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import deusto.sd.ubesto.entity.LoggedUser;

@Repository
public interface LoggedUserRepository extends JpaRepository<LoggedUser, Long> {
    
}
