package deusto.sd.ubesto.dto;
import deusto.sd.ubesto.entity.Posicion;

public class DriverDTO extends UserDTO{
    private String licenciaConducir;
    private double calificacionMedia;
    private Long vehicleActiveId;
    protected Posicion posicionActual;


    public DriverDTO(Long id, String nombre, String email, String password, String licenciaConducir,
            double calificacionMedia, Long vehicleActiveId, Posicion posicionActual) {
        super(id, nombre, email, password);
        this.licenciaConducir = licenciaConducir;
        this.calificacionMedia = calificacionMedia;
        this.vehicleActiveId = vehicleActiveId;
        this.posicionActual = posicionActual;
    }

    
    public DriverDTO(String nombre, String email, String password, String licenciaConducir,
        double calificacionMedia,  Long vehicleActiveId, Posicion posicionActual) {
    super( nombre, email, password);
    this.licenciaConducir = licenciaConducir;
    this.calificacionMedia = calificacionMedia;
    this.vehicleActiveId = vehicleActiveId;
    this.posicionActual = posicionActual;
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
    public Posicion getPosicionActual() {
        return posicionActual;
    }
    public void setPosicionActual(Posicion posicionActual) {
        this.posicionActual = posicionActual;
    }
    
}
