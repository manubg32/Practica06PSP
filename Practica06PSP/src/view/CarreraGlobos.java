package view;

import javax.swing.*;

public class CarreraGlobos extends JFrame{
    public CarreraGlobos() {
        setTitle("Carrera de Globos");
        setSize(400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Crear el panel personalizado
        PanelDeCarrera panel = new PanelDeCarrera();
        add(panel); // Agregar el panel al JFrame
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarreraGlobos frame = new CarreraGlobos();
            frame.setVisible(true);
        });
    }
}
