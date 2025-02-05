package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import model.Globo;
import model.Techo;

class PanelDeCarrera extends JPanel {
    private final List<Globo> globos; // Lista de bolas
    private boolean carreraTerminada = false; // Control de la carrera
    private boolean carreraEnCurso = false; // Control de si la carrera está en curso o no
    private BufferedImage buffer; // Imagen en memoria para el doble buffer
    private int frames = 0; // Contador de frames
    private long lastTime = System.nanoTime(); // Tiempo del último frame
    private int fps = 0; // FPS calculado
    private JButton btnJugar; // Botón para iniciar o detener la carrera
    private List<Globo> ganadores; // Lista de globos que han llegado a la meta
    private Techo techo = new Techo(0);

    public PanelDeCarrera() {

        globos = new ArrayList<>();
        ganadores = new ArrayList<>();
        buffer = new BufferedImage(380, 800, BufferedImage.TYPE_INT_ARGB);
        setLayout(null); // Usar un layout nulo para poder colocar el botón en cualquier lugar

        // Crear el botón "Jugar"
        btnJugar = new JButton("Jugar");
        btnJugar.setBounds(150, 720, 80, 30); // Posicionar el botón
        btnJugar.addActionListener(e -> {
            if (carreraEnCurso) {
                detenerCarrera(); // Detener la carrera
                btnJugar.setText("Jugar"); // Cambiar el texto del botón a "Jugar"
            } else {
                iniciarCarrera(); // Iniciar la carrera
                btnJugar.setText("Detener"); // Cambiar el texto del botón a "Detener"
            }
        });
        add(btnJugar);

        // Detectar clic en los globos
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Globo globo : globos) {
                    if (globo.contains(e.getPoint())) { // Verifica si se hizo clic dentro de un globo
                        globo.pausar();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                for (Globo globo : globos) {
                    if (globo.contains(e.getPoint())) {
                        globo.reanudar();
                    }
                }
            }
        });

        iniciarBolas();
    }

    private void iniciarCarrera() {
        carreraEnCurso = true; // La carrera ha comenzado
        carreraTerminada = false; // La carrera no ha terminado
        ganadores.clear(); // Limpiar la lista de ganadores
        globos.forEach(globo -> globo.setY(700)); // Reposicionar los globos a la posición inicial
        globos.forEach(Globo::start); // Iniciar cada globo (hilo)
    }

    private void detenerCarrera() {
        carreraEnCurso = false; // La carrera ha terminado
        for (Globo globo : globos) {
            globo.detener(); // Detener cada globo
            globo.setY(700);
        }
    }

    private void iniciarBolas() {
        globos.add(new Globo(25, 700, 50, "Practica06PSP/src/resources/globoRojo.png"));
        globos.add(new Globo(100, 700, 50, "Practica06PSP/src/resources/globoAzul.png"));
        globos.add(new Globo(175, 700, 50, "Practica06PSP/src/resources/globoVerde.png"));
        globos.add(new Globo(250, 700, 50, "Practica06PSP/src/resources/globoAmarillo.png"));
        globos.add(new Globo(325, 700, 50, "Practica06PSP/src/resources/globoNaranja.png"));

        System.out.println("Llamando a repaint()...");
        repaint();


        // El hilo de actualización de la pantalla
        new Thread(() -> {
            while (!carreraTerminada) {
                repaint(); // Redibujar el panel
                verificarGanador(); // Verificar si alguna bola llegó a la meta
                calcularFPS(); // Calcular el FPS
                try {
                    Thread.sleep(1); // Actualización periódica
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        System.out.println("Repaint ejecutado.");

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Limpiar fondo

        for (Globo globo : globos) {
            BufferedImage imagen = globo.getImagen();
            if (imagen != null) {
                System.out.println("Dibujando globo en: " + globo.getX() + ", " + globo.getY());
                g2d.drawImage(imagen, globo.getX(), globo.getY(), globo.getTamaño(), globo.getTamaño(), null);
            } else {
                System.out.println("La imagen del globo es NULL.");
            }
        }

        // FPS
        g2d.setColor(Color.BLACK);
        g2d.drawString("FPS: " + fps, getWidth() - 60, getHeight() - 20);
    }


    private void verificarGanador() {
        for (Globo globo : globos) {
            if (globo.getY() <= techo.getAltura() && !ganadores.contains(globo)) { // Meta alcanzada
                ganadores.add(globo); // Agregar al ganador
                if (ganadores.size() == globos.size()) { // Si todos los globos han llegado
                    carreraTerminada = true; // Finalizar la carrera
                    mostrarPodio(); // Mostrar el podio
                }
                globo.detener(); // Detener el globo una vez haya llegado
            }
        }
    }

    private void mostrarPodio() {
        SwingUtilities.invokeLater(() -> {
            String podioMessage = "Podio:\n";

            // Invertimos la lista de ganadores, el último será el primero
            List<Globo> ganadoresInvertidos = new ArrayList<>(ganadores);
            Collections.reverse(ganadoresInvertidos); // Invertimos el orden de la lista de ganadores

            // Limitar la cantidad de ganadores a 3 (en caso de que haya menos de 3)
            int cantidadGanadores = Math.min(ganadoresInvertidos.size(), 3);

            for (int i = 0; i < cantidadGanadores; i++) {
                String puesto = switch (i) {
                    case 0 -> "Oro: ";
                    case 1 -> "Plata: ";
                    case 2 -> "Bronce: ";
                    default -> " ";
                };
                podioMessage += puesto + obtenerNombreColor(ganadoresInvertidos.get(i).getImagen().toString()) + "\n";
            }

            JOptionPane.showMessageDialog(this, podioMessage, "Resultados del Podio", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private String obtenerNombreColor(String color) {
        return color.substring(20);
    }

    // Méto.do para calcular los FPS
    private void calcularFPS() {
        long now = System.nanoTime();
        long deltaTime = now - lastTime;

        // Si ha pasado un segundo (1 segundo = 1,000,000,000 nanosegundos)
        if (deltaTime >= 1_000_000_000) {
            fps = frames;
            frames = 0;
            lastTime = now;
        }

        frames++;
    }
}
