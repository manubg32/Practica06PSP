package controller; //Definimos el paquete donde se encuentra esta clase

import java.io.File; //Importamos la clase File para manejar archivos de audio
import java.io.IOException; //Importamos IOException para manejar errores de entrada/salida
import javax.sound.sampled.*; //Importamos las clases necesarias para manejar audio

public class MusicPlayer { //Clase para reproducir música
    private Clip clip; //Objeto Clip que manejará la reproducción del audio

    /**
     * Constructor que recibe la ruta de un archivo de audio y lo reproduce automáticamente.
     * @param filepath Ruta del archivo de audio.
     */
    public MusicPlayer(String filepath) {
        play(filepath); //Llama al método play para iniciar la reproducción
    }

    /**
     * Método para reproducir un archivo de audio.
     * @param filepath Ruta del archivo de audio.
     */
    public void play(String filepath) {
        stop(); //Detiene cualquier audio en reproducción antes de iniciar uno nuevo

        try {
            File file = new File(filepath); //Crea un objeto File con la ruta del audio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file); // Obtiene el flujo de audio
            clip = AudioSystem.getClip(); //Crea un Clip para reproducir el sonido
            clip.open(audioStream); //Abre el flujo de audio en el Clip
            clip.loop(Clip.LOOP_CONTINUOUSLY); //Configura el audio para que se repita en bucle
            clip.start(); //Inicia la reproducción del audio
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace(); //Manejo de excepciones: imprime cualquier error en la consola
        }
    }

    /**
     * Método para detener la reproducción del audio.
     */
    public void stop() {
        if (clip != null && clip.isRunning()) { //Verifica si hay un clip en ejecución
            clip.stop(); //Detiene la reproducción del audio
            clip.close(); //Cierra el clip para liberar recursos
        }
    }
}
