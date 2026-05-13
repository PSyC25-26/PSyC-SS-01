package deusto.sd.ubesto;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import deusto.sd.ubesto.swing.VentanaPrincipal;

@SpringBootApplication
public class UbestoApplication {

    public static void main(String[] args) {
        // Añadir estética: Aplicar Look and Feel moderno (Nimbus)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el estilo Nimbus: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
        SpringApplication.run(UbestoApplication.class, args);
    }
}