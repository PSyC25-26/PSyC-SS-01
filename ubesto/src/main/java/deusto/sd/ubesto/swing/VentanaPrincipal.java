import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    public VentanaPrincipal() {
        setTitle("Uber App - Inicio");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton btnPasajero = new JButton("Soy Pasajero");
        JButton btnConductor = new JButton("Soy Conductor");
        JButton btnLogin = new JButton("Ya tengo cuenta (Login)");

        btnPasajero.addActionListener(e -> new VentanaRegistro("PASAJERO").setVisible(true));
        btnConductor.addActionListener(e -> new VentanaRegistro("CONDUCTOR").setVisible(true));
        btnLogin.addActionListener(e -> new VentanaLogin().setVisible(true));

        add(btnPasajero);
        add(btnConductor);
        add(btnLogin);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}