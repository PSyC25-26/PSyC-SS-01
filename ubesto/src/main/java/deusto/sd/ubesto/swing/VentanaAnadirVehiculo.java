package deusto.sd.ubesto.swing;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.*;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

public class VentanaAñadirVehiculo extends JFrame {
    public VentanaAñadirVehiculo(String email, Long idConductor) {
        setTitle("Registro de Vehículo");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 10, 10));

        JTextField txtMatricula = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtModelo = new JTextField();
        JTextField txtColor = new JTextField();
        JComboBox<CategoriaVehiculo> comboCat = new JComboBox<>(CategoriaVehiculo.values());

        add(new JLabel(" Matrícula:")); add(txtMatricula);
        add(new JLabel(" Marca:")); add(txtMarca);
        add(new JLabel(" Modelo:")); add(txtModelo);
        add(new JLabel(" Color:")); add(txtColor);
        add(new JLabel(" Categoría:")); add(comboCat);

        JButton btnVolver = new JButton("Volver");
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(100, 200, 100));

        add(btnVolver); add(btnGuardar);

        btnVolver.addActionListener(e -> {
            new DashboardFrame("CONDUCTOR", email, idConductor).setVisible(true);
            dispose();
        });

        btnGuardar.addActionListener(e -> {
            try {
                String url = "http://localhost:8080/vehicles/create/" + idConductor;
                String body = String.format(
                    "{\"matricula\":\"%s\", \"marca\":\"%s\", \"modelo\":\"%s\", \"color\":\"%s\", \"categoria\":\"%s\"}",
                    txtMatricula.getText(), txtMarca.getText(), txtModelo.getText(), txtColor.getText(), comboCat.getSelectedItem()
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();

                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

                if (res.statusCode() == 201) {
                    JOptionPane.showMessageDialog(this, "Vehículo vinculado con éxito.");
                    btnVolver.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + res.body());
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }
}