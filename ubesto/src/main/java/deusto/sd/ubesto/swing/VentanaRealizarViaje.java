package deusto.sd.ubesto.swing;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.*;

public class VentanaRealizarViaje extends JFrame {
    public VentanaRealizarViaje(Long idConductor) {
        setTitle("Panel de Conducción");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        add(new JLabel("ID del Viaje solicitado:"));
        JTextField txtTripId = new JTextField(10);
        add(txtTripId);

        JButton btnAceptar = new JButton("ACEPTAR Y EMPEZAR");
        btnAceptar.setBackground(new Color(100, 200, 100));
        add(btnAceptar);

        btnAceptar.addActionListener(e -> {
            try {
                String tripId = txtTripId.getText();
                String url = "http://localhost:8080/trips/" + tripId + "/accept/" + idConductor;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody()).build();

                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

                if (res.statusCode() == 200) {
                    JOptionPane.showMessageDialog(this, "¡Viaje iniciado! La simulación está corriendo en el servidor.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo aceptar el viaje: " + res.body());
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }
}