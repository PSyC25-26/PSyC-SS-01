package deusto.sd.ubesto.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "trips")

public class Trip {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger cliente;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver conductor;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehiculo;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "latitud", column = @Column(name = "latOrigen")),
        @AttributeOverride(name = "longitud", column = @Column(name = "lonOrigen"))
    })
    private Posicion posicionOrigen;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "latitud", column = @Column(name = "latDestino")),
        @AttributeOverride(name = "longitud", column = @Column(name = "lonDestino"))
    })
    private Posicion posicionDestino;

    @Column(name="precio")
    private double precio;
    
    @Column(name="estado")
    private EstadoViaje estado;

    public enum EstadoViaje {
        SOLICITADO, ACEPTADO, EN_CURSO, FINALIZADO, CANCELADO
    }

    // Constructor vacío
    public Trip() {
    }

    // Constructor con todos los parámetros

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public Trip(Long id, Passenger cliente, Driver conductor, Vehicle vehiculo, double latOrigen, Posicion posicionOrigen, Posicion posicionDestino,
        double precio, EstadoViaje estado) {
        this.id = id;
        this.cliente = cliente;
        this.conductor = conductor;
        this.vehiculo = vehiculo;
        this.posicionOrigen = posicionOrigen;
        this.posicionDestino = posicionDestino;
        this.precio = precio;
        this.estado = estado;
    }

    public Trip( Passenger cliente, Driver conductor, Vehicle vehiculo, double latOrigen, Posicion posicionOrigen, Posicion posicionDestino,
        double precio, EstadoViaje estado) {
    this.cliente = cliente;
    this.conductor = conductor;
    this.vehiculo = vehiculo;
    this.posicionOrigen = posicionOrigen;
    this.posicionDestino = posicionDestino;
    this.precio = precio;
    this.estado = estado;
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
