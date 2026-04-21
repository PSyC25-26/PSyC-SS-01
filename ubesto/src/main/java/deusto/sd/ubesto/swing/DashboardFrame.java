package deusto.sd.ubesto.swing;
import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private String rol;
    private String email;
    private Long idUsuario;

    public DashboardFrame(String rol, String email, Long idUsuario) {
        this.rol = rol;
        this.email = email;
        this.idUsuario = idUsuario;

        setTitle("Ubesto Dashboard - " + rol);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Cabecera
        JLabel lblBienvenida = new JLabel("Bienvenido, " + email + " (ID: " + idUsuario + ")");
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(lblBienvenida, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
        Color verdeUbesto = new Color(100, 200, 100);
        Dimension tamañoBoton = new Dimension(160, 180);

        if (rol.equals("PASAJERO")) {
            JButton btnSolicitar = crearBoton("Solicitar Viaje", verdeUbesto, tamañoBoton);
            btnSolicitar.addActionListener(e -> {
                new VentanaSolicitarViaje(email, idUsuario).setVisible(true);
                dispose();
            });
            panelBotones.add(btnSolicitar);
            panelBotones.add(crearBoton("Editar Perfil", verdeUbesto, tamañoBoton));
            panelBotones.add(crearBoton("Ver Historial", verdeUbesto, tamañoBoton));

        } else if (rol.equals("CONDUCTOR")) {
            JButton btnVehiculo = crearBoton("Añadir Vehículo", verdeUbesto, tamañoBoton);
            btnVehiculo.addActionListener(e -> {
                new VentanaAñadirVehiculo(email, idUsuario).setVisible(true);
                dispose();
            });

            JButton btnAceptar = crearBoton("Realizar Viaje", verdeUbesto, tamañoBoton);
            btnAceptar.addActionListener(e -> {
                new VentanaRealizarViaje(idUsuario).setVisible(true);
                dispose();
            });

            panelBotones.add(btnVehiculo);
            panelBotones.add(btnAceptar);
            panelBotones.add(crearBoton("Editar Conductor", verdeUbesto, tamañoBoton));
        }

        add(panelBotones, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, Color color, Dimension d) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setPreferredSize(d);
        btn.setFocusable(false);
        return btn;
    }
}