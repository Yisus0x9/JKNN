package org.yisus.ia.utils;

import org.yisus.ia.model.DataPoint;
import org.yisus.ia.model.Dataset;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Utilidad para cargar datos desde diferentes fuentes.
 * Esta clase sigue el patrón Singleton para asegurar una única instancia.
 */
public class DataLoader {
    private static final DataLoader instance = new DataLoader();

    // Constructor privado para implementar patrón Singleton
    private DataLoader() {}

    /**
     * Obtiene la instancia única de DataLoader.
     *
     * @return Instancia de DataLoader
     */
    public static DataLoader getInstance() {
        return instance;
    }

    /**
     * Carga datos desde un archivo CSV.
     *
     * @param filename Ruta del archivo CSV
     * @param hasHeader Si el archivo tiene encabezado
     * @param labelIndex Índice de la columna que contiene la etiqueta (-1 para la última columna)
     * @return Dataset cargado
     * @throws IOException Si hay un error al leer el archivo
     */
    public Dataset fromCSV(String filename, boolean hasHeader, int labelIndex) throws IOException {
        List<DataPoint> dataPoints = new ArrayList<>();
        String[] featureNames = null;
        String targetName = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                // Procesar encabezado si existe
                if (lineNumber == 0 && hasHeader) {
                    String[] headers = line.split(",");
                    featureNames = new String[headers.length - 1]; // Excluir la columna de etiqueta

                    int featureIndex = 0;
                    for (int i = 0; i < headers.length; i++) {
                        if (i != labelIndex) {
                            featureNames[featureIndex++] = headers[i].trim();
                        } else {
                            targetName = headers[i].trim();
                        }
                    }
                    lineNumber++;
                    continue;
                }

                // Procesar datos
                String[] values = line.split(",");
                int actualLabelIndex = (labelIndex == -1) ? values.length - 1 : labelIndex;

                // Validar que haya suficientes columnas
                if (values.length <= actualLabelIndex) {
                    throw new IOException("Línea " + lineNumber + " no tiene suficientes columnas");
                }

                // Extraer características y etiqueta
                HashMap<String,Double> features = new HashMap<>(); // Excluir la columna de etiqueta
                int featureIndex = 0;
                Object label = null;

                for (int i = 0; i < values.length; i++) {
                    if (i != actualLabelIndex) {
                        try {
                            assert featureNames != null;
                            features.put(featureNames[i], Double.parseDouble(values[i].trim()));
                        } catch (NumberFormatException e) {
                            throw new IOException("Error al convertir a número en línea " + lineNumber +
                                    ", columna " + i + ": " + values[i]);
                        }
                    } else {
                        label = values[i].trim();
                    }
                }

                dataPoints.add(new DataPoint(features, label, lineNumber));
                lineNumber++;
            }
        }

        return new Dataset(dataPoints, featureNames, targetName);
    }

    /**
     * Carga datos desde un archivo CSV con la etiqueta en la última columna.
     *
     * @param filename Ruta del archivo CSV
     * @param hasHeader Si el archivo tiene encabezado
     * @return Dataset cargado
     * @throws IOException Si hay un error al leer el archivo
     */
    public Dataset fromCSV(String filename, boolean hasHeader) throws IOException {
        return fromCSV(filename, hasHeader, -1);
    }

    /**
     * Carga datos desde un archivo CSV con la etiqueta en la última columna y asumiendo que tiene encabezado.
     *
     * @param filename Ruta del archivo CSV
     * @return Dataset cargado
     * @throws IOException Si hay un error al leer el archivo
     */
    public static Dataset fromCSV(String filename) throws IOException {
        return getInstance().fromCSV(filename, true, -1);
    }

    /**
     * Carga datos desde una matriz de características y un vector de etiquetas.
     *
     * @param features Matriz de características
     * @param labels Vector de etiquetas
     * @return Dataset cargado
     */
    public Dataset fromArrays(List<HashMap<String,Double>> features, Object[] labels) {
        if (features.size() != labels.length) {
            throw new IllegalArgumentException("El número de ejemplos en features y labels debe ser igual");
        }

        List<DataPoint> dataPoints = new ArrayList<>();

        for (int i = 0; i < features.size(); i++) {
            dataPoints.add(new DataPoint(features.get(i), labels[i], i));
        }

        return new Dataset(dataPoints);
    }

    /**
     * Codifica etiquetas categóricas como valores numéricos.
     *
     * @param dataset Dataset con etiquetas categóricas
     * @return Mapa con las correspondencias entre etiquetas originales y numéricas
     */
    public Map<Object, Integer> encodeCategoricalLabels(Dataset dataset) {
        Map<Object, Integer> labelEncoder = new HashMap<>();
        int nextValue = 0;

        List<DataPoint> dataPoints = dataset.getDataPoints();
        for (DataPoint dataPoint : dataPoints) {
            Object originalLabel = dataPoint.getLabel();

            if (!labelEncoder.containsKey(originalLabel)) {
                labelEncoder.put(originalLabel, nextValue++);
            }

            dataPoint.setLabel(labelEncoder.get(originalLabel));
        }

        return labelEncoder;
    }

    /**
     * Lee datos directamente desde archivos GZIP (para usar con archivos MNIST)
     *
     * @param gzipFilePath Ruta al archivo GZIP
     * @return InputStream para leer los datos descomprimidos
     * @throws IOException Si hay error al leer el archivo
     */
    public static InputStream getInputStreamFromGZIP(String gzipFilePath) throws IOException {
        return new FileInputStream(gzipFilePath);
        //return new GZIPInputStream(fis);
    }
}