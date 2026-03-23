package deusto.sd.service;

import org.springframework.stereotype.Service;

import deusto.sd.dao.DriverRepository;
import deusto.sd.dto.DriverDTO;
import deusto.sd.entity.Driver;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

        public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }


    public DriverDTO registerDriver(DriverDTO driverDTO){

        if(driverDTO.getId()!=null){
            driverDTO.setId(null);
        }

        Driver newDriver =  new Driver(driverDTO.getNombre(), driverDTO.getEmail(),driverDTO.getPassword(),driverDTO.getLicenciaConducir(),
        driverDTO.getCalificacionMedia(),driverDTO.isDisponible(),driverDTO.getVehicleActiveId());
        
        Driver savedDrived = driverRepository.save(newDriver);

        driverDTO.setId(savedDrived.getId());

        return driverDTO;
    }
}
