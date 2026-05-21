package deusto.sd.ubesto;

import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import deusto.sd.ubesto.swing.VentanaPrincipal;

@SpringBootApplication
public class UbestoApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext context = SpringApplication.run(UbestoApplication.class, args);
        boolean uiEnabled = context.getEnvironment().getProperty("app.ui.enabled", Boolean.class, true);

        if (uiEnabled && !GraphicsEnvironment.isHeadless()) {
            configureLookAndFeel();
            SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
        } else {
            System.out.println("UI Swing desactivada. API REST levantada en modo servidor.");
        }
    }

    private static void configureLookAndFeel() {
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
    }
}
