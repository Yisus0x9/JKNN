package org.yisus.ia.utils;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.yisus.ia.model.DataPoint;
import org.yisus.ia.model.Dataset;
import org.yisus.ia.model.KNNResult;
import org.yisus.ia.model.Neighbor;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Clase para visualizar datasets y resultados KNN en 3D.
 * Requiere que los puntos de datos tengan al menos tres características.
 *
 * Esta clase utiliza JFreeChart para crear visualizaciones 3D simuladas,
 * donde el tercer eje (Z) se representa mediante el tamaño y color de los puntos.
 */
public class KNNVisualizer3D {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final String X_FEATURE = "x";
    private static final String Y_FEATURE = "y";
    private static final String Z_FEATURE = "z";

    /**
     * Visualiza un conjunto de datos en un gráfico 3D.
     *
     * @param dataset El conjunto de datos a visualizar
     * @param title Título del gráfico
     * @param xFeature Nombre de la característica para el eje X
     * @param yFeature Nombre de la característica para el eje Y
     * @param zFeature Nombre de la característica para el eje Z
     */
    public static void visualizeDataset(Dataset dataset, String title,
                                        String xFeature, String yFeature, String zFeature) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("El dataset no puede estar vacío");
        }

        // Crear el dataset XYZ para JFreeChart
        DefaultXYZDataset xyzDataset = new DefaultXYZDataset();

        // Obtener todas las etiquetas únicas
        Set<Object> uniqueLabels = getUniqueLabels(dataset);
        Map<Object, List<DataPoint>> pointsByLabel = groupPointsByLabel(dataset);

        // Crear series para cada etiqueta
        for (Object label : uniqueLabels) {
            List<DataPoint> points = pointsByLabel.get(label);
            if (points != null && !points.isEmpty()) {
                double[][] seriesData = new double[3][points.size()];

                for (int i = 0; i < points.size(); i++) {
                    HashMap<String, Double> features = points.get(i).getFeatures();
                    seriesData[0][i] = features.getOrDefault(xFeature, 0.0);
                    seriesData[1][i] = features.getOrDefault(yFeature, 0.0);
                    seriesData[2][i] = features.getOrDefault(zFeature, 0.0);
                }

                String seriesName = label != null ? label.toString() : "Desconocido";
                xyzDataset.addSeries(seriesName, seriesData);
            }
        }

        // Crear el gráfico
        JFreeChart chart = createBubbleChart(
                title,
                xFeature,
                yFeature,
                zFeature,
                xyzDataset
        );

        // Mostrar el gráfico
        showChartInFrame(chart, title);
    }

    /**
     * Visualiza un resultado KNN en un gráfico 3D.
     *
     * @param knnResult Resultado KNN a visualizar
     * @param trainingSet Conjunto de entrenamiento original
     * @param title Título del gráfico
     * @param xFeature Nombre de la característica para el eje X
     * @param yFeature Nombre de la característica para el eje Y
     * @param zFeature Nombre de la característica para el eje Z
     */
    public static void visualizeKNNResult(KNNResult knnResult, Dataset trainingSet,
                                          String title, String xFeature, String yFeature, String zFeature) {
        if (knnResult == null || trainingSet == null || trainingSet.isEmpty()) {
            throw new IllegalArgumentException("El resultado KNN y el dataset no pueden estar vacíos");
        }

        // Crear el dataset XYZ para JFreeChart
        DefaultXYZDataset xyzDataset = new DefaultXYZDataset();

        // Serie para los puntos de entrenamiento
        List<DataPoint> trainingPoints = trainingSet.getDataPoints();
        double[][] trainingData = new double[3][trainingPoints.size()];

        for (int i = 0; i < trainingPoints.size(); i++) {
            HashMap<String, Double> features = trainingPoints.get(i).getFeatures();
            trainingData[0][i] = features.getOrDefault(xFeature, 0.0);
            trainingData[1][i] = features.getOrDefault(yFeature, 0.0);
            trainingData[2][i] = features.getOrDefault(zFeature, 0.0);
        }

        xyzDataset.addSeries("Puntos de entrenamiento", trainingData);

        // Serie para el punto de consulta
        double[][] queryData = new double[3][1];
        HashMap<String, Double> queryFeatures = knnResult.getQueryPoint().getFeatures();
        queryData[0][0] = queryFeatures.getOrDefault(xFeature, 0.0);
        queryData[1][0] = queryFeatures.getOrDefault(yFeature, 0.0);
        queryData[2][0] = queryFeatures.getOrDefault(zFeature, 0.0);

        xyzDataset.addSeries("Punto de consulta", queryData);

        // Serie para los vecinos más cercanos
        List<Neighbor> neighbors = knnResult.getNeighbors();
        double[][] neighborsData = new double[3][neighbors.size()];

        for (int i = 0; i < neighbors.size(); i++) {
            HashMap<String, Double> features = neighbors.get(i).getDataPoint().getFeatures();
            neighborsData[0][i] = features.getOrDefault(xFeature, 0.0);
            neighborsData[1][i] = features.getOrDefault(yFeature, 0.0);
            neighborsData[2][i] = features.getOrDefault(zFeature, 0.0);
        }

        xyzDataset.addSeries("Vecinos más cercanos", neighborsData);

        // Crear el gráfico
        JFreeChart chart = createBubbleChart(
                title,
                xFeature,
                yFeature,
                zFeature,
                xyzDataset
        );

        // Personalizar el gráfico
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();

        // Puntos de entrenamiento: gris y pequeños
        renderer.setSeriesPaint(0, Color.LIGHT_GRAY);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));

        // Punto de consulta: rojo y grande
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-8, -8, 16, 16));

        // Vecinos más cercanos: azul y tamaño medio
        renderer.setSeriesPaint(2, Color.BLUE);
        renderer.setSeriesShape(2, new java.awt.geom.Ellipse2D.Double(-5, -5, 10, 10));

        // Mostrar el gráfico
        showChartInFrame(chart, title);
    }

    /**
     * Visualiza un dataset con características 3D por defecto.
     * Usa las tres primeras características del dataset como ejes X, Y y Z.
     *
     * @param dataset El conjunto de datos a visualizar
     * @param title Título del gráfico
     */
    public static void visualizeDataset(Dataset dataset, String title) {
        if (dataset == null || dataset.isEmpty() ||
                dataset.getDataPoints().get(0).getFeatureCount() < 3) {
            throw new IllegalArgumentException("El dataset debe tener al menos 3 características");
        }

        // Obtener nombres de las tres primeras características
        String[] featureNames = dataset.getFeatureNames();
        String xFeature, yFeature, zFeature;

        if (featureNames != null && featureNames.length >= 3) {
            xFeature = featureNames[0];
            yFeature = featureNames[1];
            zFeature = featureNames[2];
        } else {
            // Si no hay nombres de características, busca claves en el primer punto
            HashMap<String, Double> firstPointFeatures = dataset.getDataPoints().get(0).getFeatures();
            if (firstPointFeatures.containsKey(X_FEATURE) &&
                    firstPointFeatures.containsKey(Y_FEATURE) &&
                    firstPointFeatures.containsKey(Z_FEATURE)) {
                xFeature = X_FEATURE;
                yFeature = Y_FEATURE;
                zFeature = Z_FEATURE;
            } else {
                // Tomar las tres primeras claves
                String[] keys = firstPointFeatures.keySet().toArray(new String[0]);
                xFeature = keys[0];
                yFeature = keys[1];
                zFeature = keys[2];
            }
        }

        visualizeDataset(dataset, title, xFeature, yFeature, zFeature);
    }

    /**
     * Visualiza un resultado KNN con características 3D por defecto.
     *
     * @param knnResult Resultado KNN a visualizar
     * @param trainingSet Conjunto de entrenamiento original
     * @param title Título del gráfico
     */
    public static void visualizeKNNResult(KNNResult knnResult, Dataset trainingSet, String title) {
        if (knnResult == null || trainingSet == null || trainingSet.isEmpty() ||
                knnResult.getQueryPoint().getFeatureCount() < 3) {
            throw new IllegalArgumentException("El dataset debe tener al menos 3 características");
        }

        // Obtener nombres de las tres primeras características
        String[] featureNames = trainingSet.getFeatureNames();
        String xFeature, yFeature, zFeature;

        if (featureNames != null && featureNames.length >= 3) {
            xFeature = featureNames[0];
            yFeature = featureNames[1];
            zFeature = featureNames[2];
        } else {
            // Si no hay nombres de características, busca claves en el primer punto
            HashMap<String, Double> firstPointFeatures = trainingSet.getDataPoints().get(0).getFeatures();
            if (firstPointFeatures.containsKey(X_FEATURE) &&
                    firstPointFeatures.containsKey(Y_FEATURE) &&
                    firstPointFeatures.containsKey(Z_FEATURE)) {
                xFeature = X_FEATURE;
                yFeature = Y_FEATURE;
                zFeature = Z_FEATURE;
            } else {
                // Tomar las tres primeras claves
                String[] keys = firstPointFeatures.keySet().toArray(new String[0]);
                xFeature = keys[0];
                yFeature = keys[1];
                zFeature = keys[2];
            }
        }

        visualizeKNNResult(knnResult, trainingSet, title, xFeature, yFeature, zFeature);
    }

    /**
     * Crea un gráfico de burbujas para representar datos 3D.
     *
     * @param title Título del gráfico
     * @param xLabel Etiqueta para el eje X
     * @param yLabel Etiqueta para el eje Y
     * @param zLabel Etiqueta para el eje Z (representado por el tamaño de las burbujas)
     * @param dataset Conjunto de datos XYZ
     * @return Gráfico JFreeChart
     */
    private static JFreeChart createBubbleChart(String title, String xLabel, String yLabel,
                                                String zLabel, DefaultXYZDataset dataset) {
        JFreeChart chart = ChartFactory.createBubbleChart(
                title,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();

        // Personalizar ejes
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

        xAxis.setAutoRangeIncludesZero(false);
        yAxis.setAutoRangeIncludesZero(false);

        // Añadir leyenda para el eje Z
        chart.addSubtitle(new org.jfree.chart.title.TextTitle(
                zLabel + " representado por el tamaño y color de los puntos",
                new Font("Dialog", Font.ITALIC, 10)
        ));

        return chart;
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

    /**
     * Agrupa los puntos de datos por etiqueta.
     *
     * @param dataset El dataset a analizar
     * @return Mapa de etiquetas a listas de puntos
     */
    private static Map<Object, List<DataPoint>> groupPointsByLabel(Dataset dataset) {
        Map<Object, List<DataPoint>> pointsByLabel = new HashMap<>();

        for (DataPoint point : dataset.getDataPoints()) {
            Object label = point.getLabel();
            pointsByLabel.computeIfAbsent(label, k -> new ArrayList<>()).add(point);
        }

        return pointsByLabel;
    }
}