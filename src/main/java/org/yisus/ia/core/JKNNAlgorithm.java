package org.yisus.ia.core;

import org.yisus.ia.metrics.DistanceMetrics;
import org.yisus.ia.metrics.EuclidiaDistance;
import org.yisus.ia.model.DataPoint;
import org.yisus.ia.model.Dataset;
import org.yisus.ia.model.KNNResult;
import org.yisus.ia.model.Neighbor;

import java.util.*;

/**
 * Implementación del algoritmo K Nearest Neighbors (KNN).
 * Este algoritmo encuentra los k vecinos más cercanos a un punto de datos
 * basándose en una métrica de distancia.
 */
public class JKNNAlgorithm {
    private final Dataset trainingSet;
    private final DistanceMetrics distanceMetric;
    private final int k;

    /**
     * Constructor con parámetros personalizados.
     *
     * @param trainingSet Conjunto de datos de entrenamiento
     * @param k Número de vecinos a considerar
     * @param distanceMetric Métrica de distancia a utilizar
     */
    public JKNNAlgorithm(Dataset trainingSet, int k, DistanceMetrics distanceMetric) {
        if (trainingSet == null || trainingSet.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de entrenamiento no puede estar vacío");
        }
        if (k <= 0) {
            throw new IllegalArgumentException("k debe ser mayor que 0");
        }
        if (k > trainingSet.size()) {
            throw new IllegalArgumentException("k no puede ser mayor que el tamaño del conjunto de entrenamiento");
        }
        this.trainingSet = trainingSet;
        this.k = k;
        this.distanceMetric = distanceMetric;
    }

    /**
     * Constructor con distancia euclídea por defecto.
     *
     * @param trainingSet Conjunto de datos de entrenamiento
     * @param k Número de vecinos a considerar
     */
    public JKNNAlgorithm(Dataset trainingSet, int k) {
        this(trainingSet, k, new EuclidiaDistance());
    }

    /**
     * Busca los k vecinos más cercanos a un punto de datos.
     *
     * @param testPoint Punto de datos para el que se buscan vecinos
     * @return Resultado con los k vecinos más cercanos
     */
    public KNNResult findNearestNeighbors(DataPoint testPoint) {
        List<Neighbor> neighbors = new ArrayList<>();

        // Calcular distancias a todos los puntos de entrenamiento
        for (DataPoint trainPoint : trainingSet.getDataPoints()) {
            double distance = calculateDistance(testPoint, trainPoint);
            neighbors.add(new Neighbor(trainPoint, distance));
        }
        // Ordenar por distancia (menor a mayor)
        neighbors.sort(Comparator.comparingDouble(Neighbor::getDistance));

        // Tomar los k primeros vecinos
        List<Neighbor> kNeighbors;
        if (neighbors.size() > k) {
            kNeighbors = neighbors.subList(0, k);
        } else {
            kNeighbors = neighbors;
        }

        return new KNNResult(testPoint, kNeighbors);
    }

    /**
     * Calcula la distancia entre dos puntos de datos.
     *
     * @param point1 Primer punto de datos
     * @param point2 Segundo punto de datos
     * @return Distancia entre los puntos
     */
    private double calculateDistance(DataPoint point1, DataPoint point2) {
        return distanceMetric.calculate(point1.getFeatures(), point2.getFeatures());
    }

    /**
     * Predice la etiqueta para un punto de datos basado en los vecinos más cercanos.
     *
     * @param testPoint Punto de datos para el que se predice la etiqueta
     * @return Etiqueta predicha
     */
    public Object predict(DataPoint testPoint) {
        KNNResult result = findNearestNeighbors(testPoint);
        return getMajorityVote(result.getNeighbors());
    }

    /**
     * Obtiene la etiqueta mayoritaria entre los vecinos.
     *
     * @param neighbors Lista de vecinos
     * @return Etiqueta mayoritaria
     */
    private Object getMajorityVote(List<Neighbor> neighbors) {
        Map<Object, Integer> voteCounts = new HashMap<>();

        // Contar votos para cada etiqueta
        for (Neighbor neighbor : neighbors) {
            Object label = neighbor.getDataPoint().getLabel();
            voteCounts.put(label, voteCounts.getOrDefault(label, 0) + 1);
        }

        // Encontrar la etiqueta con más votos
        Object majorityLabel = null;
        int maxVotes = 0;

        for (Map.Entry<Object, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                majorityLabel = entry.getKey();
            }
        }

        return majorityLabel;
    }

    /**
     * Predice las etiquetas para un conjunto de datos de prueba.
     *
     * @param testingSet Conjunto de datos de prueba
     * @return Array con las etiquetas predichas
     */
    public Object[] predictAll(Dataset testingSet) {
        Object[] predictions = new Object[testingSet.size()];

        for (int i = 0; i < testingSet.size(); i++) {
            predictions[i] = predict(testingSet.get(i));
        }

        return predictions;
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
     * Obtiene el conjunto de entrenamiento.
     *
     * @return Conjunto de entrenamiento
     */
    public Dataset getTrainingSet() {
        return trainingSet;
    }
}