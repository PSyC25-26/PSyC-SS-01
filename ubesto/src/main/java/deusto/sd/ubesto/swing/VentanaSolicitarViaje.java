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

import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

public class VentanaSolicitarViaje extends JFrame {
    final Color fondoClaritoVerde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final Color btnNormalVerde = new Color(79, 201, 95);
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final EmptyBorder paddingBtnAtras = new EmptyBorder(10, 15, 10, 15);

    public VentanaSolicitarViaje(String emailPasajero, Long idPasajero) {
        setTitle("Solicitar Viaje");
        setSize(460, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(fondoClaritoVerde);

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Introduce las coordenadas"));
        panelForm.setBackground(fondoClaritoVerde);

        JTextField txtLatOrigen = new JTextField("43.2630");
        JTextField txtLonOrigen = new JTextField("-2.9350");
        JTextField txtLatDestino = new JTextField("43.3200");
        JTextField txtLonDestino = new JTextField("-1.9800");
        JComboBox<CategoriaVehiculo> cbCategoria = new JComboBox<>(CategoriaVehiculo.values());

        panelForm.add(label("Latitud Origen:")); panelForm.add(txtLatOrigen);
        panelForm.add(label("Longitud Origen:")); panelForm.add(txtLonOrigen);
        panelForm.add(label("Latitud Destino:")); panelForm.add(txtLatDestino);
        panelForm.add(label("Longitud Destino:")); panelForm.add(txtLonDestino);
        panelForm.add(label("Categoría Deseada:")); panelForm.add(cbCategoria);

        JButton btnSolicitar = new JButton("Solicitar Uber");
        btnSolicitar.setBackground(btnNormalVerde);
        btnSolicitar.setFont(fontBtnNormal);
        btnSolicitar.setBorder(btnNormalBorde);
        btnSolicitar.setForeground(Color.white);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnVolver.setForeground(btnSalirFont);

        panelForm.add(btnVolver); panelForm.add(btnSolicitar);
        add(panelForm, BorderLayout.CENTER);

        btnVolver.addActionListener(e -> {
            new DashboardFrame("PASAJERO", emailPasajero, idPasajero).setVisible(true);
            dispose();
        });

        btnSolicitar.addActionListener(e -> {
            try {
                Double.parseDouble(txtLatOrigen.getText());
                Double.parseDouble(txtLonOrigen.getText());
                Double.parseDouble(txtLatDestino.getText());
                Double.parseDouble(txtLonDestino.getText());

                String jsonBody = String.format(
                    "{\"passengerId\":%d, \"origen\":{\"latitud\":%s, \"longitud\":%s}, \"destino\":{\"latitud\":%s, \"longitud\":%s}, \"categoria\":\"%s\"}",
                    idPasajero, txtLatOrigen.getText(), txtLonOrigen.getText(), txtLatDestino.getText(), txtLonDestino.getText(), cbCategoria.getSelectedItem()
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/trips/request"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) {
                    JOptionPane.showMessageDialog(this, "¡Viaje solicitado con éxito!");
                    btnVolver.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al solicitar el viaje.\n" + response.body(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Las coordenadas deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(fondoClaritoVerde);
        return label;
    }
}
