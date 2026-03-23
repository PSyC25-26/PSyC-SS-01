package deusto.sd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "drivers")

public class Driver extends User {
    @Column(name = "licenciaConducir")
    private String licenciaConducir;
    
    @Column(name = "calificacionMedia")
    private double calificacionMedia;
    
    @Column(name = "disponible")
    private boolean disponible;
    
    @Column(name = "vehicleActiveId")
    private Long vehicleActiveId;

    // Constructor vacío
    public Driver() {
        super(); // Constructor de la clase padre
    }

    // Constructor con todos los parámetros
    public Driver(Long id, String nombre, String email, String password, String licenciaConducir,
                  double calificacionMedia, boolean disponible, Long vehicleActiveId) {
        super(id, nombre, email, password); // Inicializa los atributos del padre
        this.licenciaConducir = licenciaConducir;
        this.calificacionMedia = calificacionMedia;
        this.disponible = disponible;
        this.vehicleActiveId = vehicleActiveId;
    }

    public Driver( String nombre, String email, String password, String licenciaConducir,
        double calificacionMedia, boolean disponible, Long vehicleActiveId) {
super(nombre, email, password); // Inicializa los atributos del padre
this.licenciaConducir = licenciaConducir;
this.calificacionMedia = calificacionMedia;
this.disponible = disponible;
this.vehicleActiveId = vehicleActiveId;
}

    // Getters y Setters
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