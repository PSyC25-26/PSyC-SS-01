package deusto.sd.ubesto.swing;
import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;

public class VentanaPrincipal extends JFrame {
    public VentanaPrincipal() {
        setTitle("Ubesto - Inicio");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Usamos un GridBagLayout para centrar los botones como en el diseño
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton btnViajar = new JButton("Quiero viajar");
        
        LineBorder bordeVerde = new LineBorder(new Color(24, 152, 87), 2, true);
        // btnViajar.setForeground(Color.white);
        // btnViajar.setBorder(bordeVerde);
        btnViajar.setBackground(new Color(64, 192, 87)); // Color verde estilo boceto: Color(100, 200, 100)
        // btnViajar.setSize(200,400);
        
        JButton btnConducir = new JButton("Quiero conducir");
        // btnConducir.setForeground(Color.white);
        // btnConducir.setBorder(bordeVerde);
        btnConducir.setBackground(new Color(64, 192, 87));
        

        // Redirigen a la ventana combinada de Auth
        btnViajar.addActionListener(e -> {
            new VentanaAuth("PASAJERO").setVisible(true);
            dispose();
        });
        
        btnConducir.addActionListener(e -> {
            new VentanaAuth("CONDUCTOR").setVisible(true);
            dispose();
        });

        gbc.gridy = 0; add(btnViajar, gbc);
        gbc.gridy = 1; add(btnConducir, gbc);
    }

    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));

    //     SpringApplication.run(UbestoApplication.class, args);
    // }
}