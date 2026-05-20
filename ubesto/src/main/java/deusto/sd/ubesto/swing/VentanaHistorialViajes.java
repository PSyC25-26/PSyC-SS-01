package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VentanaHistorialViajes extends JFrame {
    final Color fondoClaritoVerde = new Color(224, 250, 228);
    final Color btnNormalVerde = new Color(79, 201, 95);
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);

    private final ObjectMapper mapper = new ObjectMapper();

    public VentanaHistorialViajes(String email, Long idPasajero) {
        setTitle("Mi Historial de Viajes");
        setSize(820, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(fondoClaritoVerde);

        String[] columnas = {"ID", "Origen", "Destino", "Precio (€)", "Estado", "Estrellas", "Conductor"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.setBackground(Color.WHITE);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        cargarHistorial(idPasajero, modelo);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(fondoClaritoVerde);

        JButton btnVolver = new JButton("Volver al Menú");
        JButton btnValorar = new JButton("Valorar Viaje Seleccionado");
        JButton btnCancelar = new JButton("Cancelar Viaje Seleccionado");

        styleButton(btnValorar);
        styleButton(btnCancelar);

        panelBotones.add(btnVolver);
        panelBotones.add(btnValorar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnVolver.addActionListener(e -> {
            new DashboardFrame("PASAJERO", email, idPasajero).setVisible(true);
            dispose();
        });

        btnValorar.addActionListener(e -> valorarViaje(tabla, modelo, email, idPasajero));
        btnCancelar.addActionListener(e -> cancelarViaje(tabla, modelo, idPasajero));
    }

    private void cargarHistorial(Long idPasajero, DefaultTableModel modelo) {
        modelo.setRowCount(0);
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/passengers/" + idPasajero + "/trips"))
                .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode viajes = mapper.readTree(response.body());
                for (JsonNode t : viajes) {
                    modelo.addRow(new Object[]{
                        t.get("id").asLong(),
                        formatPosition(t.get("posicionOrigen")),
                        formatPosition(t.get("posicionDestino")),
                        t.get("precio").asDouble(),
                        t.get("estado").asText(),
                        t.get("rating").isNull() ? "" : t.get("rating").asInt(),
                        t.get("driverNombre").isNull() ? "" : t.get("driverNombre").asText()
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cargar el historial: " + response.body(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void valorarViaje(JTable tabla, DefaultTableModel modelo, String email, Long idPasajero) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un viaje primero.");
            return;
        }

        Long tripId = Long.parseLong(modelo.getValueAt(fila, 0).toString());
        String estado = modelo.getValueAt(fila, 4).toString();
        if (!estado.equals("FINALIZADO")) {
            JOptionPane.showMessageDialog(this, "Solo puedes valorar viajes FINALIZADOS.");
            return;
        }

        String estrellas = JOptionPane.showInputDialog(this, "Puntúa el viaje del 1 al 5:");
        if (estrellas == null || estrellas.isBlank()) {
            return;
        }

        try {
            int calificacion = Integer.parseInt(estrellas);
            if (calificacion < 1 || calificacion > 5) {
                JOptionPane.showMessageDialog(this, "La puntuación debe estar entre 1 y 5.");
                return;
            }

            HttpClient client = HttpClient.newHttpClient();
            String body = String.format("{\"passengerId\":%d,\"estrellas\":%d}", idPasajero, calificacion);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/trips/" + tripId + "/rate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "¡Gracias por tu valoración!");
                new VentanaHistorialViajes(email, idPasajero).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, response.body(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduce un número válido.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarViaje(JTable tabla, DefaultTableModel modelo, Long idPasajero) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un viaje primero.");
            return;
        }

        Long tripId = Long.parseLong(modelo.getValueAt(fila, 0).toString());
        String estado = modelo.getValueAt(fila, 4).toString();
        if (estado.equals("FINALIZADO") || estado.equals("CANCELADO")) {
            JOptionPane.showMessageDialog(this, "Ese viaje no se puede cancelar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Cancelar el viaje " + tripId + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/trips/" + tripId + "/cancel/passenger/" + idPasajero + "?reason=Cancelado%20desde%20Swing"))
                .POST(HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Viaje cancelado.");
                cargarHistorial(idPasajero, modelo);
            } else {
                JOptionPane.showMessageDialog(this, response.body(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatPosition(JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }
        return node.get("latitud").asDouble() + ", " + node.get("longitud").asDouble();
    }

    private void styleButton(JButton button) {
        button.setBackground(btnNormalVerde);
        button.setForeground(Color.WHITE);
        button.setFont(fontBtnNormal);
    }
}
