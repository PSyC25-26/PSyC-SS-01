package deusto.sd.entity;

public class Passenger extends User {

    private String metodoPago;
    private Posicion posicionActual;

    // Constructor vacío
    public Passenger() {
        super();
    }

    // Constructor con todos los parámetros
    public Passenger(Long id, String nombre, String email, String password, String metodoPago, Posicion posicionActual) {
        super(id, nombre, email, password);
        this.metodoPago = metodoPago;
        this.posicionActual = posicionActual;
    }

    // Getters y Setters
    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Posicion getPosicionActual() {
        return posicionActual;
    }

    public void setPosicionActual(Posicion posicionActual) {
        this.posicionActual = posicionActual;
    }
}
