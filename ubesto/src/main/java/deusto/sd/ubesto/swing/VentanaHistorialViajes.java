package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import deusto.sd.ubesto.entity.Trip;

public class VentanaHistorialViajes extends JFrame {
    
    final Color fondoClarito_verde = new Color(224, 250, 228);
    final Color btnNormalVerde = new Color(79, 201, 95);
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);

    public VentanaHistorialViajes(String email, Long idPasajero) {
        setTitle("Mi Historial de Viajes");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(fondoClarito_verde);

        String[] columnas = {"ID Viaje", "Origen", "Destino", "Precio (€)", "Estado", "Estrellas"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modelo);
        tabla.setBackground(Color.WHITE);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Descargar historial del backend
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/passengers/" + idPasajero + "/trips"))
                .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                List<Trip> viajes = new ObjectMapper().readValue(response.body(), new TypeReference<List<Trip>>(){});
                for (Trip t : viajes) {
                    modelo.addRow(new Object[]{t.getId(), t.getPosicionOrigen(), t.getPosicionDestino(), t.getPrecio(), t.getEstado(), t.getRating()});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        // Botones Inferiores
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(fondoClarito_verde);
        
        JButton btnVolver = new JButton("Volver al Menú");
        JButton btnValorar = new JButton("Valorar Viaje Seleccionado");
        
        btnValorar.setBackground(btnNormalVerde);
        btnValorar.setForeground(Color.WHITE);
        btnValorar.setFont(fontBtnNormal);

        panelBotones.add(btnVolver);
        panelBotones.add(btnValorar);
        add(panelBotones, BorderLayout.SOUTH);

        // EVENTO: Volver
        btnVolver.addActionListener(e -> {
            new DashboardFrame("PASAJERO", email, idPasajero).setVisible(true);
            dispose();
        });

        // EVENTO: Valorar
        btnValorar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                Long tripId = (Long) modelo.getValueAt(fila, 0);
                String estado = modelo.getValueAt(fila, 4).toString();
                
                if (estado.equals("FINALIZADO")) {
                    String estrellas = JOptionPane.showInputDialog(this, "Puntúa el viaje del 1 al 5:");
                    if (estrellas != null && !estrellas.isEmpty()) {
                        try {
                            int calificacion = Integer.parseInt(estrellas);
                            enviarValoracion(tripId, calificacion);
                            // Recargar la tabla tras valorar
                            new VentanaHistorialViajes(email, idPasajero).setVisible(true);
                            dispose();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Por favor, introduce un número válido.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Solo puedes valorar viajes que ya están FINALIZADOS.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un viaje de la tabla primero.");
            }
        });
    }

    private void enviarValoracion(Long tripId, int estrellas) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/passengers/rateTrip/" + tripId + "?estrellas=" + estrellas))
                .POST(HttpRequest.BodyPublishers.noBody()).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            JOptionPane.showMessageDialog(this, "¡Gracias por tu valoración!");
        } catch (Exception e) { e.printStackTrace(); }
    }
}