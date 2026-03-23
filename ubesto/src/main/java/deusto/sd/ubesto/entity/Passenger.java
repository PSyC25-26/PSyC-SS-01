package deusto.sd.ubesto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "passengers")

public class Passenger extends User {
    @Column(name = "metodoPago")
    private String metodoPago;
    
    @Column(name = "posicionActual")
    private String posicionActual;

    // Constructor vacío
    public Passenger() {
        super();
    }

    // Constructor con todos los parámetros
    public Passenger(Long id, String nombre, String email, String password, String metodoPago, Posicion posicionActual) {
        super(id, nombre, email, password);
        this.metodoPago = metodoPago;
        this.posicionActual = posicionActual.toString();
    }

    public Passenger(String nombre, String email, String password, String metodoPago, Posicion posicionActual) {
        super(nombre, email, password);
        this.metodoPago = metodoPago;
        this.posicionActual = posicionActual.toString();
    }

    // Getters y Setters
    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Posicion getPosicionActual() {
        String[] posi= posicionActual.split(",");
        return new Posicion(Double.valueOf(posi[0]),Double.valueOf(posi[1]));
    }

    public void setPosicionActual(Posicion posicionActual) {
        this.posicionActual = posicionActual.toString();
    }
}
