package org.yisus.ia.model;

import java.util.*;

/**
 * Clase que representa un conjunto de datos para el algoritmo KNN.
 * Contiene una coleccion de DataPoints, EL nombre de la caracteristicas y la etiqueta
 * Contiene métodos para manipularlos.
 */
public class Dataset {
    private List<DataPoint> dataPoints;
    private String[] featureNames;        // Nombres de las características (opcional)
    private String targetName;            // Nombre de la variable objetivo (opcional)

    /**
     * Constructor para crear un Dataset vacío.
     */
    public Dataset() {
        dataPoints = new ArrayList<>();
    }

    /**
     * Constructor para crear un Dataset con una lista de DataPoints.
     *
     * @param dataPoints Lista de puntos de datos
     */
    public Dataset(List<DataPoint> dataPoints) {
        this.dataPoints = new ArrayList<>(dataPoints);
    }

    /**
     * Constructor para crear un Dataset con una lista de DataPoints y nombres de características.
     *
     * @param dataPoints Lista de puntos de datos
     * @param featureNames Nombres de las características
     * @param targetName Nombre de la variable objetivo
     */
    public Dataset(List<DataPoint> dataPoints, String[] featureNames, String targetName) {
        this.dataPoints = new ArrayList<>(dataPoints);
        this.featureNames = featureNames;
        this.targetName = targetName;
    }

    /**
     * Añade un punto de datos al conjunto.
     *
     * @param dataPoint Punto de datos a añadir
     */
    public void add(DataPoint dataPoint) {
        dataPoints.add(dataPoint);
    }

    /**
     * Añade una lista de puntos de datos al conjunto.
     *
     * @param newDataPoints Lista de puntos de datos a añadir
     */
    public void addAll(List<DataPoint> newDataPoints) {
        dataPoints.addAll(newDataPoints);
    }

    /**
     * Elimina un punto de datos del conjunto.
     *
     * @param dataPoint Punto de datos a eliminar
     * @return true si se eliminó con éxito, false en caso contrario
     */
    public boolean remove(DataPoint dataPoint) {
        return dataPoints.remove(dataPoint);
    }

    /**
     * Obtiene un punto de datos por su índice.
     *
     * @param index Índice del punto de datos
     * @return Punto de datos en el índice especificado
     */
    public DataPoint get(int index) {
        return dataPoints.get(index);
    }

    /**
     * Obtiene todos los puntos de datos.
     *
     * @return Lista de todos los puntos de datos
     */
    public List<DataPoint> getDataPoints() {
        return Collections.unmodifiableList(dataPoints);
    }

    /**
     * Devuelve una matriz de características de todos los puntos de datos.
     *
     * @return Matriz de características
     */
    public List<HashMap<String,Double>> getFeatures() {
        if (dataPoints.isEmpty()) {
            return new ArrayList<>();
        }

        int numInstances = dataPoints.size();
        int numFeatures = dataPoints.get(0).getFeatureCount();
        List<HashMap<String,Double>> features = new ArrayList<>();

        for (int i = 0; i < numInstances; i++) {
            features.add( dataPoints.get(i).getFeatures());
        }

        return features;
    }

    /**
     * Devuelve un array con las etiquetas de todos los puntos de datos.
     *
     * @return Array de etiquetas
     */
    public Object[] getLabels() {
        Object[] labels = new Object[dataPoints.size()];
        for (int i = 0; i < dataPoints.size(); i++) {
            labels[i] = dataPoints.get(i).getLabel();
        }
        return labels;
    }

    /**
     * Obtiene el tamaño del conjunto de datos.
     *
     * @return Número de puntos de datos
     */
    public int size() {
        return dataPoints.size();
    }

    /**
     * Comprueba si el conjunto de datos está vacío.
     *
     * @return true si está vacío, false en caso contrario
     */
    public boolean isEmpty() {
        return dataPoints.isEmpty();
    }

    /**
     * Establece los nombres de las características.
     *
     * @param featureNames Nombres de las características
     */
    public void setFeatureNames(String[] featureNames) {
        this.featureNames = featureNames;
    }

    /**
     * Obtiene los nombres de las características.
     *
     * @return Nombres de las características
     */
    public String[] getFeatureNames() {
        return featureNames;
    }

    /**
     * Establece el nombre de la variable objetivo.
     *
     * @param targetName Nombre de la variable objetivo
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    /**
     * Obtiene el nombre de la variable objetivo.
     *
     * @return Nombre de la variable objetivo
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Divide el conjunto de datos en conjuntos de entrenamiento y prueba.
     *
     * @param trainingRatio Proporción para el conjunto de entrenamiento (entre 0 y 1)
     * @return Array con dos conjuntos de datos [training, testing]
     */
    public Dataset[] split(double trainingRatio) {
        if (trainingRatio <= 0 || trainingRatio >= 1) {
            throw new IllegalArgumentException("La proporción de entrenamiento debe estar entre 0 y 1");
        }

        // Mezclar los datos para evitar sesgos
        List<DataPoint> shuffledData = new ArrayList<>(dataPoints);
        Collections.shuffle(shuffledData, new Random(42)); // Semilla para reproducibilidad

        int trainingSize = (int) Math.round(shuffledData.size() * trainingRatio);

        List<DataPoint> trainingData = shuffledData.subList(0, trainingSize);
        List<DataPoint> testingData = shuffledData.subList(trainingSize, shuffledData.size());

        Dataset trainingSet = new Dataset(trainingData, featureNames, targetName);
        Dataset testingSet = new Dataset(testingData, featureNames, targetName);

        return new Dataset[] {trainingSet, testingSet};
    }

    /**
     * Crea subconjuntos para validación cruzada.
     *
     * @param folds Número de subconjuntos
     * @return Array de subconjuntos
     */
    public Dataset[] createFolds(int folds) {
        if (folds <= 1) {
            throw new IllegalArgumentException("El número de subconjuntos debe ser mayor que 1");
        }

        // Mezclar los datos para evitar sesgos
        List<DataPoint> shuffledData = new ArrayList<>(dataPoints);
        Collections.shuffle(shuffledData, new Random(42)); // Semilla para reproducibilidad

        Dataset[] foldDatasets = new Dataset[folds];
        int foldSize = shuffledData.size() / folds;

        for (int i = 0; i < folds; i++) {
            int start = i * foldSize;
            int end = (i == folds - 1) ? shuffledData.size() : (i + 1) * foldSize;

            List<DataPoint> foldData = shuffledData.subList(start, end);
            foldDatasets[i] = new Dataset(foldData, featureNames, targetName);
        }

        return foldDatasets;
    }

    /**
     * Crea un conjunto de datos de entrenamiento y prueba para validación cruzada.
     *
     * @param folds Número total de subconjuntos
     * @param testFoldIndex Índice del subconjunto de prueba
     * @return Array con dos conjuntos de datos [training, testing]
     */
    public Dataset[] createCrossValidationSets(int folds, int testFoldIndex) {
        if (testFoldIndex < 0 || testFoldIndex >= folds) {
            throw new IllegalArgumentException("Índice de subconjunto de prueba fuera de rango");
        }

        Dataset[] allFolds = createFolds(folds);

        List<DataPoint> trainingData = new ArrayList<>();
        List<DataPoint> testingData = new ArrayList<>(allFolds[testFoldIndex].getDataPoints());

        for (int i = 0; i < folds; i++) {
            if (i != testFoldIndex) {
                trainingData.addAll(allFolds[i].getDataPoints());
            }
        }

        Dataset trainingSet = new Dataset(trainingData, featureNames, targetName);
        Dataset testingSet = new Dataset(testingData, featureNames, targetName);

        return new Dataset[] {trainingSet, testingSet};
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dataset [size=").append(size()).append(", features=");

        if (isEmpty()) {
            sb.append("none");
        } else {
            sb.append(dataPoints.get(0).getFeatureCount());
        }

        if (featureNames != null) {
            sb.append(", featureNames=[");
            for (int i = 0; i < featureNames.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(featureNames[i]);
            }
            sb.append("]");
        }

        if (targetName != null) {
            sb.append(", targetName=").append(targetName);
        }

        sb.append("]");
        return sb.toString();
    }
}