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
            driverDTO.getCalificacionMedia(),driverDTO.isDisponible(),driverDTO.getVehicleActiveId());
            
            Driver savedDrived = driverRepository.save(newDriver);
    
            driverDTO.setId(savedDrived.getId());
    
            return driverDTO;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean loginDriver(LoginDTO loginDTO){
        try {
            boolean correcto = verificarPassword(loginDTO);
            if(correcto){
                UUID token = UUID.randomUUID();
                Driver driver=driverRepository.findByEmail(loginDTO.getEmail()).get();
                LoggedUser loggedUser = new LoggedUser("DRIVER", driver.getId(), token.toString());

                loggedUserRepository.save(loggedUser);
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
}
