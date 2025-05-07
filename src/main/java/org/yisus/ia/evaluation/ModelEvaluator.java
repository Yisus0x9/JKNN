package org.yisus.ia.evaluation;

import org.yisus.ia.core.JKNNClassifier;
import org.yisus.ia.model.Dataset;

import java.util.List;

/**
 * Clase para evaluar modelos de clasificación KNN.
 * Proporciona métodos para evaluar el rendimiento de un clasificador
 * utilizando diferentes técnicas (hold-out, validación cruzada).
 */
public class ModelEvaluator {
    /**
     * Evalúa un clasificador utilizando el método de hold-out (división en train/test).
     *
     * @param classifier Clasificador a evaluar
     * @param dataset Conjunto de datos completo
     * @param trainingRatio Proporción de datos para entrenamiento (entre 0 y 1)
     * @return Métricas de rendimiento
     */
    public static PerformanceMetrics evaluateWithHoldout(JKNNClassifier classifier, Dataset dataset, double trainingRatio) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de datos no puede ser nulo o vacío");
        }
        if (classifier == null) {
            throw new IllegalArgumentException("El clasificador no puede ser nulo");
        }
        if (trainingRatio <= 0 || trainingRatio >= 1) {
            throw new IllegalArgumentException("La proporción de entrenamiento debe estar entre 0 y 1");
        }

        // Dividir el conjunto de datos
        Dataset[] sets = dataset.split(trainingRatio);
        Dataset trainingSet = sets[0];
        Dataset testingSet = sets[1];

        // Entrenar el clasificador
        classifier.train(trainingSet);

        // Evaluar el clasificador
        Object[] actualLabels = testingSet.getLabels();
        Object[] predictedLabels = classifier.predict(testingSet);

        // Crear matriz de confusión y métricas
        ConfusionMatrix confusionMatrix = new ConfusionMatrix(List.of(actualLabels), List.of(predictedLabels));
        return new PerformanceMetrics(confusionMatrix);
    }

    /**
     * Evalúa un clasificador utilizando validación cruzada.
     *
     * @param classifier Clasificador a evaluar
     * @param dataset Conjunto de datos completo
     * @param folds Número de subconjuntos/iteraciones
     * @return Métricas de rendimiento promedio
     */
    public static PerformanceMetrics evaluateWithCrossValidation(JKNNClassifier classifier, Dataset dataset, int folds) {
        CrossValidation cv = new CrossValidation(dataset, classifier, folds);
        return cv.getAveragePerformance();
    }

    /**
     * Realiza una búsqueda de hiperparámetros para encontrar el mejor valor de k.
     *
     * @param classifier Clasificador base
     * @param dataset Conjunto de datos
     * @param minK Valor mínimo de k a probar
     * @param maxK Valor máximo de k a probar
     * @param folds Número de folds para validación cruzada
     * @return Mejor valor de k encontrado
     */
    public static int findBestK(JKNNClassifier classifier, Dataset dataset, int minK, int maxK, int folds) {
        if (minK < 1 || maxK < minK) {
            throw new IllegalArgumentException("Rango de k inválido");
        }

        double bestScore = -1;
        int bestK = minK;

        for (int k = minK; k <= maxK; k++) {
            // Configurar el clasificador con el valor actual de k
            classifier.setK(k);

            // Evaluar el clasificador
            PerformanceMetrics metrics = evaluateWithCrossValidation(classifier, dataset, folds);
            double score = metrics.getMacroAverageF1Score(); // Usar F1-score como métrica para optimizar

            System.out.println("k = " + k + ", F1-Score = " + score);

            // Actualizar el mejor valor si es necesario
            if (score > bestScore) {
                bestScore = score;
                bestK = k;
            }
        }

        return bestK;
    }
}