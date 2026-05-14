package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.net.URI;
import java.net.http.*;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;

public class VentanaSolicitarViaje extends JFrame {
    
    final Color fondoClarito_verde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47,158,68),2,true);
    final Color btnNormalVerde =new Color(79,201,95); // Color verde estilo boceto: Color(100, 200, 100)
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47,158,68),2,true);
    final EmptyBorder paddingBtnAtras =  new EmptyBorder(10, 15, 10, 15);

    public VentanaSolicitarViaje(String emailPasajero, Long idPasajero) {
        setTitle("Solicitar Viaje");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(fondoClarito_verde);

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Introduce las coordenadas"));
        panelForm.setBackground(fondoClarito_verde);

        JTextField txtLatOrigen = new JTextField("0.0"); 
        JTextField txtLonOrigen = new JTextField("0.0");
        JTextField txtLatDestino = new JTextField("1.0"); 
        JTextField txtLonDestino = new JTextField("1.0");
        JComboBox<CategoriaVehiculo> cbCategoria = new JComboBox<>(CategoriaVehiculo.values());
        
        JLabel l1= new JLabel("Latitud Origen:");l1.setOpaque(true);l1.setBackground(fondoClarito_verde);
        panelForm.add(l1); panelForm.add(txtLatOrigen);

        JLabel l2= new JLabel("Longitud Origen:");l2.setOpaque(true);l2.setBackground(fondoClarito_verde);
        panelForm.add(l2); panelForm.add(txtLonOrigen);
        JLabel l3= new JLabel("Longitud Destino:");l3.setOpaque(true);l3.setBackground(fondoClarito_verde);
        panelForm.add(l3); panelForm.add(txtLatDestino);
        JLabel l4= new JLabel("Categoría Deseada:");l4.setOpaque(true);l4.setBackground(fondoClarito_verde);
        panelForm.add(l4); panelForm.add(txtLonDestino);

        // panelForm.add(new JLabel("Longitud Origen:")); panelForm.add(txtLonOrigen);
        // panelForm.add(new JLabel("Latitud Destino:")); panelForm.add(txtLatDestino);
        // panelForm.add(new JLabel("Longitud Destino:")); panelForm.add(txtLonDestino);
        // panelForm.add(new JLabel("Categoría Deseada:")); panelForm.add(cbCategoria);

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