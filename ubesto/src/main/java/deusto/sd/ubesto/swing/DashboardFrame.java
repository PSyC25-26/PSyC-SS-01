package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;

public class DashboardFrame extends JFrame {
    final Dimension d = new Dimension(150, 180);
    final Color verdeFondo = new Color(224, 250, 228);
    final LineBorder bordeBerde = new LineBorder(new Color(47,158,68),2,true);
    final Color colorBoton =new Color(79,201,95); // Color verde estilo boceto: Color(100, 200, 100)
    final Font fontBotones = new Font("SansSerif", Font.BOLD, 12);

    // CORRECCIÓN 1: Añadimos Long idUsuario al constructor
    public DashboardFrame(String rol, String email, Long idUsuario) {
        setTitle("Dashboard - " + rol);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // CORRECCIÓN 2: Mostramos el ID en el mensaje de bienvenida para comprobar que llega bien
        JLabel lblBienvenida = new JLabel("Bienvenido, [" + rol + "] " + email + " (ID: " + idUsuario + ")");
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(lblBienvenida, BorderLayout.NORTH);

        String verbo="";

        // Si es pasajero, añadimos los botones del boceto
        if (rol.equals("PASAJERO")) {
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            verbo="Solicitar";
            JButton btnEditar = new JButton("Editar Datos");
            JButton btnBuscar = new JButton(verbo + " Viaje");
            JButton btnHistorial = new JButton("Ver historial de viajes");
            
            btnEditar.setBackground(colorBoton);
            btnBuscar.setBackground(colorBoton);
            btnHistorial.setBackground(colorBoton);
            
            btnEditar.setPreferredSize(d);
            btnBuscar.setPreferredSize(d);
            btnHistorial.setPreferredSize(d);

            btnEditar.addActionListener(e -> {
                new VentanaEditarPasajero(email, idUsuario).setVisible(true);
                dispose();
            });

            btnBuscar.addActionListener(e -> {
                new VentanaSolicitarViaje(email, idUsuario).setVisible(true);
                dispose(); 
            });

            panelBotones.add(btnEditar);
            panelBotones.add(btnBuscar);
            panelBotones.add(btnHistorial);

            add(panelBotones, BorderLayout.CENTER);
            
        } else if (rol.equals("CONDUCTOR")) {
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            JButton btnEditar = new JButton("Editar Datos");
            btnEditar.setBackground(colorBoton);
            btnEditar.setPreferredSize(d);
            btnEditar.addActionListener(e -> {
                new VentanaEditarConductor(email, idUsuario).setVisible(true);
                dispose();
            });
            JButton btnVehiculo = new JButton("Añadir Vehículo");
            JButton btnViaje = new JButton("Realizar Viaje");
            
            Color colorBoton = new Color(100, 200, 100);
            btnEditar.setBackground(colorBoton);
            btnVehiculo.setBackground(colorBoton);
            btnViaje.setBackground(colorBoton);
            
            btnEditar.setPreferredSize(d);
            btnVehiculo.setPreferredSize(d);
            btnViaje.setPreferredSize(d);

            btnVehiculo.addActionListener(e -> {
                new VentanaAñadirVehiculo(email, idUsuario).setVisible(true);
                dispose();
            });
            
            btnViaje.addActionListener(e -> {
                new VentanaRealizarViaje(idUsuario, email).setVisible(true);
                dispose();
            });

            panelBotones.add(btnEditar);
            panelBotones.add(btnVehiculo);
            panelBotones.add(btnViaje);
            
            add(panelBotones, BorderLayout.CENTER);
        }

        JPanel panelAtras = new JPanel(new BorderLayout());
        JButton btonAtras = new JButton("Cerrar sesión");

        btonAtras.addActionListener(e -> {
            new VentanaPrincipal().setVisible(true);
            dispose();
        });

        add(panelAtras,BorderLayout.SOUTH);
        panelAtras.add(btonAtras,BorderLayout.WEST);
    }
}