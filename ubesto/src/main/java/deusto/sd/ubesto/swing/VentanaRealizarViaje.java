package deusto.sd.ubesto.swing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class VentanaRealizarViaje extends JFrame {
    final Color fondoClaritoVerde = new Color(224, 250, 228);
    final LineBorder btnNormalBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final Color btnNormalVerde = new Color(79, 201, 95);
    final Font fontBtnNormal = new Font("SansSerif", Font.BOLD, 12);
    final Color btnSalirFont = new Color(47, 158, 68);
    final LineBorder btnSalirBorde = new LineBorder(new Color(47, 158, 68), 2, true);
    final EmptyBorder paddingBtnAtras = new EmptyBorder(5, 10, 5, 10);

    public VentanaRealizarViaje(Long idConductor, String email) {
        setTitle("Panel de Conducción");
        setSize(420, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel pFondo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        add(pFondo);
        pFondo.setBackground(fondoClaritoVerde);
        getContentPane().setBackground(fondoClaritoVerde);

        String[] columnas = {"ID", "Origen", "Destino", "Precio"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaViajes = new JTable(modeloTabla);
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        JScrollPane scrollPane = new JScrollPane(tablaViajes);
        JButton btnActualizar = new JButton("Actualizar viajes");

        tablaViajes.setBackground(Color.white);
        actualizarDatosTabla(modeloTabla, tablaViajes, centro);

        pFondo.add(scrollPane);
        scrollPane.setBackground(fondoClaritoVerde);

        JPanel panelDatos = new JPanel();
        panelDatos.setBackground(fondoClaritoVerde);
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));

        JPanel panel1BtnActualizar = new JPanel();
        panel1BtnActualizar.setBackground(fondoClaritoVerde);

        JPanel panel2IDtextoArea = new JPanel();
        panel2IDtextoArea.setBackground(fondoClaritoVerde);

        JPanel panel3Botones = new JPanel();
        panel3Botones.setBackground(fondoClaritoVerde);
        JButton btnVolver = new JButton("Volver");
        btnVolver.setBackground(Color.white);
        btnVolver.setForeground(btnSalirFont);
        btnVolver.setBorder(new CompoundBorder(btnSalirBorde, paddingBtnAtras));

        JButton btnAceptar = new JButton("ACEPTAR Y EMPEZAR");
        btnAceptar.setBackground(btnNormalVerde);
        btnAceptar.setForeground(Color.white);
        btnAceptar.setFont(fontBtnNormal);
        btnAceptar.setBorder(new CompoundBorder(btnNormalBorde, paddingBtnAtras));

        pFondo.add(panelDatos);
        panelDatos.add(panel1BtnActualizar);
        panelDatos.add(Box.createVerticalStrut(10));
        panelDatos.add(panel2IDtextoArea);
        panelDatos.add(Box.createVerticalStrut(10));
        panelDatos.add(panel3Botones);

        panel1BtnActualizar.add(btnActualizar);

        panel2IDtextoArea.add(new JLabel("ID del Viaje solicitado:"));
        JTextField txtTripId = new JTextField(6);
        txtTripId.setEditable(false);
        panel2IDtextoArea.add(txtTripId);

        panel3Botones.add(btnVolver);
        panel3Botones.add(btnAceptar);

        btnVolver.addActionListener(e -> {
            new DashboardFrame("CONDUCTOR", email, idConductor).setVisible(true);
            dispose();
        });

        btnAceptar.addActionListener(e -> {
            if (txtTripId.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Selecciona un viaje primero.");
                return;
            }
            try {
                String url = "http://localhost:8080/trips/" + txtTripId.getText() + "/accept/" + idConductor;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody()).build();

                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

                if (res.statusCode() == 200) {
                    JOptionPane.showMessageDialog(this, "¡Viaje iniciado! La simulación está corriendo en el servidor.");
                    actualizarDatosTabla(modeloTabla, tablaViajes, centro);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo aceptar el viaje: " + res.body());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnActualizar.addActionListener(e -> actualizarDatosTabla(modeloTabla, tablaViajes, centro));

        tablaViajes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaViajes.getSelectedRow();
                if (filaSeleccionada != -1) {
                    Object idSeleccionado = tablaViajes.getValueAt(filaSeleccionada, 0);
                    txtTripId.setText(String.valueOf(idSeleccionado));
                }
            }
        });
    }

    public List<String> recargaInfo() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/trips/getAllTrips"))
                .header("Accept", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(response.body(), new TypeReference<List<String>>() {});
            }
            System.err.println("Error del servidor: " + response.statusCode());
            return Collections.emptyList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public void actualizarDatosTabla(DefaultTableModel modeloTabla, JTable tablaViajes, DefaultTableCellRenderer centro) {
        centro.setHorizontalAlignment(JLabel.CENTER);
        modeloTabla.setRowCount(0);
        List<String> viajesBbdd = recargaInfo();
        for (String s1 : viajesBbdd) {
            String[] row = s1.split("__");
            modeloTabla.addRow(row);
        }
        for (int i = 0; i < tablaViajes.getColumnCount(); i++) {
            tablaViajes.getColumnModel().getColumn(i).setCellRenderer(centro);
        }
        tablaViajes.setPreferredScrollableViewportSize(new Dimension(320, 180));
    }
}
