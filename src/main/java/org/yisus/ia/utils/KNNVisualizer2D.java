package org.yisus.ia.utils;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.yisus.ia.model.DataPoint;
import org.yisus.ia.model.Dataset;
import org.yisus.ia.model.KNNResult;
import org.yisus.ia.model.Neighbor;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase para visualizar datasets y resultados KNN en 2D.
 * Requiere que los puntos de datos tengan al menos dos características.
 */
public class KNNVisualizer2D {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final String X_FEATURE = "x";
    private static final String Y_FEATURE = "y";

    /**
     * Visualiza un conjunto de datos en un gráfico 2D.
     *
     * @param dataset El conjunto de datos a visualizar
     * @param title Título del gráfico
     * @param xFeature Nombre de la característica para el eje X
     * @param yFeature Nombre de la característica para el eje Y
     */
    public static void visualizeDataset(Dataset dataset, String title, String xFeature, String yFeature) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("El dataset no puede estar vacío");
        }

        // Crear colección de series
        XYSeriesCollection datasetCollection = new XYSeriesCollection();

        // Obtener todas las etiquetas únicas
        Set<Object> uniqueLabels = getUniqueLabels(dataset);

        // Crear una serie para cada etiqueta
        for (Object label : uniqueLabels) {
            XYSeries series = new XYSeries(label != null ? label.toString() : "Desconocido");

            // Añadir puntos a las series correspondientes
            for (DataPoint point : dataset.getDataPoints()) {
                if ((point.getLabel() == null && label == null) ||
                        (point.getLabel() != null && point.getLabel().equals(label))) {

                    HashMap<String, Double> features = point.getFeatures();
                    if (features.containsKey(xFeature) && features.containsKey(yFeature)) {
                        series.add(features.get(xFeature), features.get(yFeature));
                    }
                }
            }

            datasetCollection.addSeries(series);
        }

        // Crear gráfico
        JFreeChart chart = ChartFactory.createScatterPlot(
                title,
                xFeature,
                yFeature,
                datasetCollection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Mostrar gráfico
        showChartInFrame(chart, title);
    }

    /**
     * Visualiza un resultado KNN en un gráfico 2D.
     *
     * @param knnResult Resultado KNN a visualizar
     * @param trainingSet Conjunto de entrenamiento original
     * @param title Título del gráfico
     * @param xFeature Nombre de la característica para el eje X
     * @param yFeature Nombre de la característica para el eje Y
     */
    public static void visualizeKNNResult(KNNResult knnResult, Dataset trainingSet,
                                          String title, String xFeature, String yFeature) {
        if (knnResult == null || trainingSet == null || trainingSet.isEmpty()) {
            throw new IllegalArgumentException("El resultado KNN y el dataset no pueden estar vacíos");
        }

        // Crear colección de series
        XYSeriesCollection datasetCollection = new XYSeriesCollection();

        // Serie para todos los puntos del conjunto de entrenamiento
        XYSeries trainingPoints = new XYSeries("Puntos de entrenamiento");
        for (DataPoint point : trainingSet.getDataPoints()) {
            HashMap<String, Double> features = point.getFeatures();
            if (features.containsKey(xFeature) && features.containsKey(yFeature)) {
                trainingPoints.add(features.get(xFeature), features.get(yFeature));
            }
        }
        datasetCollection.addSeries(trainingPoints);

        // Serie para el punto de consulta
        XYSeries queryPoint = new XYSeries("Punto de consulta");
        HashMap<String, Double> queryFeatures = knnResult.getQueryPoint().getFeatures();
        if (queryFeatures.containsKey(xFeature) && queryFeatures.containsKey(yFeature)) {
            queryPoint.add(queryFeatures.get(xFeature), queryFeatures.get(yFeature));
        }
        datasetCollection.addSeries(queryPoint);

        // Serie para los vecinos más cercanos
        XYSeries neighbors = new XYSeries("Vecinos más cercanos");
        for (Neighbor neighbor : knnResult.getNeighbors()) {
            HashMap<String, Double> neighborFeatures = neighbor.getDataPoint().getFeatures();
            if (neighborFeatures.containsKey(xFeature) && neighborFeatures.containsKey(yFeature)) {
                neighbors.add(neighborFeatures.get(xFeature), neighborFeatures.get(yFeature));
            }
        }
        datasetCollection.addSeries(neighbors);

        // Crear gráfico
        JFreeChart chart = ChartFactory.createScatterPlot(
                title,
                xFeature,
                yFeature,
                datasetCollection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Personalizar apariencia
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();

        // Puntos de entrenamiento: gris
        renderer.setSeriesPaint(0, Color.LIGHT_GRAY);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));

        // Punto de consulta: rojo y más grande
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-6, -6, 12, 12));

        // Vecinos más cercanos: azul
        renderer.setSeriesPaint(2, Color.BLUE);
        renderer.setSeriesShape(2, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8));

        // Mostrar gráfico
        showChartInFrame(chart, title);
    }

    /**
     * Visualiza un dataset con características 2D por defecto.
     * Usa las primeras dos características del dataset como ejes X e Y.
     *
     * @param dataset El conjunto de datos a visualizar
     * @param title Título del gráfico
     */
    public static void visualizeDataset(Dataset dataset, String title) {
        if (dataset == null || dataset.isEmpty() ||
                dataset.getDataPoints().get(0).getFeatureCount() < 2) {
            throw new IllegalArgumentException("El dataset debe tener al menos 2 características");
        }

        // Obtener nombres de las dos primeras características
        String[] featureNames = dataset.getFeatureNames();
        String xFeature, yFeature;

        if (featureNames != null && featureNames.length >= 2) {
            xFeature = featureNames[0];
            yFeature = featureNames[1];
        } else {
            // Si no hay nombres de características, busca claves en el primer punto
            HashMap<String, Double> firstPointFeatures = dataset.getDataPoints().get(0).getFeatures();
            if (firstPointFeatures.containsKey(X_FEATURE) && firstPointFeatures.containsKey(Y_FEATURE)) {
                xFeature = X_FEATURE;
                yFeature = Y_FEATURE;
            } else {
                // Tomar las dos primeras claves
                xFeature = firstPointFeatures.keySet().toArray(new String[0])[0];
                yFeature = firstPointFeatures.keySet().toArray(new String[0])[1];
            }
        }

        visualizeDataset(dataset, title, xFeature, yFeature);
    }

    /**
     * Visualiza un resultado KNN con características 2D por defecto.
     *
     * @param knnResult Resultado KNN a visualizar
     * @param trainingSet Conjunto de entrenamiento original
     * @param title Título del gráfico
     */
    public static void visualizeKNNResult(KNNResult knnResult, Dataset trainingSet, String title) {
        if (knnResult == null || trainingSet == null || trainingSet.isEmpty() ||
                knnResult.getQueryPoint().getFeatureCount() < 2) {
            throw new IllegalArgumentException("El dataset debe tener al menos 2 características");
        }

        // Obtener nombres de las dos primeras características
        String[] featureNames = trainingSet.getFeatureNames();
        String xFeature, yFeature;

        if (featureNames != null && featureNames.length >= 2) {
            xFeature = featureNames[0];
            yFeature = featureNames[1];
        } else {
            // Si no hay nombres de características, busca claves en el primer punto
            HashMap<String, Double> firstPointFeatures = trainingSet.getDataPoints().get(0).getFeatures();
            if (firstPointFeatures.containsKey(X_FEATURE) && firstPointFeatures.containsKey(Y_FEATURE)) {
                xFeature = X_FEATURE;
                yFeature = Y_FEATURE;
            } else {
                // Tomar las dos primeras claves
                xFeature = firstPointFeatures.keySet().toArray(new String[0])[0];
                yFeature = firstPointFeatures.keySet().toArray(new String[0])[1];
            }
        }

        visualizeKNNResult(knnResult, trainingSet, title, xFeature, yFeature);
    }

    /**
     * Muestra el gráfico en un JFrame.
     *
     * @param chart El gráfico a mostrar
     * @param title Título de la ventana
     */
    private static void showChartInFrame(JFreeChart chart, String title) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Obtiene el conjunto de etiquetas únicas en un dataset.
     *
     * @param dataset El dataset a analizar
     * @return Conjunto de etiquetas únicas
     */
    private static Set<Object> getUniqueLabels(Dataset dataset) {
        Set<Object> uniqueLabels = new HashSet<>();
        for (DataPoint point : dataset.getDataPoints()) {
            uniqueLabels.add(point.getLabel());
        }
        return uniqueLabels;
    }
}