package deusto.sd.ubesto.swing;
import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame(String rol, String email) {
        setTitle("Dashboard - " + rol);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel lblBienvenida = new JLabel("Bienvenido, [" + rol + "] " + email);
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(lblBienvenida, BorderLayout.NORTH);

        String verbo="Editar";

        // Si es pasajero, añadimos los botones del boceto
        if (rol.equals("PASAJERO")) {
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            verbo="Solicitar";
            JButton btnEditar = new JButton("Editar Datos");
            JButton btnBuscar = new JButton(verbo + " Viaje");
            JButton btnHistorial = new JButton("Ver historial de viajes");
            
            Color colorBoton = new Color(100, 200, 100);
            btnEditar.setBackground(colorBoton);
            btnBuscar.setBackground(colorBoton);
            btnHistorial.setBackground(colorBoton);
            
            // Hacer los botones un poco más grandes
            Dimension d = new Dimension(150, 80);
            btnEditar.setPreferredSize(d);
            btnBuscar.setPreferredSize(d);
            btnHistorial.setPreferredSize(d);

            panelBotones.add(btnEditar);
            panelBotones.add(btnBuscar);
            panelBotones.add(btnHistorial);
            
            add(panelBotones, BorderLayout.CENTER);
        }
    }
}