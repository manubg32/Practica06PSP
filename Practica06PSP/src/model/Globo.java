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

    public Color getColor() {
        return color;
    }
}
