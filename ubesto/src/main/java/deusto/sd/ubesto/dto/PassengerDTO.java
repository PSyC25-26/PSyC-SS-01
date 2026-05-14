package deusto.sd.ubesto.dto;

public class PassengerDTO {
    
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private String metodoPago;
    private double latitud;
    private double longitud;
    private double monedero;

    public PassengerDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public double getMonedero() { return monedero; }
    public void setMonedero(double monedero) { this.monedero = monedero; }
}