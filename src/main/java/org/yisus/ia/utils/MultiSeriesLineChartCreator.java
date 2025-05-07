package org.yisus.ia.utils;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.util.ArrayList;

/**
 * Clase para crear gráficas de líneas con múltiples series
 * utilizando JFreeChart. Permite visualizar y comparar diferentes
 * conjuntos de datos en una misma gráfica 2D.
 */
public class MultiSeriesLineChartCreator extends ApplicationFrame {

    private static final Color[] DEFAULT_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.ORANGE, Color.CYAN, Color.PINK, Color.DARK_GRAY,
            new Color(128, 0, 128), new Color(0, 128, 128),
            new Color(128, 128, 0), new Color(64, 0, 64)
    };

    private final JFreeChart chart;
    private final XYSeriesCollection dataset;
    private final List<Color> seriesColors;

    /**
     * Constructor principal para crear una gráfica de líneas múltiples.
     *
     * @param title Título de la gráfica
     * @param xAxisLabel Etiqueta para el eje X
     * @param yAxisLabel Etiqueta para el eje Y
     */
    public MultiSeriesLineChartCreator(String title, String xAxisLabel, String yAxisLabel) {
        super(title);

        this.dataset = new XYSeriesCollection();
        this.seriesColors = new ArrayList<>();

        // Crear el gráfico
        chart = ChartFactory.createXYLineChart(
                title,                    // Título
                xAxisLabel,               // Etiqueta eje X
                yAxisLabel,               // Etiqueta eje Y
                dataset,                  // Datos
                PlotOrientation.VERTICAL, // Orientación
                true,                     // Mostrar leyenda
                true,                     // Usar tooltips
                false                     // URLs
        );

        // Customizar el gráfico
        XYPlot plot = chart.getXYPlot();

        // Configurar renderer para mostrar líneas y puntos
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);

        // Configurar ejes
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRangeIncludesZero(false);

        // Configurar panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    /**
     * Agrega una serie de datos a la gráfica.
     *
     * @param seriesName Nombre de la serie
     * @param xValues Valores del eje X
     * @param yValues Valores del eje Y
     * @param color Color para la serie (opcional)
     * @return this (para encadenamiento de métodos)
     */
    public MultiSeriesLineChartCreator addSeries(String seriesName, double[] xValues, double[] yValues, Color color) {


        XYSeries series = new XYSeries(seriesName);
        for (int i = 0; i < yValues.length; i++) {
            series.add(xValues[i], yValues[i]);
        }

        dataset.addSeries(series);
        seriesColors.add(color != null ? color : getDefaultColor(dataset.getSeriesCount() - 1));

        // Actualizar colores de las series
        updateSeriesColors();

        return this;
    }

    /**
     * Agrega una serie de datos a la gráfica con color automático.
     *
     * @param seriesName Nombre de la serie
     * @param xValues Valores del eje X
     * @param yValues Valores del eje Y
     * @return this (para encadenamiento de métodos)
     */
    public MultiSeriesLineChartCreator addSeries(String seriesName, double[] xValues, double[] yValues) {
        return addSeries(seriesName, xValues, yValues, null);
    }

    /**
     * Agrega múltiples series de datos a la gráfica.
     *
     * @param seriesNames Nombres de las series
     * @param xValues Array de arrays con los valores X para cada serie
     * @param yValues Array de arrays con los valores Y para cada serie
     * @param colors Colores para cada serie (opcional)
     * @return this (para encadenamiento de métodos)
     */
    public MultiSeriesLineChartCreator addMultipleSeries(String[] seriesNames, double[][] xValues,
                                                         double[][] yValues, Color[] colors) {

        for (int i = 0; i < seriesNames.length; i++) {
            Color color = (colors != null && i < colors.length) ? colors[i] : null;
            addSeries(seriesNames[i], xValues[i], yValues[i], color);
        }

        return this;
    }

    /**
     * Agrega múltiples series con un eje X común.
     *
     * @param seriesNames Nombres de las series
     * @param xValues Valores del eje X común para todas las series
     * @param yValuesArray Array de arrays con los valores Y para cada serie
     * @param colors Colores para cada serie (opcional)
     * @return this (para encadenamiento de métodos)
     */
    public MultiSeriesLineChartCreator addMultipleSeriesCommonX(String[] seriesNames, double[] xValues,
                                                                double[][] yValuesArray, Color[] colors) {

        for (int i = 0; i < seriesNames.length; i++) {
            if (xValues.length != yValuesArray[i].length) {
                throw new IllegalArgumentException("El array de valores X debe tener la misma longitud que cada array de valores Y");
            }

            Color color = (colors != null && i < colors.length) ? colors[i] : null;
            addSeries(seriesNames[i], xValues, yValuesArray[i], color);
        }

        return this;
    }

    /**
     * Configura los colores de las series.
     *
     * @param colors Array de colores para las series
     * @return this (para encadenamiento de métodos)
     */
    public MultiSeriesLineChartCreator setSeriesColors(Color[] colors) {
        if (colors == null) {
            return this;
        }

        int minSize = Math.min(colors.length, dataset.getSeriesCount());
        for (int i = 0; i < minSize; i++) {
            if (i < seriesColors.size()) {
                seriesColors.set(i, colors[i]);
            } else {
                seriesColors.add(colors[i]);
            }
        }

        updateSeriesColors();
        return this;
    }

    /**
     * Configura el grosor de las líneas para todas las series.
     *
     * @param lineWidth Grosor de la línea
     * @return this (para encadenamiento de métodos)
     */
    public MultiSeriesLineChartCreator setLineWidth(float lineWidth) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(lineWidth));
        }

        return this;
    }

    /**
     * Configura el tamaño de los puntos para todas las series.
     *
     * @param pointSize Tamaño de los puntos
     * @return this (para encadenamiento de métodos)
     */
    public MultiSeriesLineChartCreator setPointSize(int pointSize) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            Shape shape = new java.awt.geom.Ellipse2D.Double(-pointSize/2.0, -pointSize/2.0, pointSize, pointSize);
            renderer.setSeriesShape(i, shape);
        }

        return this;
    }

    /**
     * Configura los rangos de los ejes.
     *
     * @param xMin Valor mínimo del eje X
     * @param xMax Valor máximo del eje X
     * @param yMin Valor mínimo del eje Y
     * @param yMax Valor máximo del eje Y
     * @return this (para encadenamiento de métodos)
     */
    public MultiSeriesLineChartCreator setAxisRanges(double xMin, double xMax, double yMin, double yMax) {
        XYPlot plot = chart.getXYPlot();
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        domainAxis.setRange(xMin, xMax);
        rangeAxis.setRange(yMin, yMax);

        return this;
    }

    /**
     * Muestra la gráfica en una ventana.
     */
    public void display() {
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);
    }

    /**
     * Guarda la gráfica como una imagen PNG.
     *
     * @param filePath Ruta del archivo
     * @param width Ancho de la imagen
     * @param height Alto de la imagen
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean saveAsPNG(String filePath, int width, int height) {
        try {
            ChartUtilities.saveChartAsPNG(
                    new java.io.File(filePath),
                    chart,
                    width,
                    height
            );
            return true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene el panel de la gráfica para integrarlo en otras interfaces.
     *
     * @return Panel de la gráfica
     */
    public ChartPanel getChartPanel() {
        return (ChartPanel) getContentPane();
    }

    /**
     * Obtiene el objeto JFreeChart para personalizaciones avanzadas.
     *
     * @return Objeto JFreeChart
     */
    public JFreeChart getChart() {
        return chart;
    }

    /**
     * Actualiza los colores de las series en el renderer.
     */
    private void updateSeriesColors() {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        for (int i = 0; i < seriesColors.size() && i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, seriesColors.get(i));
        }
    }

    /**
     * Obtiene un color por defecto según el índice de la serie.
     *
     * @param index Índice de la serie
     * @return Color para la serie
     */
    private Color getDefaultColor(int index) {
        return DEFAULT_COLORS[index % DEFAULT_COLORS.length];
    }

}