package model;

import java.awt.*;

public class Globo extends Thread{
    private final int x; // Posición X del globo
    private int y; // Posición Y del globo
    private final int tamaño; // Tamaño del globo
    private final Color color; // Color del globo
    private boolean corriendo = true; // Control del movimiento

    public Globo(int x, int y, int tamaño, Color color) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.color = color;
    }

    @Override
    public void run() {
        while (corriendo && y < 800) { // Se mueve mientras no alcance el borde derecho
            y += 3;
            try {
                Thread.sleep((int) (Math.random()*25+25)); // Pausa para simular el movimiento
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        corriendo = false; // Detener la bola al llegar al borde
    }

    public void detener() {
        corriendo = false;
    }

    // Métodos para obtener los datos de la bola
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
