package deusto.sd.ubesto.swing;

import javax.swing.*;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

public class VentanaAnadirVehiculo extends JFrame {
    public VentanaAnadirVehiculo(String email, Long idConductor) {
        setTitle("Registro de Vehículo");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
            if (txtMatricula.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "La matrícula es obligatoria.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String url = "http://localhost:8080/vehicles/create/" + idConductor;
                String body = String.format(
                    "{\"matricula\":\"%s\", \"marca\":\"%s\", \"modelo\":\"%s\", \"color\":\"%s\", \"categoria\":\"%s\"}",
                    escape(txtMatricula.getText()), escape(txtMarca.getText()), escape(txtModelo.getText()), escape(txtColor.getText()), comboCat.getSelectedItem()
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
                    JOptionPane.showMessageDialog(this, "Error: " + res.body(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
