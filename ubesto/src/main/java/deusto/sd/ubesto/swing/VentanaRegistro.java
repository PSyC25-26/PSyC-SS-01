import javax.swing.*;
import deusto.sd.ubesto.entity.*; // Asegúrate de importar tus entidades
import java.awt.*;

public class VentanaRegistro extends JFrame {
    private String rol;
    private JTextField txtNombre, txtEmail, txtPass, txtExtra; // txtExtra sirve para Licencia o Método Pago

    public VentanaRegistro(String rol) {
        this.rol = rol;
        setTitle("Registro - " + rol);
        setSize(350, 400);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);

        add(new JLabel("Nombre:"));
        txtNombre = new JTextField(); add(txtNombre);
        
        add(new JLabel("Email:"));
        txtEmail = new JTextField(); add(txtEmail);
        
        add(new JLabel("Password:"));
        txtPass = new JPasswordField(); add(txtPass);

        // Campos específicos según la clase
        if (rol.equals("CONDUCTOR")) {
            add(new JLabel("Licencia de Conducir:"));
            txtExtra = new JTextField(); add(txtExtra);
        } else {
            add(new JLabel("Método de Pago (Efectivo/Tarjeta):"));
            txtExtra = new JTextField(); add(txtExtra);
        }

        JButton btnGuardar = new JButton("Crear Cuenta");
        btnGuardar.addActionListener(e -> {
            // Aquí simularíamos la creación del objeto
            if(rol.equals("CONDUCTOR")) {
                // Driver(nombre, email, password, licencia, calificacion, disponible, vehicleId)
                Driver nuevoConductor = new Driver(txtNombre.getText(), txtEmail.getText(), txtPass.getText(), txtExtra.getText(), 5.0, true, null);
                System.out.println("Conductor creado: " + nuevoConductor.getNombre());
            } else {
                // Passenger(nombre, email, password, metodoPago, posicion)
                Passenger nuevoPasajero = new Passenger(txtNombre.getText(), txtEmail.getText(), txtPass.getText(), txtExtra.getText(), new Posicion(0,0));
                System.out.println("Pasajero creado: " + nuevoPasajero.getNombre());
            }
            JOptionPane.showMessageDialog(this, "Registro exitoso.");
            dispose();
        });
        
        add(btnGuardar);
    }
}