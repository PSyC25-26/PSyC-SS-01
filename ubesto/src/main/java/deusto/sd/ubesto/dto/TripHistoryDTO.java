package deusto.sd.ubesto.dto;

import java.time.LocalDateTime;

import deusto.sd.ubesto.entity.Posicion;
import deusto.sd.ubesto.entity.Trip;
import deusto.sd.ubesto.entity.Trip.EstadoViaje;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

public class TripHistoryDTO {
    private Long id;
    private Long passengerId;
    private String passengerNombre;
    private Long driverId;
    private String driverNombre;
    private Long vehicleId;
    private String vehicleMatricula;
    private CategoriaVehiculo categoria;
    private Posicion posicionOrigen;
    private Posicion posicionDestino;
    private double precio;
    private EstadoViaje estado;
    private Integer rating;
    private String cancelReason;
    private String cancelledBy;
    private LocalDateTime cancelledAt;

    public TripHistoryDTO() {
    }

    public TripHistoryDTO(Trip trip) {
        this.id = trip.getId();
        this.posicionOrigen = trip.getPosicionOrigen();
        this.posicionDestino = trip.getPosicionDestino();
        this.precio = trip.getPrecio();
        this.estado = trip.getEstado();
        this.rating = trip.getRating();
        this.cancelReason = trip.getCancelReason();
        this.cancelledBy = trip.getCancelledBy();
        this.cancelledAt = trip.getCancelledAt();

        if (trip.getCliente() != null) {
            this.passengerId = trip.getCliente().getId();
            this.passengerNombre = trip.getCliente().getNombre();
        }
        if (trip.getConductor() != null) {
            this.driverId = trip.getConductor().getId();
            this.driverNombre = trip.getConductor().getNombre();
        }
        if (trip.getVehiculo() != null) {
            this.vehicleId = trip.getVehiculo().getId();
            this.vehicleMatricula = trip.getVehiculo().getMatricula();
            this.categoria = trip.getVehiculo().getCategoria();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    public String getPassengerNombre() { return passengerNombre; }
    public void setPassengerNombre(String passengerNombre) { this.passengerNombre = passengerNombre; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverNombre() { return driverNombre; }
    public void setDriverNombre(String driverNombre) { this.driverNombre = driverNombre; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleMatricula() { return vehicleMatricula; }
    public void setVehicleMatricula(String vehicleMatricula) { this.vehicleMatricula = vehicleMatricula; }

    public CategoriaVehiculo getCategoria() { return categoria; }
    public void setCategoria(CategoriaVehiculo categoria) { this.categoria = categoria; }

    public Posicion getPosicionOrigen() { return posicionOrigen; }
    public void setPosicionOrigen(Posicion posicionOrigen) { this.posicionOrigen = posicionOrigen; }

    public Posicion getPosicionDestino() { return posicionDestino; }
    public void setPosicionDestino(Posicion posicionDestino) { this.posicionDestino = posicionDestino; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public EstadoViaje getEstado() { return estado; }
    public void setEstado(EstadoViaje estado) { this.estado = estado; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
