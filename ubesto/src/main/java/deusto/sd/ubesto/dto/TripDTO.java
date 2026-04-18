package deusto.sd.ubesto.dto;

import deusto.sd.ubesto.entity.Driver;
import deusto.sd.ubesto.entity.Passenger;
import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Trip.EstadoViaje;
import deusto.sd.ubesto.entity.Vehicle;

public class TripDTO {
    private Long id;
    private Passenger cliente;
    private EstadoViaje estado;
    private Posicion posicionOrigen;
    private Posicion posicionDestino;
    private double precio;

    private Driver conductor;
    private Vehicle vehiculo;

    
    public TripDTO(Long id, Passenger cliente, Driver conductor, Vehicle vehiculo, Posicion posicionOrigen,
            Posicion posicionDestino, double precio, EstadoViaje estado) {
        this.id = id;
        this.cliente = cliente;
        this.conductor = conductor;
        this.vehiculo = vehiculo;
        this.posicionOrigen = posicionOrigen;
        this.posicionDestino = posicionDestino;
        this.precio = precio;
        this.estado = estado;
    }

    public TripDTO( Passenger cliente, Driver conductor, Vehicle vehiculo, Posicion posicionOrigen,
        Posicion posicionDestino, double precio, EstadoViaje estado) {
    this.cliente = cliente;
    this.conductor = conductor;
    this.vehiculo = vehiculo;
    this.posicionOrigen = posicionOrigen;
    this.posicionDestino = posicionDestino;
    this.precio = precio;
    this.estado = estado;
}

    public TripDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Passenger getCliente() {
        return cliente;
    }

    public void setCliente(Passenger cliente) {
        this.cliente = cliente;
    }

    public Driver getConductor() {
        return conductor;
    }

    public void setConductor(Driver conductor) {
        this.conductor = conductor;
    }

    public Vehicle getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehicle vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Posicion getPosicionOrigen() {
        return posicionOrigen;
    }

    public void setPosicionOrigen(Posicion posicionOrigen) {
        this.posicionOrigen = posicionOrigen;
    }

    public Posicion getPosicionDestino() {
        return posicionDestino;
    }

    public void setPosicionDestino(Posicion posicionDestino) {
        this.posicionDestino = posicionDestino;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public EstadoViaje getEstado() {
        return estado;
    }

    public void setEstado(EstadoViaje estado) {
        this.estado = estado;
    }

    
    
}
