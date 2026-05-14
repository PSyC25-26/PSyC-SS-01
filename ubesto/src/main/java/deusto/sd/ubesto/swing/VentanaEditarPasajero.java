package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.net.URI;
import java.net.http.*;

public class VentanaEditarPasajero extends JFrame {

    final Color fondoClarito_verde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47,158,68),2,true);
    final Color btnNormalVerde =new Color(79,201,95); // Color verde estilo boceto: Color(100, 200, 100)
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47,158,68),2,true);
    final EmptyBorder paddingBtnAtras =  new EmptyBorder(10, 15, 10, 15);

    public VentanaEditarPasajero(String emailActual, Long idPasajero) {
        setTitle("Editar Perfil de Pasajero");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(fondoClarito_verde);

        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelForm.setBackground(fondoClarito_verde);

        // Campos a editar
        JTextField txtNombre = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtMetodoPago = new JTextField();

        panelForm.add(new JLabel("Nuevo Nombre:"));
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Nueva Contraseña:"));
        panelForm.add(txtPass);

        panelForm.add(new JLabel("Método de Pago:"));
        panelForm.add(txtMetodoPago);

        // Botones
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(btnNormalVerde);
        btnGuardar.setFont(fontBtnNormal);
        btnGuardar.setBorder(btnNormalBorde);
        btnGuardar.setForeground(Color.white);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnVolver.setForeground(btnSalirFont);

        panelForm.add(btnVolver);
        panelForm.add(btnGuardar);

        add(panelForm, BorderLayout.CENTER);

        // EVENTOS
        btnVolver.addActionListener(e -> {
            new DashboardFrame("PASAJERO", emailActual, idPasajero).setVisible(true);
            dispose();
        });

        btnGuardar.addActionListener(e -> {
            try {
                String url = "http://localhost:8080/passengers/update/" + idPasajero;

                String jsonBody = String.format(
                    "{\"nombre\":\"%s\", \"password\":\"%s\", \"metodoPago\":\"%s\"}",
                    txtNombre.getText(), new String(txtPass.getPassword()), txtMetodoPago.getText()
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
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
        });
    }
}