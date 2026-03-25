import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {
    private JTextField txtEmail;

    public VentanaLogin() {
        setTitle("Login Uber");
        setSize(300, 150);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        add(new JLabel("Introduce tu Email:"));
        txtEmail = new JTextField(20);
        add(txtEmail);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(e -> {
            String email = txtEmail.getText();
            if (!email.isEmpty()) {
                new VentanaDashboard(email).setVisible(true);
                dispose();
            }
        });
        add(btnEntrar);
    }
}