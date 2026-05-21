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

public class VentanaAnadirVehiculo extends JFrame {
     final Color fondoClarito_verde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47,158,68),2,true);
    final Color btnNormalVerde =new Color(79,201,95); // Color verde estilo boceto: Color(100, 200, 100)
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47,158,68),2,true);
    final EmptyBorder paddingBtnAtras =  new EmptyBorder(5, 10, 5, 10);

    public VentanaAnadirVehiculo(String email, Long idConductor) {
        setTitle("Registro de Vehículo");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(fondoClarito_verde);
        setLayout(new GridLayout(6, 2, 2, 2));


        JTextField txtMatricula = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtModelo = new JTextField();
        JTextField txtColor = new JTextField();
        JComboBox<CategoriaVehiculo> comboCat = new JComboBox<>(CategoriaVehiculo.values());
        
        JLabel l1=new JLabel(" Matrícula:");
            l1.setBackground(fondoClarito_verde);
            l1.setOpaque(true);
            add(l1); add(txtMatricula);
        JLabel l2=new JLabel(" Marca:");
            l2.setBackground(fondoClarito_verde);
            l2.setOpaque(true);
            add(l2);add(txtMarca);
        JLabel l3=new JLabel(" Modelo:");
            l3.setBackground(fondoClarito_verde);
            l3.setOpaque(true);

            add(l3); add(txtModelo);
        JLabel l4=new JLabel(" Color:");
            l4.setBackground(fondoClarito_verde);
            l4.setOpaque(true);
            add(l4); add(txtColor);
        JLabel l5=new JLabel(" Categoría:");
            l5.setBackground(fondoClarito_verde);
            l5.setOpaque(true);
            add(l5);add(comboCat);

        // add(new JLabel(" Matricula:")); add(txtMatricula);
        // add(new JLabel(" Marca:")); add(txtMarca);
        // add(new JLabel(" Modelo:")); add(txtModelo);
        // add(new JLabel(" Color:")); add(txtColor);
        // add(new JLabel(" Categoría:")); add(comboCat);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(Color.white);
        btnVolver.setForeground(btnSalirFont);
        btnVolver.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(btnNormalVerde);
        btnGuardar.setForeground(Color.white);
        btnGuardar.setFont(fontBtnNormal);
        btnGuardar.setBorder(new CompoundBorder(btnNormalBorde, paddingBtnAtras));

        add(btnVolver); add(btnGuardar);

        btnVolver.addActionListener(e -> {
            new DashboardFrame("CONDUCTOR", email, idConductor).setVisible(true);
            dispose();
        });

        btnGuardar.addActionListener(e -> {
            if (txtMatricula.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "La matrícula es obligatoria.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String url = "http://localhost:8080/vehicles/create/" + idConductor;
                String body = String.format(
                    "{\"matricula\":\"%s\", \"marca\":\"%s\", \"modelo\":\"%s\", \"color\":\"%s\", \"categoria\":\"%s\"}",
                    escape(txtMatricula.getText()), escape(txtMarca.getText()), escape(txtModelo.getText()), escape(txtColor.getText()), comboCat.getSelectedItem()
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();

                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

                if (res.statusCode() == 201) {
                    JOptionPane.showMessageDialog(this, "Vehículo vinculado con éxito.");
                    btnVolver.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + res.body(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
