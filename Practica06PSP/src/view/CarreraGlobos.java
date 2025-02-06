package view;

import javax.swing.*;

public class CarreraGlobos extends JFrame{

    //Ventana en la que se desarrolla la aplicacion
    public CarreraGlobos() {
        setTitle("Carrera de Globos");
        setSize(400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        ImageIcon icon = new ImageIcon("Practica06PSP/src/resources/globoRojo.png");
        setIconImage(icon.getImage());

        //Instanciamos el panel de carrera
        PanelDeCarrera panel = new PanelDeCarrera();
        add(panel); //Agregamos el panel al JFrame
    }

    //Funcion principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarreraGlobos frame = new CarreraGlobos();
            frame.setVisible(true);
        });
    }
}
