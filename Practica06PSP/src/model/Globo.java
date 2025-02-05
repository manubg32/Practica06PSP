package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Globo extends Thread {
    private final int x; // Posición X del globo
    private int y; // Posición Y del globo
    private final int tamaño; // Tamaño del globo
    private final String path; // Imagen del globo
    private BufferedImage imagen;
    private boolean corriendo = true; // Control del hilo
    private boolean pausado = false; // Control de pausa
    private final Object lock = new Object(); // Bloqueo para sincronización

    public Globo(int x, int y, int tamaño, String ruta) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.path = ruta;

        File file = new File(ruta);
        System.out.println("Intentando cargar: " + file.getAbsolutePath());
        System.out.println("¿El archivo exidte?: " + file.exists());

        try {
            imagen = ImageIO.read(new File(ruta)); // Cargar la imagen solo una vez
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error cargando la imagen: " + ruta);
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock) {
                while (pausado) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return; // Salir del hilo si es interrumpido
                    }
                }

                if (!corriendo) break; // ✅ Salir del hilo inmediatamente
            }

            y -= 3; // Mover el globo hacia arriba

            try {
                Thread.sleep((int) (Math.random() * 25 + 25)); // Simular velocidad
            } catch (InterruptedException e) {
                return; // Salir del hilo si es interrumpido
            }

            if (!corriendo) break; //  Verificar nuevamente si debe detenerse
        }
    }

    //  Método para detener el hilo completamente
    public void detener() {
        synchronized (lock) {
            corriendo = false;
            pausado = false; // Asegurar que no quede en pausa
            lock.notifyAll();// Despertar cualquier hilo pausado para que termine
        }
    }

    //  Método para pausar el hilo sin detenerlo completamente
    public void pausar() {
        synchronized (lock) {
            pausado = true;
        }
    }

    //  Método para reanudar el hilo pausado
    public void reanudar() {
        synchronized (lock) {
            pausado = false;
            lock.notify(); // Reanudar el hilo pausado
        }
    }

    public boolean contains(Point p) {
        int cx = this.getX() + this.getTamaño() / 2;
        int cy = this.getY() + this.getTamaño() / 2;
        int radius = this.getTamaño() / 2;

        return p.distance(cx, cy) <= radius;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTamaño() {
        return tamaño;
    }

    public String getRuta() {
        return path;
    }

    public BufferedImage getImagen() { return imagen; }
}
