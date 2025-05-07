package org.yisus.ia.evaluation;

/**
 * Clase que encapsula las métricas de rendimiento para modelos de clasificación.
 * Incluye precisión, recall, F1-score y sus promedios macro para evaluación multiclase.
 */
public class PerformanceMetrics {
    private final double accuracy;
    private final double macroAveragePrecision;
    private final double macroAverageRecall;
    private final double macroAverageF1Score;
    private final ConfusionMatrix confusionMatrix;

    /**
     * Constructor para crear métricas a partir de una matriz de confusión.
     *
     * @param confusionMatrix Matriz de confusión
     */
    public PerformanceMetrics(ConfusionMatrix confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
        this.accuracy = confusionMatrix.getAccuracy();
        this.macroAveragePrecision = confusionMatrix.getMacroAveragePrecision();
        this.macroAverageRecall = confusionMatrix.getMacroAverageRecall();
        this.macroAverageF1Score = confusionMatrix.getMacroAverageF1Score();
    }

    /**
     * Constructor para crear métricas con valores específicos (útil para promedios).
     *
     * @param accuracy Precisión global
     * @param macroAveragePrecision Precisión media
     * @param macroAverageRecall Recall medio
     * @param macroAverageF1Score F1-score medio
     */
    public PerformanceMetrics(double accuracy, double macroAveragePrecision,
                              double macroAverageRecall, double macroAverageF1Score) {
        this.accuracy = accuracy;
        this.macroAveragePrecision = macroAveragePrecision;
        this.macroAverageRecall = macroAverageRecall;
        this.macroAverageF1Score = macroAverageF1Score;
        this.confusionMatrix = null;
    }

    /**
     * Obtiene la precisión global (accuracy).
     *
     * @return Precisión global
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     * Obtiene la precisión media para todas las clases.
     *
     * @return Precisión media
     */
    public double getMacroAveragePrecision() {
        return macroAveragePrecision;
    }

    /**
     * Obtiene el recall medio para todas las clases.
     *
     * @return Recall medio
     */
    public double getMacroAverageRecall() {
        return macroAverageRecall;
    }

    /**
     * Obtiene el F1-score medio para todas las clases.
     *
     * @return F1-score medio
     */
    public double getMacroAverageF1Score() {
        return macroAverageF1Score;
    }

    /**
     * Obtiene la matriz de confusión asociada.
     *
     * @return Matriz de confusión (puede ser null para métricas promedio)
     */
    public ConfusionMatrix getConfusionMatrix() {
        return confusionMatrix;
    }

    /**
     * Obtiene la precisión (precision) para una clase específica.
     *
     * @param classLabel Etiqueta de clase
     * @return Precisión para la clase
     * @throws IllegalStateException Si no hay matriz de confusión disponible
     */
    public double getPrecision(Object classLabel) {
        if (confusionMatrix == null) {
            throw new IllegalStateException("No hay matriz de confusión disponible");
        }
        return confusionMatrix.getPrecision(classLabel);
    }

    /**
     * Obtiene el recall para una clase específica.
     *
     * @param classLabel Etiqueta de clase
     * @return Recall para la clase
     * @throws IllegalStateException Si no hay matriz de confusión disponible
     */
    public double getRecall(Object classLabel) {
        if (confusionMatrix == null) {
            throw new IllegalStateException("No hay matriz de confusión disponible");
        }
        return confusionMatrix.getRecall(classLabel);
    }

    /**
     * Obtiene el F1-score para una clase específica.
     *
     * @param classLabel Etiqueta de clase
     * @return F1-score para la clase
     * @throws IllegalStateException Si no hay matriz de confusión disponible
     */
    public double getF1Score(Object classLabel) {
        if (confusionMatrix == null) {
            throw new IllegalStateException("No hay matriz de confusión disponible");
        }
        return confusionMatrix.getF1Score(classLabel);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Performance Metrics:\n");
        sb.append("Accuracy: ").append(String.format("%.4f", accuracy)).append("\n");
        sb.append("Macro-average Precision: ").append(String.format("%.4f", macroAveragePrecision)).append("\n");
        sb.append("Macro-average Recall: ").append(String.format("%.4f", macroAverageRecall)).append("\n");
        sb.append("Macro-average F1-Score: ").append(String.format("%.4f", macroAverageF1Score)).append("\n");

        if (confusionMatrix != null) {
            sb.append("\n").append(confusionMatrix.toString());
        }

        return sb.toString();
    }
}