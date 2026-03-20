package deusto.sd.entity;

public class Trip {

    private Long id;
    private Passenger cliente;
    private Driver conductor;
    private Vehicle vehiculo;
    private Posicion origen;
    private Posicion destino;
    private double precio;
    private EstadoViaje estado;

    public enum EstadoViaje {
        SOLICITADO, ACEPTADO, EN_CURSO, FINALIZADO, CANCELADO
    }

    // Constructor vacío
    public Trip() {
    }

    // Constructor con todos los parámetros
    public Trip(Long id, Passenger cliente, Driver conductor, Vehicle vehiculo, Posicion origen,
                Posicion destino, double precio, EstadoViaje estado) {
        this.id = id;
        this.cliente = cliente;
        this.conductor = conductor;
        this.vehiculo = vehiculo;
        this.origen = origen;
        this.destino = destino;
        this.precio = precio;
        this.estado = estado;
    }

    // Getters y Setters
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
