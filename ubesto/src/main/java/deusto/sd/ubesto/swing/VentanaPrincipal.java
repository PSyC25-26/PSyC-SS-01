package deusto.sd.ubesto.swing;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;

public class VentanaPrincipal extends JFrame {

    final Color verdeFondo = new Color(224, 250, 228);
    final LineBorder bordeBerde = new LineBorder(new Color(47,158,68),2,true);
    final Color verdeBoton =new Color(79,201,95); // Color verde estilo boceto: Color(100, 200, 100)
    final Font fontBotones = new Font("SansSerif", Font.BOLD, 12);

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
        this.getContentPane().setBackground(verdeFondo);
        
        JButton btnViajar = new JButton("Quiero viajar");
        btnViajar.setFont(fontBotones);
        btnViajar.setForeground(Color.white);
        btnViajar.setBackground(verdeBoton);
        btnViajar.setBorder(new CompoundBorder(bordeBerde, 
            new EmptyBorder(15, 25, 15, 25)));
        
        JButton btnConducir = new JButton("Quiero conducir");
        btnConducir.setFont(fontBotones);
        btnConducir.setForeground(Color.white);
        btnConducir.setBackground(verdeBoton);
        btnConducir.setBorder(new CompoundBorder(bordeBerde, 
            new EmptyBorder(15, 25, 15, 25)));

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
}