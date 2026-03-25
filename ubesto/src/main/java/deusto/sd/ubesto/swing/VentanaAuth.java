package deusto.sd.ubesto.swing;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VentanaAuth extends JFrame {
    private String rol;

    public VentanaAuth(String rol) {
        this.rol = rol;
        setTitle("Autenticación - " + rol);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // --- SECCIÓN REGISTRO ---
        JPanel panelRegistro = new JPanel(new GridLayout(0, 2, 5, 5));
        panelRegistro.setBorder(BorderFactory.createTitledBorder("REGISTER " + rol));
        
        JTextField txtRegNombre = new JTextField();
        JTextField txtRegEmail = new JTextField();
        JPasswordField txtRegPass = new JPasswordField();
        
        panelRegistro.add(new JLabel("User name:")); panelRegistro.add(txtRegNombre);
        panelRegistro.add(new JLabel("Email:")); panelRegistro.add(txtRegEmail);
        panelRegistro.add(new JLabel("Password:")); panelRegistro.add(txtRegPass);
        
        // Campos extra para conductor
        JTextField txtLicencia = new JTextField();
        if (rol.equals("CONDUCTOR")) {
            panelRegistro.add(new JLabel("LicenciaConducir:")); panelRegistro.add(txtLicencia);
            // Ocultamos la calificación en el registro visual, se pondrá por defecto.
        }

        JButton btnRegister = new JButton("REGISTER");
        btnRegister.setBackground(new Color(100, 200, 100));
        panelRegistro.add(new JLabel("")); // Celda vacía para cuadrar el grid
        panelRegistro.add(btnRegister);

        // --- SECCIÓN LOGIN ---
        JPanel panelLogin = new JPanel(new GridLayout(0, 2, 5, 5));
        panelLogin.setBorder(BorderFactory.createTitledBorder("LOGIN"));
        
        JTextField txtLogEmail = new JTextField();
        JPasswordField txtLogPass = new JPasswordField();
        
        panelLogin.add(new JLabel("Email:")); panelLogin.add(txtLogEmail);
        panelLogin.add(new JLabel("Password:")); panelLogin.add(txtLogPass);
        
        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(100, 200, 100));
        panelLogin.add(new JLabel("")); 
        panelLogin.add(btnLogin);

        // --- EVENTOS ---
        
        // EVENTO: REGISTER
        btnRegister.addActionListener(e -> {
            try {
                String nombre = txtRegNombre.getText();
                String email = txtRegEmail.getText();
                String pass = new String(txtRegPass.getPassword());
                
                String url;
                String jsonBody;

                if (rol.equals("PASAJERO")) {
                    url = "http://localhost:8060/passengers/registerPassenger";
                    // Coincide con PassengerDTO
                    jsonBody = String.format(
                        "{\"nombre\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"metodoPago\":\"efectivo\", \"posicionActual\":{\"latitud\":0.0, \"longitud\":0.0}}", 
                        nombre, email, pass
                    );
                } else {
                    url = "http://localhost:8060/drivers/registerDriver";
                    String licencia = txtLicencia.getText();
                    // Coincide con DriverDTO
                    jsonBody = String.format(
                        "{\"nombre\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"licenciaConducir\":\"%s\", \"calificacionMedia\":5.0, \"disponible\":true}", 
                        nombre, email, pass, licencia
                    );
                }

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) { // 201 CREATED según tu Controller
                    JOptionPane.showMessageDialog(this, "Registro exitoso en la Base de Datos.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar (Código " + response.statusCode() + ").\n" + response.body(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor Spring Boot.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENTO: LOGIN
        btnLogin.addActionListener(e -> {
            try {
                String email = txtLogEmail.getText();
                String pass = new String(txtLogPass.getPassword());
                
                String url = rol.equals("PASAJERO") ? 
                    "http://localhost:8060/passengers/loginPassenger" : 
                    "http://localhost:8060/drivers/loginDriver";

                // Coincide con LoginDTO
                String jsonBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, pass);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Tu PassengerController devuelve 202 (ACCEPTED) o el Driver devuelve un String directo (200 OK)
                if (response.statusCode() == 200 || response.statusCode() == 202) { 
                    String respuestaBackend = response.body();
                    
                    // Comprobamos el String que devuelve tu Controller para asegurar que es un éxito
                    if(respuestaBackend.contains("correctamente!")) {
                        JOptionPane.showMessageDialog(this, respuestaBackend);
                        new DashboardFrame(rol, email).setVisible(true);
                        dispose(); // Cierra esta ventana
                    } else {
                        // Caso de fallo controlado (tu backend devuelve String con el error)
                        JOptionPane.showMessageDialog(this, respuestaBackend, "Aviso de Login", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error en credenciales o servidor.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor Spring Boot.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panelRegistro);
        add(Box.createVerticalStrut(20)); // Espacio
        add(panelLogin);
    }
}