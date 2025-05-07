package org.yisus.ia.evaluation;

import java.util.*;

/**
 * Clase que implementa una matriz de confusión para evaluar el rendimiento de un clasificador.
 * La matriz de confusión muestra la relación entre las etiquetas reales y las predichas.
 */
public class ConfusionMatrix {
    // Mapa de matriz de confusión: [etiqueta real][etiqueta predicha] -> conteo
    private final Map<Object, Map<Object, Integer>> matrix;

    private final Set<Object> classLabels;

    /**
     * Constructor que crea una matriz de confusión basada en etiquetas reales y predichas.
     *
     * @param actualLabels Lista de etiquetas reales
     * @param predictedLabels Lista de etiquetas predichas
     * @throws IllegalArgumentException Si las listas tienen diferentes tamaños
     */
    public ConfusionMatrix(List<Object> actualLabels, List<Object> predictedLabels) {
        if (actualLabels.size() != predictedLabels.size()) {
            throw new IllegalArgumentException("Las listas de etiquetas reales y predichas deben tener el mismo tamaño");
        }

        this.matrix = new HashMap<>();
        this.classLabels = new HashSet<>();

        // Recolectar todas las etiquetas únicas
        classLabels.addAll(actualLabels);
        classLabels.addAll(predictedLabels);

        // Inicializar la matriz con ceros
        for (Object actual : classLabels) {
            Map<Object, Integer> row = new HashMap<>();
            for (Object predicted : classLabels) {
                row.put(predicted, 0);
            }
            matrix.put(actual, row);
        }

        // Rellenar la matriz
        for (int i = 0; i < actualLabels.size(); i++) {
            Object actual = actualLabels.get(i);
            Object predicted = predictedLabels.get(i);

            // Incrementar el conteo
            Map<Object, Integer> row = matrix.get(actual);
            row.put(predicted, row.get(predicted) + 1);
        }
    }

    /**
     * Calcula la precisión para una clase específica.
     * Precisión = TP / (TP + FP)
     *
     * @param classLabel Etiqueta de clase
     * @return Precisión para la clase
     */
    public double getPrecision(Object classLabel) {
        if (!classLabels.contains(classLabel)) {
            throw new IllegalArgumentException("Etiqueta de clase no encontrada: " + classLabel);
        }

        // True Positives: cuando la clase fue predicha correctamente
        int truePositives = matrix.getOrDefault(classLabel, Collections.emptyMap()).getOrDefault(classLabel, 0);

        // False Positives: suma de todos los casos en que se predijo esta clase pero era incorrecta
        int falsePositives = 0;
        for (Object actual : classLabels) {
            if (!actual.equals(classLabel)) {
                falsePositives += matrix.getOrDefault(actual, Collections.emptyMap()).getOrDefault(classLabel, 0);
            }
        }

        // Evitar división por cero
        if (truePositives + falsePositives == 0) {
            return 0.0;
        }

        return (double) truePositives / (truePositives + falsePositives);
    }

    /**
     * Calcula el recall para una clase específica.
     * Recall = TP / (TP + FN)
     *
     * @param classLabel Etiqueta de clase
     * @return Recall para la clase
     */
    public double getRecall(Object classLabel) {
        if (!classLabels.contains(classLabel)) {
            throw new IllegalArgumentException("Etiqueta de clase no encontrada: " + classLabel);
        }

        // True Positives: cuando la clase fue predicha correctamente
        int truePositives = matrix.getOrDefault(classLabel, Collections.emptyMap()).getOrDefault(classLabel, 0);

        // False Negatives: suma de todos los casos en que era esta clase pero se predijo incorrectamente
        int falseNegatives = 0;
        Map<Object, Integer> row = matrix.getOrDefault(classLabel, Collections.emptyMap());
        for (Object predicted : classLabels) {
            if (!predicted.equals(classLabel)) {
                falseNegatives += row.getOrDefault(predicted, 0);
            }
        }

        // Evitar división por cero
        if (truePositives + falseNegatives == 0) {
            return 0.0;
        }

        return (double) truePositives / (truePositives + falseNegatives);
    }

    /**
     * Calcula el F1-Score para una clase específica.
     * F1-Score = 2 * (Precision * Recall) / (Precision + Recall)
     *
     * @param classLabel Etiqueta de clase
     * @return F1-Score para la clase
     */
    public double getF1Score(Object classLabel) {
        double precision = getPrecision(classLabel);
        double recall = getRecall(classLabel);

        // Evitar división por cero
        if (precision + recall == 0) {
            return 0.0;
        }

        return 2 * (precision * recall) / (precision + recall);
    }

    /**
     * Calcula la exactitud general (accuracy) del clasificador.
     * Accuracy = (suma de la diagonal principal) / (total de predicciones)
     *
     * @return Exactitud (accuracy)
     */
    public double getAccuracy() {
        int correctPredictions = 0;
        int totalPredictions = 0;

        for (Object actual : classLabels) {
            Map<Object, Integer> row = matrix.get(actual);
            for (Object predicted : classLabels) {
                int count = row.get(predicted);
                if (actual.equals(predicted)) {
                    correctPredictions += count;
                }
                totalPredictions += count;
            }
        }

        // Evitar división por cero
        if (totalPredictions == 0) {
            return 0.0;
        }

        return (double) correctPredictions / totalPredictions;
    }

    /**
     * Calcula la precisión media macro (macro-average precision).
     * Es la media de las precisiones de todas las clases.
     *
     * @return Precisión media macro
     */
    public double getMacroAveragePrecision() {
        double sum = 0.0;
        for (Object classLabel : classLabels) {
            sum += getPrecision(classLabel);
        }

        return sum / classLabels.size();
    }

    /**
     * Calcula el recall medio macro (macro-average recall).
     * Es la media de los recalls de todas las clases.
     *
     * @return Recall medio macro
     */
    public double getMacroAverageRecall() {
        double sum = 0.0;
        for (Object classLabel : classLabels) {
            sum += getRecall(classLabel);
        }

        return sum / classLabels.size();
    }

    /**
     * Calcula el F1-Score medio macro (macro-average F1-Score).
     * Es la media de los F1-Scores de todas las clases.
     *
     * @return F1-Score medio macro
     */
    public double getMacroAverageF1Score() {
        double sum = 0.0;
        for (Object classLabel : classLabels) {
            sum += getF1Score(classLabel);
        }

        return sum / classLabels.size();
    }

    /**
     * Obtiene el número de verdaderos positivos para una clase específica.
     *
     * @param classLabel Etiqueta de clase
     * @return Número de verdaderos positivos
     */
    public int getTruePositives(Object classLabel) {
        if (!classLabels.contains(classLabel)) {
            throw new IllegalArgumentException("Etiqueta de clase no encontrada: " + classLabel);
        }

        return matrix.getOrDefault(classLabel, Collections.emptyMap()).getOrDefault(classLabel, 0);
    }

    /**
     * Obtiene el número de falsos positivos para una clase específica.
     *
     * @param classLabel Etiqueta de clase
     * @return Número de falsos positivos
     */
    public int getFalsePositives(Object classLabel) {
        if (!classLabels.contains(classLabel)) {
            throw new IllegalArgumentException("Etiqueta de clase no encontrada: " + classLabel);
        }

        int falsePositives = 0;
        for (Object actual : classLabels) {
            if (!actual.equals(classLabel)) {
                falsePositives += matrix.getOrDefault(actual, Collections.emptyMap()).getOrDefault(classLabel, 0);
            }
        }

        return falsePositives;
    }

    /**
     * Obtiene el número de falsos negativos para una clase específica.
     *
     * @param classLabel Etiqueta de clase
     * @return Número de falsos negativos
     */
    public int getFalseNegatives(Object classLabel) {
        if (!classLabels.contains(classLabel)) {
            throw new IllegalArgumentException("Etiqueta de clase no encontrada: " + classLabel);
        }

        int falseNegatives = 0;
        Map<Object, Integer> row = matrix.getOrDefault(classLabel, Collections.emptyMap());
        for (Object predicted : classLabels) {
            if (!predicted.equals(classLabel)) {
                falseNegatives += row.getOrDefault(predicted, 0);
            }
        }

        return falseNegatives;
    }

    /**
     * Obtiene el número de verdaderos negativos para una clase específica.
     *
     * @param classLabel Etiqueta de clase
     * @return Número de verdaderos negativos
     */
    public int getTrueNegatives(Object classLabel) {
        if (!classLabels.contains(classLabel)) {
            throw new IllegalArgumentException("Etiqueta de clase no encontrada: " + classLabel);
        }

        int trueNegatives = 0;
        for (Object actual : classLabels) {
            if (!actual.equals(classLabel)) {
                Map<Object, Integer> row = matrix.get(actual);
                for (Object predicted : classLabels) {
                    if (!predicted.equals(classLabel)) {
                        trueNegatives += row.getOrDefault(predicted, 0);
                    }
                }
            }
        }

        return trueNegatives;
    }

    /**
     * Obtiene un conjunto de todas las etiquetas de clase.
     *
     * @return Conjunto de etiquetas de clase
     */
    public Set<Object> getClassLabels() {
        return new HashSet<>(classLabels);
    }

    /**
     * Obtiene la matriz de confusión completa como un mapa.
     *
     * @return Mapa de la matriz de confusión
     */
    public Map<Object, Map<Object, Integer>> getMatrix() {
        // Crear una copia profunda para evitar modificaciones externas
        Map<Object, Map<Object, Integer>> copy = new HashMap<>();
        for (Object actual : matrix.keySet()) {
            Map<Object, Integer> row = matrix.get(actual);
            copy.put(actual, new HashMap<>(row));
        }
        return copy;
    }

    /**
     * Convierte la matriz de confusión a una representación en cadena.
     *
     * @return Representación en cadena de la matriz de confusión
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Object> sortedLabels = new ArrayList<>(classLabels);

        // Encabezado
        sb.append("Matriz de Confusión:\n");
        sb.append("                ");
        for (Object predicted : sortedLabels) {
            sb.append(String.format("%-15s", "Pred: " + predicted));
        }
        sb.append("\n");

        // Filas
        for (Object actual : sortedLabels) {
            sb.append(String.format("%-15s", "Actual: " + actual));
            Map<Object, Integer> row = matrix.get(actual);
            for (Object predicted : sortedLabels) {
                sb.append(String.format("%-15d", row.get(predicted)));
            }
            sb.append("\n");
        }

        // Métricas generales
        sb.append("\nMétricas:\n");
        sb.append(String.format("Accuracy: %.4f\n", getAccuracy()));
        sb.append(String.format("Macro-Average Precision: %.4f\n", getMacroAveragePrecision()));
        sb.append(String.format("Macro-Average Recall: %.4f\n", getMacroAverageRecall()));
        sb.append(String.format("Macro-Average F1-Score: %.4f\n", getMacroAverageF1Score()));

        // Métricas por clase
        sb.append("\nMétricas por clase:\n");
        for (Object classLabel : sortedLabels) {
            sb.append(String.format("Clase: %s\n", classLabel));
            sb.append(String.format("  Precision: %.4f\n", getPrecision(classLabel)));
            sb.append(String.format("  Recall: %.4f\n", getRecall(classLabel)));
            sb.append(String.format("  F1-Score: %.4f\n", getF1Score(classLabel)));
            sb.append(String.format("  TP: %d, FP: %d, FN: %d, TN: %d\n",
                    getTruePositives(classLabel),
                    getFalsePositives(classLabel),
                    getFalseNegatives(classLabel),
                    getTrueNegatives(classLabel)));
        }

        return sb.toString();
    }
}