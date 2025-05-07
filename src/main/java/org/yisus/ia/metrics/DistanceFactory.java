package org.yisus.ia.metrics;


public class DistanceFactory {

    public static DistanceMetrics createDistance(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la métrica no puede ser nulo o vacío");
        }

        String normalizedName = name.trim().toLowerCase();

        switch (normalizedName) {
            case "euclidean":
                return new EuclidiaDistance();
            case "manhattan":
                return new ManhattanDistance();
            case "cosine":
                return new CosineDistance();
            default:
                throw new IllegalArgumentException("Métrica de distancia no reconocida: " + name);
        }
    }

    public static DistanceMetrics createEuclidianDistance() {
        return new EuclidiaDistance();
    }


    public static DistanceMetrics createManhattanDistance() {
        return new ManhattanDistance();
    }


    public static DistanceMetrics createCosineDistance() {
        return new CosineDistance();
    }
}