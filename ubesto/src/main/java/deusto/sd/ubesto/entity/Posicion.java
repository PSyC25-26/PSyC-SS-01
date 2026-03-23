package deusto.sd.ubesto.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Posicion {

    private double latitud;
    private double longitud;

    // Constructor vacío
    public Posicion() {
    }

    // Constructor con parámetros
    public Posicion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters y Setters
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return latitud +
               "," + longitud;
    }
}