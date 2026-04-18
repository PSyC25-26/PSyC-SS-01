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
        JTextField txtPrecio = new JTextField();
        if (rol.equals("CONDUCTOR")) {
            panelRegistro.add(new JLabel("LicenciaConducir:")); panelRegistro.add(txtLicencia);
            panelRegistro.add(new JLabel("PrecioPorKm:")); panelRegistro.add(txtPrecio);
        }

        JButton btnRegister = new JButton("REGISTER");
        btnRegister.setBackground(new Color(100, 200, 100));
        panelRegistro.add(new JLabel("")); 
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

                // Apuntamos al puerto 8080 que es donde está tu Spring Boot
                if (rol.equals("PASAJERO")) {
                    url = "http://localhost:8080/passengers/registerPassenger"; //
                    jsonBody = String.format(
                        "{\"nombre\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"metodoPago\":\"efectivo\", \"posicionActual\":{\"latitud\":0.0, \"longitud\":0.0}}", 
                        nombre, email, pass
                    ); //
                } else {
                    url = "http://localhost:8080/drivers/registerDriver"; //
                    String licencia = txtLicencia.getText();
                    jsonBody = String.format(
                        "{\"nombre\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"licenciaConducir\":\"%s\", \"calificacionMedia\":5.0, \"disponible\":true}", 
                        nombre, email, pass, licencia
                    ); //
                }

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Tu PassengerController devuelve 201 CREATED
                if (response.statusCode() == 201) { 
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
                
                // Apuntamos al puerto 8080
                String url = rol.equals("PASAJERO") ? 
                    "http://localhost:8080/passengers/loginPassenger" : //
                    "http://localhost:8080/drivers/loginDriver"; //

                String jsonBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, pass); //

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 || response.statusCode() == 202) { 
                    String respuestaBackend = response.body();
                    
                    if(respuestaBackend.contains("correctamente!")) { //
                        JOptionPane.showMessageDialog(this, respuestaBackend);
                        new DashboardFrame(rol, email).setVisible(true);
                        dispose(); 
                    } else {
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
        add(Box.createVerticalStrut(20)); 
        add(panelLogin);
    }
}