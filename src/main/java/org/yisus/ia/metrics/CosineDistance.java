package org.yisus.ia.metrics;

import java.util.HashMap;
import java.util.Set;

/**
 * Implementación de la distancia coseno.
 * La distancia coseno mide la similitud entre dos vectores no nulos calculando
 * el coseno del ángulo entre ellos.
 */
public class CosineDistance implements DistanceMetrics {

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

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        // Calcular el producto punto y las normas
        for (String feature : allFeatures) {
            double val1 = features1.get(feature);
            double val2 = features2.get(feature);

            dotProduct += val1 * val2;
            norm1 += val1 * val1;
            norm2 += val2 * val2;
        }

        // Calcular las magnitudes
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);

        // Evitar división por cero
        if (norm1 == 0 || norm2 == 0) {
            return 1.0; // Máxima distancia
        }

        // Calcular la similitud coseno
        double cosineSimilarity = dotProduct / (norm1 * norm2);

        // Convertir similitud en distancia (1 - similitud)
        return 1.0 - cosineSimilarity;
    }

    @Override
    public String getName() {
        return "Cosine";
    }
}