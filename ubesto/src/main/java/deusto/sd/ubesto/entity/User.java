package deusto.sd.ubesto.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "monedero")
    private double monedero = 100.0;

    @Embedded
    private Posicion posicionActual;

    @Column(name = "cuenta_eliminada", nullable = false)
    private boolean cuentaEliminada = false;

    @Column(name = "fecha_eliminacion")
    private LocalDateTime fechaEliminacion;

    public User() {
    }

    public User(Long id, String nombre, String email, String password, Posicion posicionActual) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.posicionActual = posicionActual;
    }

    public User(String nombre, String email, String password, Posicion posicionActual) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.posicionActual = posicionActual;
    }

    public User(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getMonedero() {
        return monedero;
    }

    public void setMonedero(double monedero) {
        this.monedero = monedero;
    }

    public Posicion getPosicionActual() {
        return posicionActual;
    }

    public void setPosicionActual(Posicion posicionActual) {
        this.posicionActual = posicionActual;
    }

    public boolean isCuentaEliminada() {
        return cuentaEliminada;
    }

    public void setCuentaEliminada(boolean cuentaEliminada) {
        this.cuentaEliminada = cuentaEliminada;
    }

    public LocalDateTime getFechaEliminacion() {
        return fechaEliminacion;
    }

    public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
        this.fechaEliminacion = fechaEliminacion;
    }

    public void marcarCuentaEliminada(String emailAnonimizado) {
        this.cuentaEliminada = true;
        this.fechaEliminacion = LocalDateTime.now();
        this.email = emailAnonimizado;
        this.password = "";
    }
}
