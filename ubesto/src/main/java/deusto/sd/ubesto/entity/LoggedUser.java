package deusto.sd.ubesto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "logged_users")
public class LoggedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="type")
    private String user_type;
    @Column(name="user_id")
    private Long user_id;
    @Column(name="token")
    private String token;
    
    public LoggedUser(Long id, String user_type, Long user_id, String token) {
        this.id = id;
        this.user_type = user_type;
        this.user_id = user_id;
        this.token = token;
    }

    public LoggedUser( String user_type, Long user_id, String token) {
        this.user_type = user_type;
        this.user_id = user_id;
        this.token = token;
    }

    public LoggedUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    
    
}
