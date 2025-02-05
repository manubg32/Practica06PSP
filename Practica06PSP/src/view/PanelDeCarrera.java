package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import model.Globo;
import model.Techo;

class PanelDeCarrera extends JPanel {
    private final List<Globo> globos;
    private boolean carreraTerminada = false;
    private boolean carreraEnCurso = false;
    private BufferedImage buffer;
    private int frames = 0;
    private long lastTime = System.nanoTime();
    private int fps = 0;
    private JButton btnJugar;
    private List<Globo> ganadores;
    private Techo techo = new Techo(0);

    public static BufferedImage fondo = null;
    public static BufferedImage explosion = null;

    public PanelDeCarrera() {
        try {
            fondo = ImageIO.read(new File("Practica06PSP/src/resources/fondo.png"));
            explosion = ImageIO.read(new File("Practica06PSP/src/resources/explosion.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        globos = new ArrayList<>();
        ganadores = new ArrayList<>();
        buffer = new BufferedImage(380, 800, BufferedImage.TYPE_INT_ARGB);
        setLayout(null);

        btnJugar = new JButton("Jugar");
        btnJugar.setBounds(150, 720, 80, 30);
        btnJugar.addActionListener(e -> {
            if (carreraEnCurso) {
                setEnabled(false);
            } else {
                iniciarCarrera();
            }
        });
        add(btnJugar);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Globo globo : globos) {
                    if (globo.contains(e.getPoint())) {
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
        carreraEnCurso = true;
        carreraTerminada = false;
        ganadores.clear();
        globos.forEach(globo -> globo.setY(700));
        globos.forEach(Globo::start);
    }


    private void iniciarBolas() {
        globos.add(new Globo(25, 700, 50, "Practica06PSP/src/resources/globoRojo.png"));
        globos.add(new Globo(100, 700, 50, "Practica06PSP/src/resources/globoAzul.png"));
        globos.add(new Globo(175, 700, 50, "Practica06PSP/src/resources/globoVerde.png"));
        globos.add(new Globo(250, 700, 50, "Practica06PSP/src/resources/globoAmarillo.png"));
        globos.add(new Globo(325, 700, 50, "Practica06PSP/src/resources/globoNaranja.png"));

        new Thread(() -> {
            while (!carreraTerminada) {
                repaint();
                verificarGanador();
                calcularFPS();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (fondo != null) {
            g2d.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }

        for (Globo globo : globos) {
            if (ganadores.contains(globo)) {
                g2d.drawImage(explosion, globo.getX(), techo.getAltura(), globo.getTamaño(), globo.getTamaño(), null);
            } else {
                BufferedImage imagen = globo.getImagen();
                if (imagen != null) {
                    g2d.drawImage(imagen, globo.getX(), globo.getY(), globo.getTamaño(), globo.getTamaño(), null);
                }
            }
        }

        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + fps, getWidth() - 60, getHeight() - 20);

    }

    private void verificarGanador() {
        for (Globo globo : globos) {
            if (globo.getY() <= techo.getAltura() && !ganadores.contains(globo)) {
                ganadores.add(globo);
                if (ganadores.size() == globos.size()) { // Si todos los globos han llegado
                    carreraTerminada = true; // Finalizar la carrera
                    mostrarPodio(); // Mostrar el podio
                    btnJugar.setEnabled(true);
                }
                globo.detener();
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
                podioMessage += puesto + obtenerNombreColor(ganadoresInvertidos.get(i).getRuta()) + "\n";
            }

            JOptionPane.showMessageDialog(this, podioMessage, "Resultados del Podio", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private String obtenerNombreColor(String ruta) {
        return "globo " + ruta.substring(ruta.lastIndexOf("/") + 1, ruta.lastIndexOf(".png"));
    }

    private void calcularFPS() {
        long now = System.nanoTime();
        long deltaTime = now - lastTime;
        if (deltaTime >= 1_000_000_000) {
            fps = frames;
            frames = 0;
            lastTime = now;
        }
        frames++;
    }
}
