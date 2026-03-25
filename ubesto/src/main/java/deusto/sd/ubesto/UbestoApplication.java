package deusto.sd.ubesto;

import javax.swing.SwingUtilities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import deusto.sd.ubesto.swing.VentanaPrincipal;

@SpringBootApplication
public class UbestoApplication {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
		SpringApplication.run(UbestoApplication.class, args);
		
	}

}
