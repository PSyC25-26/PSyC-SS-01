package deusto.sd.ubesto.dto;

public class DriverDTO extends UserDTO{
    private String licenciaConducir;
    private double calificacionMedia;
    private Long vehicleActiveId;


    public DriverDTO(Long id, String nombre, String email, String password, String licenciaConducir,
            double calificacionMedia, Long vehicleActiveId) {
        super(id, nombre, email, password);
        this.licenciaConducir = licenciaConducir;
        this.calificacionMedia = calificacionMedia;
        this.vehicleActiveId = vehicleActiveId;
    }

    
    public DriverDTO(String nombre, String email, String password, String licenciaConducir,
        double calificacionMedia,  Long vehicleActiveId) {
    super( nombre, email, password);
    this.licenciaConducir = licenciaConducir;
    this.calificacionMedia = calificacionMedia;
    this.vehicleActiveId = vehicleActiveId;
}

    
    public DriverDTO() {
        super();
    }
    public String getLicenciaConducir() {
        return licenciaConducir;
    }
    public void setLicenciaConducir(String licenciaConducir) {
        this.licenciaConducir = licenciaConducir;
    }
    public double getCalificacionMedia() {
        return calificacionMedia;
    }
    public void setCalificacionMedia(double calificacionMedia) {
        this.calificacionMedia = calificacionMedia;
    }
    
    public Long getVehicleActiveId() {
        return vehicleActiveId;
    }
    public void setVehicleActiveId(Long vehicleActiveId) {
        this.vehicleActiveId = vehicleActiveId;
    }
}
