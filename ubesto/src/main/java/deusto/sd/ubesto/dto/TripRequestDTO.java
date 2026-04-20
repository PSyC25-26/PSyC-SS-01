package deusto.sd.ubesto.dto;

import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

// Este DTO es solamente para recibir los datos de una nueva solicitud de viaje
public class TripRequestDTO {
    private Long passengerId;
    private Posicion origen;
    private Posicion destino;
    private CategoriaVehiculo categoria;


    public TripRequestDTO() {
    }

    public TripRequestDTO(Long passengerId, Posicion origen, Posicion destino, CategoriaVehiculo categoria) {
        this.passengerId = passengerId;
        this.origen = origen;
        this.destino = destino;
        this.categoria = categoria;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Posicion getOrigen() {
        return origen;
    }

    public void setOrigen(Posicion origen) {
        this.origen = origen;
    }

    public Posicion getDestino() {
        return destino;
    }

    public void setDestino(Posicion destino) {
        this.destino = destino;
    }

    public CategoriaVehiculo getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaVehiculo categoria) {
        this.categoria = categoria;
    }
}