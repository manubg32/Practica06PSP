package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Globo extends Thread {
    private int x;                              //Posicion X del globo
    private int y;                              //Posicion Y del globo
    private final int tamaño;                   //Tamaño del globo
    private final String path;                  //Imagen del globo
    private BufferedImage imagen;               //Buffer de imagen
    private boolean corriendo = true;           //Control del hilo
    private boolean pausado = false;            //Control de pausa
    private final Object lock = new Object();   //Bloqueo para sincronizacion

    //Constructor del globo
    public Globo(int x, int y, int tamaño, String ruta) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.path = ruta;

        //Intentamos cargar la ruta y mostramos por pantalla un estado de la carga
        File file = new File(ruta);
        System.out.println("Intentando cargar: " + file.getAbsolutePath());
        System.out.println("¿El archivo exidte?: " + file.exists());

        try {
            imagen = ImageIO.read(new File(ruta)); //Cargamos la imagen solo una vez
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error cargando la imagen: " + ruta);
        }
    }


    @Override
    public void run() {
        //Bucle infinito para mantener el hilo en ejecucion
        while (true) {
            //Bloque de sincronización para gestionar la pausa y la ejecución segura del hilo
            synchronized (lock) {
                //Si el hilo esta pausado, esperamos hasta que sea notificado para continuar
                while (pausado) {
                    try {
                        lock.wait(); //Espera a que otro hilo lo reanude con lock.notify()
                    } catch (InterruptedException e) {
                        return; //Salimos del hilo si es interrumpido
                    }
                }

                //Si se ha parado el globo, salimos del bucle y terminamos el hilo
                if (!corriendo) break; //Salir del hilo inmediatamente
            }

            //Creamos un objeto random para movimientos aleatorios
            Random r = new Random();

            y -= 3; //Mover el globo hacia arriba

            //Si se mantiene dentro de los margenes de la pantalla movemos el globo a los lados para simular balanceo
            if (x -2 > 0 && x + 6 < 400) {
                x += r.nextInt(-2, 3);
            }

            //Hacemos que cada globo se pare un tiempo aleatorio entre 25 y 50 ms para simular velocidad
            try {
                Thread.sleep((int) (Math.random() * 25 + 25)); // Simular velocidad
            } catch (InterruptedException e) {
                return; //Salimos del hilo si es interrumpido
            }

            if (!corriendo) break; //Verificar nuevamente si debe detenerse
        }
    }

    //Funcion para detener el hilo completamente
    public void detener() {
        //Bloque de sincronización para gestionar la pausa y la ejecución segura del hilo
        synchronized (lock) {
            corriendo = false; //Cambiamos el estado
            pausado = false; //Nos aseguramos que no quede en pausa
            lock.notifyAll(); //Despertamos cualquier hilo pausado para que termine
        }
    }

    //Funcion para pausar el hilo temporalmente
    public void pausar() {
        //Bloque de sincronización para gestionar la pausa y la ejecución segura del hilo
        synchronized (lock) {
            pausado = true; //Pausamos el hilo
        }
    }

    //Funcion para reanudar el hilo pausado
    public void reanudar() {
        //Bloque de sincronización para gestionar la pausa y la ejecución segura del hil
        synchronized (lock) {
            pausado = false;    //Cambiamos el estado
            lock.notify();      //Reanudamos el hilo pausado
        }
    }

    //Funcion para verificar si un punto dado esta dentro del area del globo
    public boolean contains(Point p) {
        //Calculamos el centro del globo
        int cx = this.getX() + this.getTamaño() / 2;
        int cy = this.getY() + this.getTamaño() / 2;

        //Calculamos el radio cilcular del globo
        int radius = this.getTamaño() / 2;

        //Verificamos si la distancia entre el punto 'p' y el centro del globo es menor o igual al radio
        return p.distance(cx, cy) <= radius;
    }

    //Getters & Setters
    public void setY(int y) { this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getTamaño() { return tamaño; }
    public String getRuta() { return path; }
    public BufferedImage getImagen() { return imagen; }
}
