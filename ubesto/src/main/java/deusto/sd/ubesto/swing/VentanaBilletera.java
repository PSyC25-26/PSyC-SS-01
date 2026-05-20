package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VentanaBilletera extends JFrame {
    private final Color fondoClaritoVerde = new Color(224, 250, 228);

    public VentanaBilletera(String rol, String email, Long idUsuario) {
        setTitle("Billetera - " + rol);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(fondoClaritoVerde);

        JTextArea datos = new JTextArea();
        datos.setEditable(false);
        datos.setFont(new Font("Monospaced", Font.PLAIN, 14));
        datos.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(datos, BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver");
        btnVolver.addActionListener(e -> {
            new DashboardFrame(rol, email, idUsuario).setVisible(true);
            dispose();
        });
        add(btnVolver, BorderLayout.SOUTH);

        cargarBilletera(rol, idUsuario, datos);
    }

    private void cargarBilletera(String rol, Long idUsuario, JTextArea datos) {
        try {
            String endpoint = rol.equals("CONDUCTOR") ? "drivers" : "passengers";
            String url = "http://localhost:8080/" + endpoint + "/" + idUsuario + "/wallet";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                datos.setText("No se pudo cargar la billetera:\n" + response.body());
                return;
            }

            JsonNode json = new ObjectMapper().readTree(response.body());
            StringBuilder sb = new StringBuilder();
            sb.append("Usuario ID: ").append(json.get("userId").asLong()).append("\n");
            sb.append("Rol: ").append(json.get("rol").asText()).append("\n\n");
            sb.append("Saldo actual: ").append(json.get("saldo").asDouble()).append(" €\n");
            sb.append("Viajes finalizados: ").append(json.get("viajesFinalizados").asLong()).append("\n");
            sb.append("Viajes cancelados: ").append(json.get("viajesCancelados").asLong()).append("\n");
            if (rol.equals("CONDUCTOR")) {
                sb.append("Ganancias totales: ").append(json.get("gananciasTotales").asDouble()).append(" €\n");
                sb.append("Valoraciones recibidas: ").append(json.get("valoracionesRecibidas").asLong()).append("\n");
                sb.append("Calificación media: ").append(json.get("calificacionMedia").asDouble()).append(" / 5\n");
            }
            datos.setText(sb.toString());
        } catch (Exception e) {
            datos.setText("Error de conexión con el servidor.");
        }
    }
}
