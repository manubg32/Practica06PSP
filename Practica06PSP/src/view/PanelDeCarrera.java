package view;

import model.Globo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

class PanelDeCarrera extends JPanel {
    private final List<Globo> globos; // Lista de bolas
    private boolean carreraTerminada = false; // Control de la carrera
    private boolean carreraEnCurso = false; // Control de si la carrera está en curso o no
    private BufferedImage buffer; // Imagen en memoria para el doble buffer
    private int frames = 0; // Contador de frames
    private long lastTime = System.nanoTime(); // Tiempo del último frame
    private int fps = 0; // FPS calculado
    private JButton btnJugar; // Botón para iniciar o detener la carrera

    public PanelDeCarrera() {
        globos = new ArrayList<>();
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
        globos.forEach(globo -> globo.setY(700)); // Reposicionar los globos a la posición inicial
        globos.forEach(Globo::start); // Iniciar cada globo (hilo)
    }

    private void detenerCarrera() {
        carreraEnCurso = false; // La carrera ha terminado
        for (Globo globo : globos) {
            globo.detener(); // Detener cada globo
        }
    }

    private void iniciarBolas() {
        globos.add(new Globo(25, 700, 30, Color.RED));
        globos.add(new Globo(100, 700, 30, Color.BLUE));
        globos.add(new Globo(175, 700, 30, Color.GREEN));
        globos.add(new Globo(250, 700, 30, Color.YELLOW));
        globos.add(new Globo(325, 700, 30, Color.ORANGE));

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

        if (buffer == null) {
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d = buffer.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, buffer.getWidth(), buffer.getHeight()); // Limpiar el fondo

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Globo globo : globos) {
            g2d.setColor(Color.BLACK);
            g2d.fillOval(globo.getX(), globo.getY(), globo.getTamaño(), globo.getTamaño());
            g2d.setColor(globo.getColor());
            g2d.fillOval(globo.getX() + 3, globo.getY() + 3, globo.getTamaño() - 7 , globo.getTamaño() - 7);
        }

        g.drawImage(buffer, 0, 0, null);

        // Dibujar FPS en la esquina inferior derecha
        g.setColor(Color.BLACK);
        g.drawString("FPS: " + fps, getWidth() - 60, getHeight() - 20);

        g2d.dispose();
    }

    private void verificarGanador() {
        for (Globo globo : globos) {
            if (globo.getY() <= 0) { // Meta alcanzada
                carreraTerminada = true;
                detenerCarrera();
                notificarGanador(globo);
                break;
            }
        }
    }

    private void notificarGanador(Globo ganadora) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "¡El globo de color " + obtenerNombreColor(ganadora.getColor()) + " ganó la carrera!",
                    "Ganador", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private String obtenerNombreColor(Color color) {
        if (color.equals(Color.RED)) return "Rojo";
        if (color.equals(Color.BLUE)) return "Azul";
        if (color.equals(Color.GREEN)) return "Verde";
        if (color.equals(Color.YELLOW)) return "Amarillo";
        if (color.equals(Color.ORANGE)) return "Naranja";
        return "Desconocido";
    }

    // Método para calcular los FPS
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
