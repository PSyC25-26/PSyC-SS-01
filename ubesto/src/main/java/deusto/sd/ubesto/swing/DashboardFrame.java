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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DashboardFrame extends JFrame {
    final Dimension d = new Dimension(150, 180);
    final Color fondoClarito_verde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47,158,68),2,true);
    final Color btnNormalVerde =new Color(79,201,95); // Color verde estilo boceto: Color(100, 200, 100)
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 14);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47,158,68),2,true);
    final EmptyBorder paddingBtnAtras =  new EmptyBorder(5, 10, 5, 10);


    // CORRECCIÓN 1: Añadimos Long idUsuario al constructor
    public DashboardFrame(String rol, String email, Long idUsuario) {
        setTitle("Dashboard - " + rol);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        // Creamos un panel para la cabecera que tenga 2 filas (Bienvenida y Saldo)
        JPanel panelCabecera = new JPanel(new GridLayout(2, 1));
        panelCabecera.setBackground(fondoClarito_verde);

        JLabel lblBienvenida = new JLabel("Bienvenido, [" + rol + "] " + email + " (ID: " + idUsuario + ")");
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        // Creamos la etiqueta del saldo
        JLabel lblSaldo = new JLabel("Cargando saldo...");
        lblSaldo.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
        lblSaldo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSaldo.setForeground(new Color(47, 158, 68)); 

        panelCabecera.add(lblBienvenida);
        panelCabecera.add(lblSaldo);
        
        add(panelCabecera, BorderLayout.NORTH);
        setBackground(fondoClarito_verde);

        actualizarSaldo(lblSaldo, idUsuario, rol);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(fondoClarito_verde);

        // Si es pasajero, añadimos los botones del boceto
        if (rol.equals("PASAJERO")) {
            
            JButton btnEditar = new JButton("Editar Datos");
            JButton btnBuscar = new JButton( "Solicitar Viaje");
            JButton btnHistorial = new JButton("Ver historial \n de viajes");
            
            btnEditar.setBackground(btnNormalVerde);
            btnEditar.setFont(fontBtnNormal);
            btnEditar.setForeground(Color.white);
            btnEditar.setPreferredSize(d);
            btnEditar.setBorder(btnNormalBorde);//new CompoundBorder(btnNormalBorde, paddingBtnAtras)

            btnBuscar.setBackground(btnNormalVerde);
            btnBuscar.setFont(fontBtnNormal);
            btnBuscar.setForeground(Color.white);
            btnBuscar.setPreferredSize(d);
            btnBuscar.setBorder(btnNormalBorde);

            btnHistorial.setBackground(btnNormalVerde);
            btnHistorial.setFont(fontBtnNormal);
            btnHistorial.setForeground(Color.white);
            btnHistorial.setPreferredSize(d);
            btnHistorial.setBorder(btnNormalBorde);

            btnEditar.addActionListener(e -> {
                new VentanaEditarPasajero(email, idUsuario).setVisible(true);
                dispose();
            });

            btnBuscar.addActionListener(e -> {
                new VentanaSolicitarViaje(email, idUsuario).setVisible(true);
                dispose(); 
            });

            panelBotones.add(btnEditar);
            panelBotones.add(btnBuscar);
            panelBotones.add(btnHistorial);

            add(panelBotones, BorderLayout.CENTER);
            
        } else if (rol.equals("CONDUCTOR")) {
            // JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

            JButton btnEditar = new JButton("Editar Datos");
            btnEditar.setBackground(btnNormalVerde);
            btnEditar.setPreferredSize(d);
            btnEditar.setFont(fontBtnNormal);
            btnEditar.setForeground(Color.white);
            
            JButton btnVehiculo = new JButton("Añadir Vehículo");
            btnVehiculo.setBackground(btnNormalVerde);
            btnVehiculo.setPreferredSize(d);
            btnVehiculo.setFont(fontBtnNormal);
            btnVehiculo.setForeground(Color.white);
            
            JButton btnViaje = new JButton("Realizar Viaje");
            btnViaje.setBackground(btnNormalVerde);
            btnViaje.setPreferredSize(d);
            btnViaje.setFont(fontBtnNormal);
            btnViaje.setForeground(Color.white);
            
            btnVehiculo.setPreferredSize(d);
            btnViaje.setPreferredSize(d);
            btnEditar.setPreferredSize(d);

            btnVehiculo.addActionListener(e -> {
                new VentanaAñadirVehiculo(email, idUsuario).setVisible(true);
                dispose();
            });
            
            btnViaje.addActionListener(e -> {
                new VentanaRealizarViaje(idUsuario, email).setVisible(true);
                dispose();
            });

            btnEditar.addActionListener(e -> {
                new VentanaEditarConductor(email, idUsuario).setVisible(true);
                dispose();
            });

            panelBotones.add(btnEditar);
            panelBotones.add(btnVehiculo);
            panelBotones.add(btnViaje);
            
            add(panelBotones, BorderLayout.CENTER);
        }

        JPanel panelAtras = new JPanel(new BorderLayout());
        panelAtras.setBackground(fondoClarito_verde);
        JButton btnCerrarSesion = new JButton("Cerrar sesión");

        btnCerrarSesion .setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));
        btnCerrarSesion .setForeground(btnSalirFont);
        btnCerrarSesion .setBackground(Color.white);

        btnCerrarSesion .addActionListener(e -> {
            try {
                String url = "http://localhost:8080/";
                if(rol.equals("CONDUCTOR")){
                    url= url+ "drivers/logout/"+String.valueOf(idUsuario);
                }else{
                    url=url+"passengers/logout/"+String.valueOf(idUsuario);
                };
               
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) { 
                    JOptionPane.showMessageDialog(this, "Cerrado sesión exitosamente.","Information", JOptionPane.INFORMATION_MESSAGE);
                    new VentanaPrincipal().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al cerrar sesión", "Error", JOptionPane.ERROR_MESSAGE);
                }
                

            } catch (Exception ex) {
                // TODO: handle exception
            }
        });

        add(panelAtras,BorderLayout.SOUTH);
        panelAtras.add(btnCerrarSesion,BorderLayout.WEST);
    }
    private void actualizarSaldo(JLabel lblSaldo, Long id, String rol) {
        try {
            String url = "http://localhost:8080/" + (rol.equals("PASAJERO") ? "passengers/" : "drivers/") + id;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(response.body());
                double saldo = json.get("monedero").asDouble();
                lblSaldo.setText("Saldo actual: " + saldo + "€");
            }
        } catch (Exception e) {
            lblSaldo.setText("Saldo: ---");
        }
    }
}