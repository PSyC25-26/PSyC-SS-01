package deusto.sd.ubesto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "passengers")

public class Passenger extends User {
    @Column(name = "metodoPago")
    private String metodoPago;
    
    @Embedded
    @Column(name = "posicion_actual")
    private Posicion posicionActual;

    // Constructor vacío
    public Passenger() {
        super();
    }

    // Constructor con todos los parámetros
    public Passenger(Long id, String nombre, String email, String password, String metodoPago, 
        Posicion posicionActual) {
        super(id, nombre, email, password);
        this.metodoPago = metodoPago;
        this.posicionActual = posicionActual;
    }

    public Passenger(String nombre, String email, String password, String metodoPago,
        Posicion posicionActual) {
        super(nombre, email, password);
        this.metodoPago = metodoPago;
        this.posicionActual = posicionActual;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public Posicion getPosicionActual() {
        return posicionActual;
    }

    public void setPosicionActual(Posicion posicionActual) {
        this.posicionActual = posicionActual;
    }

    

    

}
