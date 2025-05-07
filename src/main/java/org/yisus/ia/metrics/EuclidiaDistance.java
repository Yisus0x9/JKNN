package org.yisus.ia.metrics;

import java.util.HashMap;
import java.util.Set;

/**
 * Implementación de la distancia euclídea.
 * La distancia euclídea es la "distancia ordinaria" entre dos puntos en un espacio euclídeo,
 * calculada mediante el teorema de Pitágoras.
 */
public class EuclidiaDistance implements DistanceMetrics {

    @Override
    public double calculate(HashMap<String, Double> features1, HashMap<String, Double> features2) {
        // Verificar que ambos vectores tengan características
        if (features1 == null || features2 == null) {
            throw new IllegalArgumentException("Los vectores de características no pueden ser nulos");
        }

        // Obtener el conjunto de todas las características
        Set<String> allFeatures = features1.keySet();
        // Verificar que ambos vectores tengan las mismas características
        if (!features2.keySet().equals(allFeatures)) {
            throw new IllegalArgumentException("Los vectores deben tener las mismas características");
        }
        double sumSquaredDiff = 0.0;

        // Calcular la suma de las diferencias al cuadrado
        for (String feature : allFeatures) {
            double diff = features1.get(feature) - features2.get(feature);
            sumSquaredDiff += diff * diff;
        }

        // Calcular la raíz cuadrada de la suma
        return Math.sqrt(sumSquaredDiff);
    }
    @Override
    public String getName() {
        return "Euclidean";
    }
}