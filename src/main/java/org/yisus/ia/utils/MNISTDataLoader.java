package org.yisus.ia.utils;

import org.yisus.ia.model.DataPoint;
import org.yisus.ia.model.Dataset;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utilidad para cargar específicamente el conjunto de datos MNIST.
 * Esta clase extiende la funcionalidad de DataLoader para trabajar con el formato IDX de MNIST.
 */
public class MNISTDataLoader {
    private static final MNISTDataLoader instance = new MNISTDataLoader();

    // Constructor privado para implementar patrón Singleton
    private MNISTDataLoader() {}

    /**
     * Obtiene la instancia única de MNISTDataLoader.
     *
     * @return Instancia de MNISTDataLoader
     */
    public static MNISTDataLoader getInstance() {
        return instance;
    }

    /**
     * Carga el conjunto de datos MNIST completo (imágenes y etiquetas).
     *
     * @param imagesFilePath Ruta al archivo de imágenes IDX
     * @param labelsFilePath Ruta al archivo de etiquetas IDX
     * @return Dataset cargado con las imágenes MNIST y sus etiquetas
     * @throws IOException Si hay error al leer los archivos
     */
    public Dataset loadMNIST(String imagesFilePath, String labelsFilePath) throws IOException {
        // Cargar imágenes y etiquetas
        int[][] images = readMNISTImages(imagesFilePath);
        int[] labels = readMNISTLabels(labelsFilePath);

        if (images.length != labels.length) {
            throw new IOException("Número inconsistente de imágenes y etiquetas");
        }

        // Crear DataPoints a partir de las imágenes y etiquetas
        List<DataPoint> dataPoints = new ArrayList<>();
        String[] featureNames = new String[images[0].length];

        // Generar nombres de características (pixeles)
        for (int i = 0; i < featureNames.length; i++) {
            featureNames[i] = "pixel_" + i;
        }

        // Crear un DataPoint por cada imagen/etiqueta
        for (int i = 0; i < images.length; i++) {
            HashMap<String, Double> features = new HashMap<>();
            for (int j = 0; j < images[i].length; j++) {
                // Normalizar los valores de píxeles a rango [0,1]
                features.put(featureNames[j], images[i][j] / 255.0);
            }
            dataPoints.add(new DataPoint(features, labels[i], i));
        }

        return new Dataset(dataPoints, featureNames, "digit");
    }

    /**
     * Lee las imágenes desde un archivo en formato IDX.
     *
     * @param filePath Ruta al archivo
     * @return Matriz de imágenes (cada fila es una imagen, cada columna un píxel)
     * @throws IOException Si hay error al leer el archivo
     */
    private int[][] readMNISTImages(String filePath) throws IOException {
        try (DataInputStream dataInputStream = new DataInputStream(DataLoader.getInputStreamFromGZIP(filePath))) {
            // Leer cabecera
            int magicNumber = dataInputStream.readInt();
            if (magicNumber != 2051) {
                throw new IOException("Formato de archivo incorrecto, magic number esperado: 2051, encontrado: " + magicNumber);
            }

            int numImages = dataInputStream.readInt();
            int numRows = dataInputStream.readInt();
            int numCols = dataInputStream.readInt();
            int imageSize = numRows * numCols;

            // Leer datos de imágenes
            int[][] images = new int[numImages][imageSize];
            byte[] byteBuffer = new byte[imageSize];

            for (int i = 0; i < numImages; i++) {
                dataInputStream.readFully(byteBuffer);
                for (int j = 0; j < imageSize; j++) {
                    // Convertir byte sin signo (0-255)
                    images[i][j] = byteBuffer[j] & 0xFF;
                }
            }

            return images;
        }
    }

    /**
     * Lee las etiquetas desde un archivo en formato IDX.
     *
     * @param filePath Ruta al archivo
     * @return Array de etiquetas
     * @throws IOException Si hay error al leer el archivo
     */
    private int[] readMNISTLabels(String filePath) throws IOException {
        try (DataInputStream dataInputStream = new DataInputStream(DataLoader.getInputStreamFromGZIP(filePath))) {
            // Leer cabecera
            int magicNumber = dataInputStream.readInt();
            if (magicNumber != 2049) {
                throw new IOException("Formato de archivo incorrecto, magic number esperado: 2049, encontrado: " + magicNumber);
            }

            int numItems = dataInputStream.readInt();
            int[] labels = new int[numItems];

            // Leer etiquetas
            for (int i = 0; i < numItems; i++) {
                labels[i] = dataInputStream.readUnsignedByte();
            }

            return labels;
        }
    }
}