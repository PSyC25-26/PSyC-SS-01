package deusto.sd.ubesto.swing;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.*;

public class VentanaEditarConductor extends JFrame {
    
    public VentanaEditarConductor(String emailActual, Long idConductor) {
        setTitle("Editar Perfil de Conductor");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campos a editar
        JTextField txtNombre = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtLicencia = new JTextField();

        panelForm.add(new JLabel("Nuevo Nombre:")); 
        panelForm.add(txtNombre);
        
        panelForm.add(new JLabel("Nueva Contraseña:")); 
        panelForm.add(txtPass);
        
        panelForm.add(new JLabel("Nueva Licencia:")); 
        panelForm.add(txtLicencia);

        // Botones
        JButton btnVolver = new JButton("Volver");
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(100, 200, 100));

        panelForm.add(btnVolver); 
        panelForm.add(btnGuardar);
        
        add(panelForm, BorderLayout.CENTER);

        // EVENTOS
        btnVolver.addActionListener(e -> {
            new DashboardFrame("CONDUCTOR", emailActual, idConductor).setVisible(true);
            dispose();
        });

        btnGuardar.addActionListener(e -> {
            try {
            
                String url = "http://localhost:8080/drivers/update/" + idConductor; 
                
                String jsonBody = String.format(
                    "{\"nombre\":\"%s\", \"password\":\"%s\", \"licenciaConducir\":\"%s\"}", 
                    txtNombre.getText(), new String(txtPass.getPassword()), txtLicencia.getText()
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody)) 
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 || response.statusCode() == 204) {
                    JOptionPane.showMessageDialog(this, "¡Datos actualizados correctamente!");
                    btnVolver.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar.\n" + response.body(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) { 
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}