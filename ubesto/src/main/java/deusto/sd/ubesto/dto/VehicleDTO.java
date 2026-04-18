package deusto.sd.ubesto.dto;

import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

public class VehicleDTO {
    private Long id;
    private String matricula;
    private String marca;
    private String modelo;
    private String color;
    private CategoriaVehiculo categoria;
    private DriverDTO driver;

    public VehicleDTO(Long id, String matricula, String marca, String modelo, String color, CategoriaVehiculo categoria,
            DriverDTO driver) {
        this.id = id;
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.categoria = categoria;
        this.driver = driver;
    } 
    public VehicleDTO() {

    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public CategoriaVehiculo getCategoria() {
        return categoria;
    }
    public void setCategoria(CategoriaVehiculo categoria) {
        this.categoria = categoria;
    }
    public DriverDTO getDriver() {
        return driver;
    }
    public void setDriver(DriverDTO driver) {
        this.driver = driver;
    } 

    
    
}
