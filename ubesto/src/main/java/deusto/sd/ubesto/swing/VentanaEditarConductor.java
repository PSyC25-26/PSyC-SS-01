package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class VentanaEditarConductor extends JFrame {
    final Color fondoClaritoVerde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final Color btnNormalVerde = new Color(79, 201, 95);
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final EmptyBorder paddingBtnAtras = new EmptyBorder(10, 15, 10, 15);

    public VentanaEditarConductor(String emailActual, Long idConductor) {
        setTitle("Editar Perfil de Conductor");
        setSize(560, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel pFondo = new JPanel();
        pFondo.setLayout(new BoxLayout(pFondo, BoxLayout.Y_AXIS));
        add(pFondo);
        pFondo.setBackground(fondoClaritoVerde);

        JPanel panelDatos = new JPanel();
        panelDatos.setBackground(fondoClaritoVerde);
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));

        JPanel panelForm1 = new JPanel(new GridLayout(3, 2, 10, 15));
        panelForm1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelForm1.setBackground(fondoClaritoVerde);

        JTextField txtNombre = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtLicencia = new JTextField();

        panelForm1.add(label("Nuevo Nombre:")); panelForm1.add(txtNombre);
        panelForm1.add(label("Nueva Contraseña:")); panelForm1.add(txtPass);
        panelForm1.add(label("Nueva Licencia:")); panelForm1.add(txtLicencia);

        JSeparator separador = new JSeparator();
        separador.setOrientation(SwingConstants.HORIZONTAL);

        JPanel panelForm2 = new JPanel(new GridLayout(1, 3, 10, 15));
        panelForm2.setBackground(fondoClaritoVerde);
        panelForm2.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panelForm2.add(label("Cambiar vehículo activo por matrícula:"));
        JTextField txtMatricula = new JTextField();
        panelForm2.add(txtMatricula);

        JButton btnCambiarVehiculo = new JButton("Cambiar");
        panelForm2.add(btnCambiarVehiculo);
        stylePrimary(btnCambiarVehiculo);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(fondoClaritoVerde);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnVolver.setForeground(btnSalirFont);
        btnVolver.setBackground(Color.white);

        JButton btnGuardar = new JButton("Guardar Cambios");
        stylePrimary(btnGuardar);

        JButton btnEliminar = new JButton("Eliminar cuenta");
        btnEliminar.setForeground(Color.RED.darker());

        panelBotones.add(btnVolver);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);

        panelDatos.add(panelForm1);
        panelDatos.add(separador);
        panelDatos.add(panelForm2);
        panelDatos.add(panelBotones);
        pFondo.add(panelDatos, BorderLayout.CENTER);

        btnVolver.addActionListener(e -> {
            new DashboardFrame("CONDUCTOR", emailActual, idConductor).setVisible(true);
            dispose();
        });

        btnGuardar.addActionListener(e -> guardarCambios(idConductor, txtNombre, txtPass, txtLicencia, btnVolver));
        btnCambiarVehiculo.addActionListener(e -> cambiarVehiculo(idConductor, txtMatricula));
        btnEliminar.addActionListener(e -> eliminarCuenta(idConductor));
    }

    private void guardarCambios(Long idConductor, JTextField txtNombre, JPasswordField txtPass, JTextField txtLicencia, JButton btnVolver) {
        try {
            String jsonBody = String.format(
                "{\"nombre\":\"%s\", \"password\":\"%s\", \"licenciaConducir\":\"%s\"}",
                escape(txtNombre.getText()), escape(new String(txtPass.getPassword())), escape(txtLicencia.getText())
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/drivers/update/" + idConductor))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                JOptionPane.showMessageDialog(this, "¡Datos actualizados correctamente!");
                btnVolver.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.\n" + response.body(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarVehiculo(Long idConductor, JTextField txtMatricula) {
        if (txtMatricula.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Indica una matrícula.");
            return;
        }
        try {
            String encoded = URLEncoder.encode(txtMatricula.getText().trim(), StandardCharsets.UTF_8);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/vehicles/driver/" + idConductor + "/active?matricula=" + encoded))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Vehículo activo cambiado.");
            } else {
                JOptionPane.showMessageDialog(this, response.body(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCuenta(Long idConductor) {
        int confirm = JOptionPane.showConfirmDialog(this, "Esto eliminará tu cuenta y cancelará tus viajes activos. ¿Seguro?", "Eliminar cuenta", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/drivers/" + idConductor))
                .DELETE()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Cuenta eliminada.");
                new VentanaPrincipal().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, response.body(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(fondoClaritoVerde);
        return label;
    }

    private void stylePrimary(JButton button) {
        button.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        button.setBackground(btnNormalVerde);
        button.setForeground(Color.white);
        button.setFont(fontBtnNormal);
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
