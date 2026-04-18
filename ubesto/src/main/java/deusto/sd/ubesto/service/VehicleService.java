package deusto.sd.ubesto.service;


import java.util.Optional;

import org.springframework.stereotype.Service;

import deusto.sd.ubesto.dao.DriverRepository;
import deusto.sd.ubesto.dao.VehicleRepository;
import deusto.sd.ubesto.dto.DriverDTO;
import deusto.sd.ubesto.dto.VehicleDTO;
import deusto.sd.ubesto.entity.Vehicle;
import deusto.sd.ubesto.entity.Driver;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public VehicleService(VehicleRepository vehicleRepository, DriverRepository driverRepository){
        this.vehicleRepository=vehicleRepository;
        this.driverRepository=driverRepository;
    }

    public VehicleDTO createVehicle (VehicleDTO vehicleDTO, Long driver_id){
        try {
            Optional<Driver> d1 = driverRepository.findById(driver_id);
            if(d1.isPresent()){

                Vehicle newVehicle= new Vehicle(vehicleDTO.getMatricula(), vehicleDTO.getMarca(), 
                vehicleDTO.getModelo(), vehicleDTO.getColor(), vehicleDTO.getCategoria(), d1.get());

                Vehicle savedVehicle= vehicleRepository.save(newVehicle);
                vehicleDTO.setId(savedVehicle.getId());
            
                Driver d=d1.get();
                DriverDTO drDTO= new DriverDTO(d.getId(),d.getNombre(),d.getEmail(),
                d.getPassword(),d.getLicenciaConducir(),d.getCalificacionMedia(),
                null);

                vehicleDTO.setDriver(drDTO);
                return vehicleDTO;
            }else{
                return null;
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
       
    }
}
