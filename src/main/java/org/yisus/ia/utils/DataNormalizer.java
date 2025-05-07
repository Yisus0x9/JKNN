package org.yisus.ia.utils;

import org.yisus.ia.model.DataPoint;
import org.yisus.ia.model.Dataset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utilidad para normalizar datos en un conjunto de datos.
 */
public class DataNormalizer {
    // Mapas para almacenar los valores mínimos y máximos de cada característica
    private final Map<String, Double> minValues;
    private final Map<String, Double> maxValues;
    private boolean isFitted;

    /**
     * Constructor por defecto.
     */
    public DataNormalizer() {
        this.minValues = new HashMap<>();
        this.maxValues = new HashMap<>();
        this.isFitted = false;
    }

    /**
     * Calcula los valores mínimos y máximos para cada característica en el conjunto de datos.
     *
     * @param dataset Conjunto de datos
     */
    public void fit(Dataset dataset) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de datos no puede ser nulo o vacío");
        }

        // Obtener la lista de puntos de datos
        List<DataPoint> dataPoints = dataset.getDataPoints();

        // Inicializar con el primer punto
        DataPoint firstPoint = dataPoints.get(0);
        Set<String> features = firstPoint.getFeatures().keySet();

        for (String feature : features) {
            minValues.put(feature, Double.POSITIVE_INFINITY);
            maxValues.put(feature, Double.NEGATIVE_INFINITY);
        }

        // Calcular min y max para cada característica
        for (DataPoint point : dataPoints) {
            HashMap<String, Double> featureValues = point.getFeatures();

            for (String feature : features) {
                double value = featureValues.get(feature);

                // Actualizar min y max
                if (value < minValues.get(feature)) {
                    minValues.put(feature, value);
                }
                if (value > maxValues.get(feature)) {
                    maxValues.put(feature, value);
                }
            }
        }

        this.isFitted = true;
    }

    /**
     * Normaliza un conjunto de datos utilizando los valores mínimos y máximos calculados previamente.
     *
     * @param dataset Conjunto de datos a normalizar
     * @return Conjunto de datos normalizado
     */
    public Dataset transform(Dataset dataset) {
        if (!isFitted) {
            throw new IllegalStateException("El normalizador debe ser ajustado primero con fit()");
        }

        // Crear una copia del dataset para no modificar el original
        Dataset normalizedDataset = new Dataset();
        normalizedDataset.setFeatureNames(dataset.getFeatureNames());
        normalizedDataset.setTargetName(dataset.getTargetName());

        // Normalizar cada punto de datos
        for (DataPoint point : dataset.getDataPoints()) {
            HashMap<String, Double> normalizedFeatures = new HashMap<>();
            HashMap<String, Double> originalFeatures = point.getFeatures();

            for (String feature : originalFeatures.keySet()) {
                double value = originalFeatures.get(feature);
                double min = minValues.get(feature);
                double max = maxValues.get(feature);

                // Evitar división por cero
                double normalizedValue;
                if (max == min) {
                    normalizedValue = 0.5; // Valor arbitrario en caso de que todos los valores sean iguales
                } else {
                    normalizedValue = (value - min) / (max - min);
                }

                normalizedFeatures.put(feature, normalizedValue);
            }

            // Crear un nuevo punto de datos normalizado
            DataPoint normalizedPoint = new DataPoint(normalizedFeatures, point.getLabel(), point.getId());
            normalizedDataset.add(normalizedPoint);
        }

        return normalizedDataset;
    }

    /**
     * Ajusta y normaliza un conjunto de datos en un solo paso.
     *
     * @param dataset Conjunto de datos a normalizar
     * @return Conjunto de datos normalizado
     */
    public Dataset fitTransform(Dataset dataset) {
        // Primero ajustamos el normalizador con los datos
        fit(dataset);
        // Luego normalizamos el conjunto de datos
        return transform(dataset);
    }

    /**
     * Obtiene los valores mínimos calculados para cada característica.
     *
     * @return Mapa con los valores mínimos por característica
     */
    public Map<String, Double> getMinValues() {
        if (!isFitted) {
            throw new IllegalStateException("El normalizador debe ser ajustado primero con fit()");
        }
        return new HashMap<>(minValues);
    }

    /**
     * Obtiene los valores máximos calculados para cada característica.
     *
     * @return Mapa con los valores máximos por característica
     */
    public Map<String, Double> getMaxValues() {
        if (!isFitted) {
            throw new IllegalStateException("El normalizador debe ser ajustado primero con fit()");
        }
        return new HashMap<>(maxValues);
    }

    /**
     * Verifica si el normalizador ha sido ajustado.
     *
     * @return true si el normalizador está ajustado, false en caso contrario
     */
    public boolean isFitted() {
        return isFitted;
    }

    /**
     * Normaliza un único punto de datos utilizando los valores mínimos y máximos calculados.
     * Útil para normalizar nuevos datos de predicción.
     *
     * @param dataPoint Punto de datos a normalizar
     * @return Punto de datos normalizado
     */
    public DataPoint normalizePoint(DataPoint dataPoint) {
        if (!isFitted) {
            throw new IllegalStateException("El normalizador debe ser ajustado primero con fit()");
        }

        HashMap<String, Double> normalizedFeatures = new HashMap<>();
        HashMap<String, Double> originalFeatures = dataPoint.getFeatures();

        for (String feature : originalFeatures.keySet()) {
            // Verificar que la característica esté en nuestros valores calculados
            if (!minValues.containsKey(feature) || !maxValues.containsKey(feature)) {
                throw new IllegalArgumentException("La característica " + feature + " no estaba presente en los datos de entrenamiento");
            }

            double value = originalFeatures.get(feature);
            double min = minValues.get(feature);
            double max = maxValues.get(feature);

            // Evitar división por cero
            double normalizedValue;
            if (max == min) {
                normalizedValue = 0.5;
            } else {
                normalizedValue = (value - min) / (max - min);
            }

            normalizedFeatures.put(feature, normalizedValue);
        }

        return new DataPoint(normalizedFeatures, dataPoint.getLabel(), dataPoint.getId());
    }

    /**
     * Desnormaliza un valor específico para una característica.
     * Útil para convertir valores normalizados de vuelta a su escala original.
     *
     * @param feature Nombre de la característica
     * @param normalizedValue Valor normalizado
     * @return Valor en la escala original
     */
    public double denormalize(String feature, double normalizedValue) {
        if (!isFitted) {
            throw new IllegalStateException("El normalizador debe ser ajustado primero con fit()");
        }

        if (!minValues.containsKey(feature) || !maxValues.containsKey(feature)) {
            throw new IllegalArgumentException("La característica " + feature + " no está presente en los datos");
        }

        double min = minValues.get(feature);
        double max = maxValues.get(feature);

        // Fórmula inversa de la normalización
        return normalizedValue * (max - min) + min;
    }

    /**
     * Reinicia el normalizador, eliminando todos los valores calculados.
     */
    public void reset() {
        minValues.clear();
        maxValues.clear();
        isFitted = false;
    }
}