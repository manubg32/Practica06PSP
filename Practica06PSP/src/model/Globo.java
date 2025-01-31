package model;

import java.awt.*;

public class Globo extends Thread {
    private final int x; // Posición X del globo
    private int y; // Posición Y del globo
    private final int tamaño; // Tamaño del globo
    private final Color color; // Color del globo
    private boolean corriendo = true; // Control del hilo
    private boolean pausado = false; // Control de pausa
    private final Object lock = new Object(); // Bloqueo para sincronización

    public Globo(int x, int y, int tamaño, Color color) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.color = color;
    }

    @Override
    public void run() {
        while (corriendo) {
            synchronized (lock) {
                while (pausado) { // Si está pausado, esperar
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            y -= 3; // Mover el globo hacia arriba
            if (y < 0) y = 700; // Reiniciar cuando llegue arriba

            try {
                Thread.sleep((int) (Math.random() * 25 + 25)); // Simular velocidad
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para detener completamente el hilo
    public void detener() {
        corriendo = false;
    }

    // Método para pausar el hilo
    public void pausar() {
        synchronized (lock) {
            pausado = true;
        }
    }

    // Método para reanudar el hilo
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

    public Color getColor() {
        return color;
    }
}
