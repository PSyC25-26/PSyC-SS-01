package deusto.sd.ubesto.dto;

public class WalletDTO {
    private Long userId;
    private String rol;
    private double saldo;
    private double gananciasTotales;
    private long viajesFinalizados;
    private long viajesCancelados;
    private long valoracionesRecibidas;
    private double calificacionMedia;

    public WalletDTO() {
    }

    public WalletDTO(Long userId, String rol, double saldo, double gananciasTotales, long viajesFinalizados,
                     long viajesCancelados, long valoracionesRecibidas, double calificacionMedia) {
        this.userId = userId;
        this.rol = rol;
        this.saldo = saldo;
        this.gananciasTotales = gananciasTotales;
        this.viajesFinalizados = viajesFinalizados;
        this.viajesCancelados = viajesCancelados;
        this.valoracionesRecibidas = valoracionesRecibidas;
        this.calificacionMedia = calificacionMedia;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public double getGananciasTotales() { return gananciasTotales; }
    public void setGananciasTotales(double gananciasTotales) { this.gananciasTotales = gananciasTotales; }

    public long getViajesFinalizados() { return viajesFinalizados; }
    public void setViajesFinalizados(long viajesFinalizados) { this.viajesFinalizados = viajesFinalizados; }

    public long getViajesCancelados() { return viajesCancelados; }
    public void setViajesCancelados(long viajesCancelados) { this.viajesCancelados = viajesCancelados; }

    public long getValoracionesRecibidas() { return valoracionesRecibidas; }
    public void setValoracionesRecibidas(long valoracionesRecibidas) { this.valoracionesRecibidas = valoracionesRecibidas; }

    public double getCalificacionMedia() { return calificacionMedia; }
    public void setCalificacionMedia(double calificacionMedia) { this.calificacionMedia = calificacionMedia; }
}
