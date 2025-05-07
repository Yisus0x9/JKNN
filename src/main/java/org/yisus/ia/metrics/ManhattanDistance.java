package org.yisus.ia.metrics;

import java.util.HashMap;
import java.util.Set;

/**
 * Implementación de la distancia de Manhattan.
 * La distancia de Manhattan es la suma de las diferencias absolutas de sus coordenadas,
 * también conocida como distancia de la ciudad o distancia del taxista.
 */
public class ManhattanDistance implements DistanceMetrics {

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

        double sumAbsDiff = 0.0;

        // Calcular la suma de las diferencias absolutas
        for (String feature : allFeatures) {
            double diff = Math.abs(features1.get(feature) - features2.get(feature));
            sumAbsDiff += diff;
        }

        return sumAbsDiff;
    }

    @Override
    public String getName() {
        return "Manhattan";
    }
}