package deusto.sd.ubesto.dto;

import deusto.sd.ubesto.entity.Posicion;

public class PassengerDTO extends UserDTO{
    private String metodoPago;
    private Posicion posicionActual;
    
    public PassengerDTO(Long id, String nombre, String email, String password, String metodoPago, 
        Posicion posicionActual) {
        super(id, nombre, email, password);
        this.metodoPago = metodoPago;
        this.posicionActual = posicionActual;
    }

    public PassengerDTO(String nombre, String email, String password, String metodoPago,
        Posicion posicionActual) {
        super(nombre, email, password);
        this.metodoPago = metodoPago;
        this.posicionActual = posicionActual;
    }

    public PassengerDTO() {
    }

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
