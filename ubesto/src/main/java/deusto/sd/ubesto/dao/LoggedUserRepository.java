package deusto.sd.ubesto.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import deusto.sd.ubesto.entity.LoggedUser;
import jakarta.transaction.Transactional;

@Repository
public interface LoggedUserRepository extends JpaRepository<LoggedUser, Long> {
    Optional<LoggedUser> findByUserid(Long id);

    @Transactional
    void deleteByUserid(Long userId);
}
