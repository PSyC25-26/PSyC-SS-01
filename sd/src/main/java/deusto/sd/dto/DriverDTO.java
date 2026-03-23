package deusto.sd.dto;


public class DriverDTO extends UserDTO{
    private String licenciaConducir;
    private double calificacionMedia;
    private boolean disponible;
    private Long vehicleActiveId;


    public DriverDTO(Long id, String nombre, String email, String password, String licenciaConducir,
            double calificacionMedia, boolean disponible, Long vehicleActiveId) {
        super(id, nombre, email, password);
        this.licenciaConducir = licenciaConducir;
        this.calificacionMedia = calificacionMedia;
        this.disponible = disponible;
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
    public boolean isDisponible() {
        return disponible;
    }
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    public Long getVehicleActiveId() {
        return vehicleActiveId;
    }
    public void setVehicleActiveId(Long vehicleActiveId) {
        this.vehicleActiveId = vehicleActiveId;
    }

    

    
}
