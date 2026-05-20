package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DashboardFrame extends JFrame {
    final Dimension d = new Dimension(150, 120);
    final Color fondoClaritoVerde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final Color btnNormalVerde = new Color(79, 201, 95);
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 13);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final EmptyBorder paddingBtnAtras = new EmptyBorder(5, 10, 5, 10);

    public DashboardFrame(String rol, String email, Long idUsuario) {
        setTitle("Dashboard - " + rol);
        setSize(720, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelCabecera = new JPanel(new GridLayout(2, 1));
        panelCabecera.setBackground(fondoClaritoVerde);

        JLabel lblBienvenida = new JLabel("Bienvenido, [" + rol + "] " + email + " (ID: " + idUsuario + ")");
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        JLabel lblSaldo = new JLabel("Cargando saldo...");
        lblSaldo.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
        lblSaldo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSaldo.setForeground(new Color(47, 158, 68));

        panelCabecera.add(lblBienvenida);
        panelCabecera.add(lblSaldo);
        add(panelCabecera, BorderLayout.NORTH);
        getContentPane().setBackground(fondoClaritoVerde);

        actualizarSaldo(lblSaldo, idUsuario, rol);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(fondoClaritoVerde);

        if (rol.equals("PASAJERO")) {
            JButton btnEditar = buildMainButton("Editar Datos");
            JButton btnBuscar = buildMainButton("Solicitar Viaje");
            JButton btnHistorial = buildMainButton("Historial / Valorar");
            JButton btnBilletera = buildMainButton("Billetera");

            btnEditar.addActionListener(e -> {
                new VentanaEditarPasajero(email, idUsuario).setVisible(true);
                dispose();
            });
            btnBuscar.addActionListener(e -> {
                new VentanaSolicitarViaje(email, idUsuario).setVisible(true);
                dispose();
            });
            btnHistorial.addActionListener(e -> {
                new VentanaHistorialViajes(email, idUsuario).setVisible(true);
                dispose();
            });
            btnBilletera.addActionListener(e -> {
                new VentanaBilletera("PASAJERO", email, idUsuario).setVisible(true);
                dispose();
            });

            panelBotones.add(btnEditar);
            panelBotones.add(btnBuscar);
            panelBotones.add(btnHistorial);
            panelBotones.add(btnBilletera);
        } else if (rol.equals("CONDUCTOR")) {
            JButton btnEditar = buildMainButton("Editar Datos");
            JButton btnVehiculo = buildMainButton("Añadir Vehículo");
            JButton btnViaje = buildMainButton("Realizar Viaje");
            JButton btnBilletera = buildMainButton("Billetera / Ganancias");

            btnVehiculo.addActionListener(e -> {
                new VentanaAnadirVehiculo(email, idUsuario).setVisible(true);
                dispose();
            });
            btnViaje.addActionListener(e -> {
                new VentanaRealizarViaje(idUsuario, email).setVisible(true);
                dispose();
            });
            btnEditar.addActionListener(e -> {
                new VentanaEditarConductor(email, idUsuario).setVisible(true);
                dispose();
            });
            btnBilletera.addActionListener(e -> {
                new VentanaBilletera("CONDUCTOR", email, idUsuario).setVisible(true);
                dispose();
            });

            panelBotones.add(btnEditar);
            panelBotones.add(btnVehiculo);
            panelBotones.add(btnViaje);
            panelBotones.add(btnBilletera);
        }

        add(panelBotones, BorderLayout.CENTER);

        JPanel panelAtras = new JPanel(new BorderLayout());
        panelAtras.setBackground(fondoClaritoVerde);
        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnCerrarSesion.setForeground(btnSalirFont);
        btnCerrarSesion.setBackground(Color.white);

        btnCerrarSesion.addActionListener(e -> cerrarSesion(rol, idUsuario));

        add(panelAtras, BorderLayout.SOUTH);
        panelAtras.add(btnCerrarSesion, BorderLayout.WEST);
    }

    private JButton buildMainButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(btnNormalVerde);
        button.setFont(fontBtnNormal);
        button.setForeground(Color.white);
        button.setPreferredSize(d);
        button.setBorder(btnNormalBorde);
        return button;
    }

    private void cerrarSesion(String rol, Long idUsuario) {
        try {
            String endpoint = rol.equals("CONDUCTOR") ? "drivers/logout/" : "passengers/logout/";
            String url = "http://localhost:8080/" + endpoint + idUsuario;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Sesión cerrada correctamente.");
                new VentanaPrincipal().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cerrar sesión", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarSaldo(JLabel lblSaldo, Long id, String rol) {
        try {
            String url = "http://localhost:8080/" + (rol.equals("PASAJERO") ? "passengers/" : "drivers/") + id + "/wallet";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(response.body());
                double saldo = json.get("saldo").asDouble();
                if (rol.equals("CONDUCTOR")) {
                    double ganancias = json.get("gananciasTotales").asDouble();
                    lblSaldo.setText("Saldo actual: " + saldo + "€ | Ganancias: " + ganancias + "€");
                } else {
                    lblSaldo.setText("Saldo actual: " + saldo + "€");
                }
            }
        } catch (Exception e) {
            lblSaldo.setText("Saldo: ---");
        }
    }
}
