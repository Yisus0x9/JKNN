package org.yisus.ia.evaluation;

import org.yisus.ia.core.JKNNClassifier;
import org.yisus.ia.model.Dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementación de validación cruzada para evaluar modelos KNN.
 * La validación cruzada divide el conjunto de datos en k subconjuntos (folds)
 * y entrena/evalúa el modelo k veces, cada vez usando un subconjunto diferente como conjunto de prueba.
 */
public class CrossValidation {
    private final int folds;
    private final Dataset dataset;
    private final JKNNClassifier classifier;

    /**
     * Constructor para validación cruzada.
     *
     * @param dataset Conjunto de datos completo
     * @param classifier Clasificador a evaluar
     * @param folds Número de subconjuntos/iteraciones
     */
    public CrossValidation(Dataset dataset, JKNNClassifier classifier, int folds) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de datos no puede ser nulo o vacío");
        }
        if (classifier == null) {
            throw new IllegalArgumentException("El clasificador no puede ser nulo");
        }
        if (folds < 2) {
            throw new IllegalArgumentException("El número de folds debe ser al menos 2");
        }
        if (folds > dataset.size()) {
            throw new IllegalArgumentException("El número de folds no puede ser mayor que el tamaño del conjunto de datos");
        }

        this.dataset = dataset;
        this.classifier = classifier;
        this.folds = folds;
    }

    /**
     * Ejecuta la validación cruzada.
     *
     * @return Lista de métricas de rendimiento para cada iteración
     */
    public List<PerformanceMetrics> evaluate() {
        List<PerformanceMetrics> results = new ArrayList<>();

        // Crear los folds para validación cruzada
        Dataset[] foldDatasets = dataset.createFolds(folds);

        // Para cada fold como conjunto de prueba
        for (int i = 0; i < folds; i++) {
            // Crear conjuntos de entrenamiento y prueba
            Dataset[] sets = dataset.createCrossValidationSets(folds, i);
            Dataset trainingSet = sets[0];
            Dataset testingSet = sets[1];

            // Entrenar el clasificador
            classifier.train(trainingSet);

            // Evaluar el clasificador
            Object[] actualLabels = testingSet.getLabels();
            Object[] predictedLabels = classifier.predict(testingSet);

            // Crear matriz de confusión
            ConfusionMatrix confusionMatrix = new ConfusionMatrix(List.of(actualLabels), List.of(predictedLabels));

            // Calcular métricas de rendimiento
            PerformanceMetrics metrics = new PerformanceMetrics(confusionMatrix);
            results.add(metrics);
        }

        return results;
    }

    /**
     * Calcula y devuelve el rendimiento promedio de la validación cruzada.
     *
     * @return Métricas de rendimiento promedio
     */
    public PerformanceMetrics getAveragePerformance() {
        List<PerformanceMetrics> results = evaluate();
        // Calcular medias
        double avgAccuracy = 0.0;
        double avgPrecision = 0.0;
        double avgRecall = 0.0;
        double avgF1Score = 0.0;

        for (PerformanceMetrics metrics : results) {
            avgAccuracy += metrics.getAccuracy();
            avgPrecision += metrics.getMacroAveragePrecision();
            avgRecall += metrics.getMacroAverageRecall();
            avgF1Score += metrics.getMacroAverageF1Score();
        }
        int count = results.size();
        avgAccuracy /= count;
        avgPrecision /= count;
        avgRecall /= count;
        avgF1Score /= count;
        // Crear un objeto de métricas promedio
        return new PerformanceMetrics(avgAccuracy, avgPrecision, avgRecall, avgF1Score);
    }

    /**
     * Devuelve el número de folds utilizados.
     *
     * @return Número de folds
     */
    public int getFolds() {
        return folds;
    }

    /**
     * Devuelve el conjunto de datos utilizado.
     *
     * @return Conjunto de datos
     */
    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Devuelve el clasificador utilizado.
     *
     * @return Clasificador
     */
    public JKNNClassifier getClassifier() {
        return classifier;
    }
}