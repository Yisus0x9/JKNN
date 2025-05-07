package org.yisus.ia.utils;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Sphere;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.yisus.ia.model.DataPoint;
import org.yisus.ia.model.Dataset;
import org.yisus.ia.model.KNNResult;
import org.yisus.ia.model.Neighbor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase utilitaria para visualizar datos y resultados KNN usando JZY3D.
 * Esta clase proporciona métodos para crear gráficos 3D de conjuntos de datos
 * y resultados de búsqueda de vecinos más cercanos.
 */
public class VisualizeDataset {

    /**
     * Visualiza un conjunto de datos en un gráfico 3D.
     * Este método muestra las primeras tres características de cada punto de datos.
     * Diferentes colores representan diferentes etiquetas de clase.
     *
     * @param dataset Conjunto de datos a visualizar
     * @param title Título del gráfico
     */
    public static void visualizeDataset(Dataset dataset, String title) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de datos no puede estar vacío");
        }

        // Verificar que haya al menos 3 características para visualizar en 3D
        DataPoint firstPoint = dataset.get(0);
        if (firstPoint.getFeatureCount() < 3) {
            throw new IllegalArgumentException("Se necesitan al menos 3 características para visualización 3D");
        }

        // Preparar datos para visualización
        int size = dataset.size();
        Coord3d[] points = new Coord3d[size];
        Color[] colors = new Color[size];

        // Mapear etiquetas únicas a colores
        Map<Object, Color> labelColors = new HashMap<>();
        List<Object> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Object label = dataset.get(i).getLabel();
            if (!labels.contains(label)) {
                labels.add(label);
            }
        }

        // Generar colores para cada etiqueta
        ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), 0, labels.size());
        for (int i = 0; i < labels.size(); i++) {
            org.jzy3d.colors.Color jzyColor = colorMapper.getColor(i);
            labelColors.put(labels.get(i), new Color(jzyColor.r, jzyColor.g, jzyColor.b, 0.8f));
        }

        // Extraer las primeras tres características de cada punto
        String[] featureNames = dataset.getFeatureNames();
        if (featureNames == null || featureNames.length < 3) {
            // Si no hay nombres de características, usar las tres primeras claves
            featureNames = (String[]) firstPoint.getFeatures().keySet().toArray(new String[3]);
        }

        String feature1 = featureNames[0];
        String feature2 = featureNames[1];
        String feature3 = featureNames[2];

        // Crear puntos 3D
        for (int i = 0; i < size; i++) {
            DataPoint dataPoint = dataset.get(i);
            float x = (float)dataPoint.getFeature(feature1);
            float y = (float)dataPoint.getFeature(feature2);
            float z = (float)dataPoint.getFeature(feature3);
            points[i] = new Coord3d(x, y, z);
            colors[i] = labelColors.get(dataPoint.getLabel());
        }

        // Crear gráfico de dispersión
        Scatter scatter = new Scatter(points, colors);
        scatter.setWidth(5f);

        // Crear y configurar el gráfico
        Chart chart = new AWTChart(AWTChartFactory.chart().getFactory(), Quality.Advanced());
        chart.getScene().add(scatter);
        chart.getAxisLayout().setXAxisLabel(feature1);
        chart.getAxisLayout().setYAxisLabel(feature2);
        chart.getAxisLayout().setZAxisLabel(feature3);

        // Mostrar el gráfico en una ventana
        showChart(chart, title);
    }

    /**
     * Visualiza los resultados de KNN en un gráfico 3D.
     * Este método muestra el punto de consulta y sus vecinos más cercanos.
     *
     * @param knnResult Resultado de la búsqueda de vecinos más cercanos
     * @param title Título del gráfico
     */
    public static void visualizeKNNResult(KNNResult knnResult, String title) {
        if (knnResult == null || knnResult.getNeighborCount() == 0) {
            throw new IllegalArgumentException("El resultado KNN no puede estar vacío");
        }

        DataPoint queryPoint = knnResult.getQueryPoint();
        List<Neighbor> neighbors = knnResult.getNeighbors();

        // Verificar que haya al menos 3 características para visualizar en 3D
        if (queryPoint.getFeatureCount() < 3) {
            throw new IllegalArgumentException("Se necesitan al menos 3 características para visualización 3D");
        }

        // Extraer las primeras tres características
        String[] featureKeys = queryPoint.getFeatures().keySet().toArray(new String[0]);
        String feature1 = featureKeys[0];
        String feature2 = featureKeys[1];
        String feature3 = featureKeys[2];

        // Crear un gráfico
        Chart chart = new AWTChart(AWTChartFactory.chart().getFactory(),Quality.Advanced());

        // Añadir el punto de consulta (más grande y rojo)
        float qx = (float)queryPoint.getFeature(feature1);
        float qy =(float) queryPoint.getFeature(feature2);
        float qz = (float)queryPoint.getFeature(feature3);
        Sphere querySphere = new Sphere(new Coord3d(qx, qy, qz), 0.1f, 15, new Color(1f, 0f, 0f, 0.8f));
        chart.getScene().add(querySphere);

        // Añadir vecinos (tamaño proporcional a su cercanía)
        ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), 0, neighbors.size());

        for (int i = 0; i < neighbors.size(); i++) {
            Neighbor neighbor = neighbors.get(i);
            DataPoint neighborPoint = neighbor.getDataPoint();

            float nx = (float)neighborPoint.getFeature(feature1);
            float ny = (float)neighborPoint.getFeature(feature2);
            float nz = (float)neighborPoint.getFeature(feature3);

            // El tamaño es inversamente proporcional a la distancia
            float size = 0.08f - (float)(0.05f * neighbor.getDistance() / neighbors.get(neighbors.size()-1).getDistance());
            if (size < 0.03f) size = 0.03f;

            org.jzy3d.colors.Color jzyColor = colorMapper.getColor(i);
            Color color = new Color(jzyColor.r, jzyColor.g, jzyColor.b, 0.7f);

            Sphere neighborSphere = new Sphere(new Coord3d(nx, ny, nz), size, 10, color);
            chart.getScene().add(neighborSphere);
        }

        chart.getAxisLayout().setXAxisLabel(feature1);
        chart.getAxisLayout().setYAxisLabel(feature2);
        chart.getAxisLayout().setZAxisLabel(feature3);

        // Mostrar el gráfico en una ventana
        showChart(chart, title);
    }

    /**
     * Método auxiliar para mostrar un gráfico en una ventana.
     *
     * @param chart Gráfico a mostrar
     * @param title Título de la ventana
     */
    private static void showChart(Chart chart, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        frame.getContentPane().add((Component) chart.getCanvas());

        frame.setVisible(true);
    }
}