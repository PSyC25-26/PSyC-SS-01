package deusto.sd.ubesto.swing;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.*;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

public class VentanaSolicitarViaje extends JFrame {
    public VentanaSolicitarViaje(String email, Long idPasajero) {
        setTitle("Solicitar Nuevo Viaje");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 15));

        JTextField tLatO = new JTextField("0.0"); JTextField tLonO = new JTextField("0.0");
        JTextField tLatD = new JTextField("1.0"); JTextField tLonD = new JTextField("1.0");
        JComboBox<CategoriaVehiculo> comboCat = new JComboBox<>(CategoriaVehiculo.values());

        add(new JLabel(" Latitud Origen:")); add(tLatO);
        add(new JLabel(" Longitud Origen:")); add(tLonO);
        add(new JLabel(" Latitud Destino:")); add(tLatD);
        add(new JLabel(" Longitud Destino:")); add(tLonD);
        add(new JLabel(" Tipo de Uber:")); add(comboCat);

        JButton btnVolver = new JButton("Cancelar");
        JButton btnPedir = new JButton("PEDIR UBER");
        btnPedir.setBackground(new Color(100, 200, 100));

        add(btnVolver); add(btnPedir);

        btnPedir.addActionListener(e -> {
            try {
                String url = "http://localhost:8080/trips/request";
                String body = String.format(
                    "{\"passengerId\":%d, \"origen\":{\"latitud\":%s, \"longitud\":%s}, \"destino\":{\"latitud\":%s, \"longitud\":%s}, \"categoria\":\"%s\"}",
                    idPasajero, tLatO.getText(), tLonO.getText(), tLatD.getText(), tLonD.getText(), comboCat.getSelectedItem()
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();

                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

                if (res.statusCode() == 201) {
                    JOptionPane.showMessageDialog(this, "Viaje solicitado. Buscando conductor...");
                    new DashboardFrame("PASAJERO", email, idPasajero).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al solicitar: " + res.body());
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }
}