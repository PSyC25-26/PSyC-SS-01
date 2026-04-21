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

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        Color colorBoton = new Color(100, 200, 100);
        Dimension d = new Dimension(150, 180);

        if (rol.equals("PASAJERO")) {
            JButton btnEditar = new JButton("Editar Datos");
            JButton btnBuscar = new JButton("Solicitar Viaje");
            JButton btnHistorial = new JButton("Ver historial");
            
            btnEditar.setBackground(colorBoton); btnBuscar.setBackground(colorBoton); btnHistorial.setBackground(colorBoton);
            btnEditar.setPreferredSize(d); btnBuscar.setPreferredSize(d); btnHistorial.setPreferredSize(d);

            // ACCIÓN: Solicitar Viaje
            btnBuscar.addActionListener(e -> {
                new VentanaSolicitarViaje(email).setVisible(true);
                dispose(); // Cierra el dashboard
            });

            panelBotones.add(btnEditar); panelBotones.add(btnBuscar); panelBotones.add(btnHistorial);

        } else if (rol.equals("CONDUCTOR")) {
            JButton btnEditar = new JButton("Editar Datos");
            JButton btnVehiculo = new JButton("Añadir Vehículo");
            JButton btnViaje = new JButton("Realizar Viaje");
            
            btnEditar.setBackground(colorBoton); btnVehiculo.setBackground(colorBoton); btnViaje.setBackground(colorBoton);
            btnEditar.setPreferredSize(d); btnVehiculo.setPreferredSize(d); btnViaje.setPreferredSize(d);

            // ACCIÓN: Añadir Vehículo
            btnVehiculo.addActionListener(e -> {
                new VentanaAñadirVehiculo(email).setVisible(true);
                dispose(); // Cierra el dashboard
            });

            panelBotones.add(btnEditar); panelBotones.add(btnVehiculo); panelBotones.add(btnViaje);
        }
        
        add(panelBotones, BorderLayout.CENTER);
    }
}