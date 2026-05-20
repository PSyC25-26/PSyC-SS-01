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

public class VentanaEditarPasajero extends JFrame {

    final Color fondoClaritoVerde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final Color btnNormalVerde = new Color(79, 201, 95);
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final EmptyBorder paddingBtnAtras = new EmptyBorder(10, 15, 10, 15);

    public VentanaEditarPasajero(String emailActual, Long idPasajero) {
        setTitle("Editar Perfil de Pasajero");
        setSize(450, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(fondoClaritoVerde);

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelForm.setBackground(fondoClaritoVerde);

        JTextField txtNombre = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtMetodoPago = new JTextField();

        panelForm.add(new JLabel("Nuevo Nombre:")); panelForm.add(txtNombre);
        panelForm.add(new JLabel("Nueva Contraseña:")); panelForm.add(txtPass);
        panelForm.add(new JLabel("Método de Pago:")); panelForm.add(txtMetodoPago);

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(btnNormalVerde);
        btnGuardar.setFont(fontBtnNormal);
        btnGuardar.setBorder(btnNormalBorde);
        btnGuardar.setForeground(Color.white);

        JButton btnEliminar = new JButton("Eliminar cuenta");
        btnEliminar.setForeground(Color.RED.darker());

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnVolver.setForeground(btnSalirFont);

        panelForm.add(btnVolver); panelForm.add(btnGuardar);
        panelForm.add(new JLabel("")); panelForm.add(btnEliminar);
        add(panelForm, BorderLayout.CENTER);

        btnVolver.addActionListener(e -> {
            new DashboardFrame("PASAJERO", emailActual, idPasajero).setVisible(true);
            dispose();
        });

        btnGuardar.addActionListener(e -> guardarCambios(idPasajero, txtNombre, txtPass, txtMetodoPago, btnVolver));
        btnEliminar.addActionListener(e -> eliminarCuenta(idPasajero));
    }

    private void guardarCambios(Long idPasajero, JTextField txtNombre, JPasswordField txtPass, JTextField txtMetodoPago, JButton btnVolver) {
        try {
            String jsonBody = String.format(
                "{\"nombre\":\"%s\", \"password\":\"%s\", \"metodoPago\":\"%s\"}",
                escape(txtNombre.getText()), escape(new String(txtPass.getPassword())), escape(txtMetodoPago.getText())
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/passengers/update/" + idPasajero))
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

    private void eliminarCuenta(Long idPasajero) {
        int confirm = JOptionPane.showConfirmDialog(this, "Esto eliminará tu cuenta y cancelará tus viajes activos. ¿Seguro?", "Eliminar cuenta", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/passengers/" + idPasajero))
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

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
