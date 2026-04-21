package deusto.sd.ubesto.swing;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.*;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

public class VentanaSolicitarViaje extends JFrame {
    
    public VentanaSolicitarViaje(String emailPasajero, Long idPasajero) {
        setTitle("Solicitar Viaje");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(7, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Introduce las coordenadas"));

        JTextField txtLatOrigen = new JTextField("0.0"); 
        JTextField txtLonOrigen = new JTextField("0.0");
        JTextField txtLatDestino = new JTextField("1.0"); 
        JTextField txtLonDestino = new JTextField("1.0");
        JComboBox<CategoriaVehiculo> cbCategoria = new JComboBox<>(CategoriaVehiculo.values());

        panelForm.add(new JLabel("Latitud Origen:")); panelForm.add(txtLatOrigen);
        panelForm.add(new JLabel("Longitud Origen:")); panelForm.add(txtLonOrigen);
        panelForm.add(new JLabel("Latitud Destino:")); panelForm.add(txtLatDestino);
        panelForm.add(new JLabel("Longitud Destino:")); panelForm.add(txtLonDestino);
        panelForm.add(new JLabel("Categoría Deseada:")); panelForm.add(cbCategoria);

        JButton btnSolicitar = new JButton("Solicitar Uber");
        btnSolicitar.setBackground(new Color(100, 200, 100));
        JButton btnVolver = new JButton("Volver");

        panelForm.add(btnVolver); panelForm.add(btnSolicitar);
        add(panelForm, BorderLayout.CENTER);

        // EVENTO: VOLVER
        btnVolver.addActionListener(e -> {
            new DashboardFrame("PASAJERO", emailPasajero, idPasajero).setVisible(true);
            dispose();
        });

        // EVENTO: SOLICITAR VIAJE
        btnSolicitar.addActionListener(e -> {
            try {
                String url = "http://localhost:8080/trips/request"; 
                String jsonBody = String.format(
                    "{\"passengerId\":%d, \"origen\":{\"latitud\":%s, \"longitud\":%s}, \"destino\":{\"latitud\":%s, \"longitud\":%s}, \"categoria\":\"%s\"}", 
                    idPasajero, txtLatOrigen.getText(), txtLonOrigen.getText(), txtLatDestino.getText(), txtLonDestino.getText(), cbCategoria.getSelectedItem().toString()
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                        
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) {
                    JOptionPane.showMessageDialog(this, "¡Viaje solicitado con éxito!");
                    btnVolver.doClick(); // Volvemos automáticamente al dashboard
                } else {
                    JOptionPane.showMessageDialog(this, "Error al solicitar el viaje.\n" + response.body(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) { 
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}