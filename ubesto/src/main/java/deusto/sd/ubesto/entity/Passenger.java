package deusto.sd.ubesto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "passengers")

public class Passenger extends User {
    @Column(name = "metodoPago")
    private String metodoPago;

    // Constructor vacío
    public Passenger() {
        super();
    }

    // Constructor con todos los parámetros
    public Passenger(Long id, String nombre, String email, String password, Posicion posicionActual, String metodoPago) {
        super(id, nombre, email, password, posicionActual);
        this.metodoPago = metodoPago;
    }

    public Passenger(String nombre, String email, String password, Posicion posicionActual, String metodoPago) {
        super(nombre, email, password, posicionActual);
        this.metodoPago = metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }    

}
