package org.yisus.ia.core;

import org.yisus.ia.metrics.DistanceFactory;
import org.yisus.ia.metrics.DistanceMetrics;
import org.yisus.ia.model.DataPoint;
import org.yisus.ia.model.Dataset;

import java.util.HashMap;

/**
 * Clasificador basado en el algoritmo KNN.
 * Esta clase proporciona una interfaz más sencilla para utilizar el algoritmo KNN
 * para tareas de clasificación.
 */
public class JKNNClassifier {
    private JKNNAlgorithm knnAlgorithm;
    private int k;
    private DistanceMetrics distanceMetric;
    private Dataset trainingSet;
    private boolean isTrained;

    public JKNNClassifier() {
        this.k = 3; // Valor por defecto
        this.distanceMetric = DistanceFactory.createEuclidianDistance();
        this.isTrained = false;
    }

    /**
     * Constructor con parámetros personalizados.
     *
     * @param k Número de vecinos a considerar
     * @param distanceMetricName Nombre de la métrica de distancia a utilizar
     */
    public JKNNClassifier(int k, String distanceMetricName) {
        this.k = k;
        this.distanceMetric = DistanceFactory.createDistance(distanceMetricName);
        this.isTrained = false;
    }

    /**
     * Constructor con parámetros personalizados.
     *
     * @param k Número de vecinos a considerar
     * @param distanceMetric Métrica de distancia a utilizar
     */
    public JKNNClassifier(int k, DistanceMetrics distanceMetric) {
        this.k = k;
        this.distanceMetric = distanceMetric;
        this.isTrained = false;
    }

    /**
     * Entrena el clasificador con un conjunto de datos.
     *
     * @param dataset Conjunto de datos de entrenamiento
     */
    public void train(Dataset dataset) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de entrenamiento no puede estar vacío");
        }

        this.trainingSet = dataset;
        this.knnAlgorithm = new JKNNAlgorithm(dataset, k, distanceMetric);
        this.isTrained = true;
    }

    /**
     * Predice la etiqueta para un punto de datos.
     *
     * @param features Características del punto de datos
     * @return Etiqueta predicha
     */
    public Object predict(HashMap<String, Double> features) {
        checkTrained();
        DataPoint testPoint = new DataPoint(features, null);
        return knnAlgorithm.predict(testPoint);
    }

    /**
     * Predice las etiquetas para un conjunto de datos.
     *
     * @param testingSet Conjunto de datos de prueba
     * @return Array con las etiquetas predichas
     */
    public Object[] predict(Dataset testingSet) {
        checkTrained();
        return knnAlgorithm.predictAll(testingSet);
    }

    /**
     * Verifica si el clasificador ha sido entrenado.
     */
    private void checkTrained() {
        if (!isTrained) {
            throw new IllegalStateException("El clasificador no ha sido entrenado");
        }
    }

    /**
     * Establece el número de vecinos a considerar.
     *
     * @param k Número de vecinos
     */
    public void setK(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k debe ser mayor que 0");
        }
        this.k = k;
        // Si ya está entrenado, actualizar el algoritmo KNN
        if (isTrained) {
            this.knnAlgorithm = new JKNNAlgorithm(trainingSet, k, distanceMetric);
        }
    }

    /**
     * Establece la métrica de distancia a utilizar.
     *
     * @param distanceMetric Métrica de distancia
     */
    public void setDistanceMetric(DistanceMetrics distanceMetric) {
        this.distanceMetric = distanceMetric;
        // Si ya está entrenado, actualizar el algoritmo KNN
        if (isTrained) {
            this.knnAlgorithm = new JKNNAlgorithm(trainingSet, k, distanceMetric);
        }
    }

    /**
     * Establece la métrica de distancia por su nombre.
     *
     * @param distanceMetricName Nombre de la métrica de distancia
     */
    public void setDistanceMetric(String distanceMetricName) {
        setDistanceMetric(DistanceFactory.createDistance(distanceMetricName));
    }

    /**
     * Obtiene el valor de k utilizado.
     *
     * @return Valor de k
     */
    public int getK() {
        return k;
    }

    /**
     * Obtiene la métrica de distancia utilizada.
     *
     * @return Métrica de distancia
     */
    public DistanceMetrics getDistanceMetric() {
        return distanceMetric;
    }

    /**
     * Comprueba si el clasificador ha sido entrenado.
     *
     * @return true si está entrenado, false en caso contrario
     */
    public boolean isTrained() {
        return isTrained;
    }

    /**
     * Obtiene el conjunto de entrenamiento.
     *
     * @return Conjunto de entrenamiento
     */
    public Dataset getTrainingSet() {
        return trainingSet;
    }
}