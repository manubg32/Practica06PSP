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
    //Instanciamos las variables necesarias
    private final List<Globo> globos;                        //Lista de globos
    private boolean carreraTerminada = false;                //Booleano para comprobar si se ha terminado la carrera
    private boolean carreraEnCurso = false;                  //Booleano para comprobar si la carrera sigue en curso
    private BufferedImage buffer;                            //Buffer con las imagenes
    private int frames = 0;                                  //Imagenes que se muestran por segundo
    private long lastTime = System.nanoTime();               //Variable que recoge el tiempo
    private int fps = 0;                                     //Variable a la que le asignaremos los fps (frames)
    private JButton btnJugar;                                //Boton para comenzar la carrera
    private List<Globo> ganadores;                           //Lista de globos ganadores
    private Techo techo = new Techo(0);                //Techo que definira la altura

    public static BufferedImage fondo = null;                //Buffer que carga el fondo
    public static BufferedImage explosion = null;            //Buffer que carga la explosion

    public PanelDeCarrera() {
        try {
            //Cargamos la imagen de fondo
            fondo = ImageIO.read(new File("Practica06PSP/src/resources/fondo.png"));

            //Cargamos la imagen de la explosion
            explosion = ImageIO.read(new File("Practica06PSP/src/resources/explosion.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Inicializamos la lista de globos, ganadores y el buffer
        globos = new ArrayList<>();
        ganadores = new ArrayList<>();
        buffer = new BufferedImage(380, 800, BufferedImage.TYPE_INT_ARGB);
        setLayout(null);

        //Damos atributos y localizamos el boton jugar
        btnJugar = new JButton("Jugar");
        btnJugar.setBounds(150, 720, 80, 30);

        //Si la carrera esta en curso desactivamos el boton
        btnJugar.addActionListener(e -> {
            if (carreraEnCurso) {
                setEnabled(false);
            } else {
                iniciarCarrera();
            }
        });
        add(btnJugar);

        //Para cuando pulsemos sobre los globos que se detengan
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Globo globo : globos) {
                    if (globo.contains(e.getPoint())) {
                        globo.pausar();
                    }
                }
            }

            //Y que sigan subiendo cuando dejemos de pulsar
            @Override
            public void mouseReleased(MouseEvent e) {
                for (Globo globo : globos) {
                    if (globo.contains(e.getPoint())) {
                        globo.reanudar();
                    }
                }
            }
        });

        //Inicializamos los globos
        iniciarGlobos();
    }

    private void iniciarCarrera() {
        carreraEnCurso = true;
        carreraTerminada = false;
        ganadores.clear();
        globos.forEach(globo -> globo.setY(700));
        globos.forEach(Globo::start);
    }


    private void iniciarGlobos() {

        //Creamos los globos
        globos.add(new Globo(25, 700, 50, "Practica06PSP/src/resources/globoRojo.png"));
        globos.add(new Globo(100, 700, 50, "Practica06PSP/src/resources/globoAzul.png"));
        globos.add(new Globo(175, 700, 50, "Practica06PSP/src/resources/globoVerde.png"));
        globos.add(new Globo(250, 700, 50, "Practica06PSP/src/resources/globoAmarillo.png"));
        globos.add(new Globo(325, 700, 50, "Practica06PSP/src/resources/globoNaranja.png"));

        //Mientas que la carrera no este terminada pintamos, verificamos si hay ganador y calculamos los FPS en un nuevo hilo
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
        }).start(); //Iniciamos el hilo
    }

    //Funcion que pinta los componentes
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //Instanciamos Graphics2D y ponemos el fondo blanco
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        //Si existe la imagen de fondo, la ponemos
        if (fondo != null) {
            g2d.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }

        //Por cada globo pintamos el globo en la posicion que corresponda, y si ya ha llegado a la meta pintamos la explosion
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

        //Pintamos los FPS
        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + fps, getWidth() - 60, getHeight() - 20);

    }

    //Funcion que comprueba si hay ganadores
    private void verificarGanador() {
        for (Globo globo : globos) {
            //Si el globo ha colisionado con el techo lo añadimos a ganadores
            if (globo.getY() <= techo.getAltura() && !ganadores.contains(globo)) {
                ganadores.add(globo);
                if (ganadores.size() == globos.size()) { //Si todos los globos han llegado
                    carreraTerminada = true; //Finalizamos la carrera
                    mostrarPodio(); //Mostramos el podio
                }
                globo.detener();
            }
        }
    }

    //Funcion que muestra el podio
    private void mostrarPodio() {

        //Con esto decimos que se inicialice en el momento de mostrarse para no consumir recursos
        SwingUtilities.invokeLater(() -> {
            String podioMessage = "Podio:\n";

            //Invertimos la lista de ganadores, ya que el ultimo es el primero
            List<Globo> ganadoresInvertidos = new ArrayList<>(ganadores);
            Collections.reverse(ganadoresInvertidos); //Invertimos el orden de la lista de ganadores

            //Limitamos la cantidad de ganadores a 3
            int cantidadGanadores = Math.min(ganadoresInvertidos.size(), 3);

            //Para cada uno le asignamos su puesto
            for (int i = 0; i < cantidadGanadores; i++) {
                String puesto = switch (i) {
                    case 0 -> "Oro: ";
                    case 1 -> "Plata: ";
                    case 2 -> "Bronce: ";
                    default -> " ";
                };

                //Agregamos al mensaje la medalla y el globo
                podioMessage += puesto + obtenerNombreColor(ganadoresInvertidos.get(i).getRuta()) + "\n";
            }

            //Mostamos el JOptionPane
            JOptionPane.showMessageDialog(this, podioMessage, "Resultados del Podio", JOptionPane.INFORMATION_MESSAGE);

            //Cuando se pulse sobre OK se cerrara la aplicacion
            System.exit(0);
        });
    }

    //Funcion que formatea y devuelve el color del globo en base a su path
    private String obtenerNombreColor(String ruta) {
        return "Globo " + ruta.substring(33, ruta.lastIndexOf(".png"));
    }

    //Funcion que calcula los FPS
    private void calcularFPS() {
        //Obtenemos el tiempo actual en nanosegundos
        long now = System.nanoTime();

        //Calculamos el tiempo transcurrido desde la ultima actualizacion de FPS
        long deltaTime = now - lastTime;

        //Si ha pasado menos de un segundo
        if (deltaTime >= 1_000_000_000) {
            //Guardamos el numero de frames renderizados en este segundo
            fps = frames;
            //Reiniciamos el contador de frames
            frames = 0;
            //Actualizamos el tiempo de referencia
            lastTime = now;
        }
        //Incrementamos el contador de frames
        frames++;
    }
}
