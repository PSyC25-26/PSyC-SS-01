package deusto.sd.ubesto.swing;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

public class VentanaAñadirVehiculo extends JFrame {

    public VentanaAñadirVehiculo(String emailConductor) {
        setTitle("Añadir Vehículo - " + emailConductor);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtMatricula = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtModelo = new JTextField();
        JTextField txtColor = new JTextField();
        JComboBox<CategoriaVehiculo> cbCategoria = new JComboBox<>(CategoriaVehiculo.values());

        panelForm.add(new JLabel("Matrícula:")); panelForm.add(txtMatricula);
        panelForm.add(new JLabel("Marca:")); panelForm.add(txtMarca);
        panelForm.add(new JLabel("Modelo:")); panelForm.add(txtModelo);
        panelForm.add(new JLabel("Color:")); panelForm.add(txtColor);
        panelForm.add(new JLabel("Categoría:")); panelForm.add(cbCategoria);

        JButton btnGuardar = new JButton("Guardar Vehículo");
        btnGuardar.setBackground(new Color(100, 200, 100));
        
        JButton btnVolver = new JButton("Volver");

        panelForm.add(btnVolver);
        panelForm.add(btnGuardar);

        add(panelForm, BorderLayout.CENTER);

        // EVENTOS
        btnVolver.addActionListener(e -> {
            new DashboardFrame("CONDUCTOR", emailConductor).setVisible(true);
            dispose();
        });

        btnGuardar.addActionListener(e -> {
            try {
               
                String url = "http://localhost:8080/vehicles/create?driverEmail=" + emailConductor; 
                
                String jsonBody = String.format(
                    "{\"matricula\":\"%s\", \"marca\":\"%s\", \"modelo\":\"%s\", \"color\":\"%s\", \"categoria\":\"%s\"}", 
                    txtMatricula.getText(), txtMarca.getText(), txtModelo.getText(), txtColor.getText(), cbCategoria.getSelectedItem().toString()
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    JOptionPane.showMessageDialog(this, "Vehículo añadido correctamente.");
                    new DashboardFrame("CONDUCTOR", emailConductor).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al añadir vehículo: " + response.body(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}