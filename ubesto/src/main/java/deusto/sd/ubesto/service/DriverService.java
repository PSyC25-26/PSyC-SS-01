package deusto.sd.ubesto.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.*;
import deusto.sd.ubesto.dto.DriverDTO;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.entity.*;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final LoggedUserRepository loggedUserRepository;

    public DriverService(DriverRepository driverRepository, LoggedUserRepository loggedUserRepository) {
        this.driverRepository = driverRepository;
        this.loggedUserRepository = loggedUserRepository;
    }

    public DriverDTO registerDriver(DriverDTO driverDTO){
        try {
            if(driverDTO.getId()!=null){
                driverDTO.setId(null);
            }
            if(driverDTO.getVehicleActiveId()!=null){
                driverDTO.setVehicleActiveId(null);
            }
    
            Driver newDriver =  new Driver(driverDTO.getNombre(), driverDTO.getEmail(),driverDTO.getPassword(),driverDTO.getLicenciaConducir(),
            driverDTO.getCalificacionMedia(),driverDTO.getVehicleActiveId(), driverDTO.getPosicionActual());
            
            Driver savedDrived = driverRepository.save(newDriver);
    
            driverDTO.setId(savedDrived.getId());
    
            return driverDTO;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long loginDriver(LoginDTO loginDTO){
        try {
            boolean correcto = verificarPassword(loginDTO);
            if(correcto){
                Driver driver = driverRepository.findByEmail(loginDTO.getEmail()).get();
                UUID token = UUID.randomUUID();
                LoggedUser loggedUser = new LoggedUser("DRIVER", driver.getId(), token.toString());
                loggedUserRepository.save(loggedUser);
                
                return driver.getId(); // Devolvemos el ID
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verificarPassword(LoginDTO loginDTO){
        String real_pw=driverRepository.findByEmail(loginDTO.getEmail()).get().getPassword();
        if(loginDTO.getPassword().equals(real_pw)){
            return true;
        }else{
            return false;
        }
    }

    public DriverDTO updateDriver(Long id, DriverDTO driverDTO) {
        try {
            Driver driver = driverRepository.findById(id).orElse(null);
            if (driver == null) return null;

            if (driverDTO.getNombre() != null && !driverDTO.getNombre().isBlank())
                driver.setNombre(driverDTO.getNombre());

            if (driverDTO.getPassword() != null && !driverDTO.getPassword().isBlank())
                driver.setPassword(driverDTO.getPassword());

            if (driverDTO.getLicenciaConducir() != null && !driverDTO.getLicenciaConducir().isBlank())
                driver.setLicenciaConducir(driverDTO.getLicenciaConducir());

            driverRepository.save(driver);
            return driverDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
