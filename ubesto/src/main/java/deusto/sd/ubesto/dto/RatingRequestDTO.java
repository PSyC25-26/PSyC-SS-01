package deusto.sd.ubesto.dto;

public class RatingRequestDTO {
    private Long passengerId;
    private int estrellas;

    public RatingRequestDTO() {
    }

    public RatingRequestDTO(Long passengerId, int estrellas) {
        this.passengerId = passengerId;
        this.estrellas = estrellas;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }
}
