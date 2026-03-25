import javax.swing.*;
import java.awt.*;

// EL DASHBOARD (TU TAREA FINAL DEL SPRINT 1)
class VentanaDashboard extends JFrame {
    public VentanaDashboard(String usuario) {
        setTitle("Dashboard");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JLabel lblBienvenida = new JLabel("Bienvenido, " + usuario, SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblBienvenida);
    }
}