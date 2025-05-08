package org.yisus.ia.utils;

import org.yisus.ia.model.DataPoint;
import org.yisus.ia.model.Dataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Clase para visualizar los dígitos MNIST y sus predicciones.
 * Esta utilidad permite mostrar visualmente las imágenes de los dígitos
 * junto con las etiquetas reales y predichas.
 */
public class MNISTVisualizer {

    /**
     * Muestra una ventana con la visualización de un dígito MNIST.
     *
     * @param dataPoint Punto de datos que contiene la imagen del dígito
     * @param prediction Etiqueta predicha (puede ser null si no hay predicción)
     */
    public static void showDigit(DataPoint dataPoint, Object prediction) {
        JFrame frame = new JFrame("Visualizador MNIST");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 350);

        // Panel para dibujar el dígito
        JPanel digitPanel = new DigitPanel(dataPoint);

        // Etiqueta para mostrar información
        String labelText = "Dígito real: " + dataPoint.getLabel();
        if (prediction != null) {
            labelText += ", Predicción: " + prediction;
        }
        JLabel infoLabel = new JLabel(labelText, SwingConstants.CENTER);
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Añadir componentes al frame
        frame.setLayout(new BorderLayout());
        frame.add(digitPanel, BorderLayout.CENTER);
        frame.add(infoLabel, BorderLayout.SOUTH);

        // Mostrar ventana
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Muestra múltiples dígitos MNIST en una cuadrícula.
     *
     * @param dataset Conjunto de datos que contiene los dígitos
     * @param predictions Array de predicciones (puede ser null)
     * @param startIndex Índice inicial en el dataset
     * @param count Número de dígitos a mostrar
     */
    public static void showDigits(Dataset dataset, Object[] predictions, int startIndex, int count) {
        int cols = (int) Math.ceil(Math.sqrt(count));
        int rows = (int) Math.ceil((double) count / cols);

        JFrame frame = new JFrame("Visualizador MNIST - Múltiples dígitos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(cols * 100, rows * 120);

        JPanel mainPanel = new JPanel(new GridLayout(rows, cols));

        for (int i = 0; i < count; i++) {
            if (startIndex + i >= dataset.size()) break;

            DataPoint dataPoint = dataset.get(startIndex + i);
            Object prediction = predictions != null ? predictions[startIndex + i] : null;

            JPanel digitContainer = new JPanel(new BorderLayout());
            JPanel digitPanel = new DigitPanel(dataPoint);

            String labelText = "Real: " + dataPoint.getLabel();
            if (prediction != null) {
                labelText += ", Pred: " + prediction;

                // Resaltar predicciones incorrectas
                if (!prediction.equals(dataPoint.getLabel())) {
                    digitContainer.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                }
            }

            JLabel infoLabel = new JLabel(labelText, SwingConstants.CENTER);
            infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));

            digitContainer.add(digitPanel, BorderLayout.CENTER);
            digitContainer.add(infoLabel, BorderLayout.SOUTH);
            mainPanel.add(digitContainer);
        }

        frame.add(new JScrollPane(mainPanel));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Panel personalizado para dibujar un dígito MNIST.
     */
    private static class DigitPanel extends JPanel {
        private final DataPoint dataPoint;
        private final int imageSize;

        public DigitPanel(DataPoint dataPoint) {
            this.dataPoint = dataPoint;
            this.imageSize = (int) Math.sqrt(dataPoint.getFeatureCount());
            setPreferredSize(new Dimension(imageSize * 4, imageSize * 4));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int w = getWidth();
            int h = getHeight();
            double scale = Math.min(w / (double) imageSize, h / (double) imageSize);

            // Dibujar píxeles
            HashMap<String, Double> features = dataPoint.getFeatures();
            for (int y = 0; y < imageSize; y++) {
                for (int x = 0; x < imageSize; x++) {
                    String featureName = "pixel_" + (y * imageSize + x);
                    double value = features.getOrDefault(featureName, 0.0);

                    // Escalar el valor a 0-255 y convertir a color gris
                    int grayValue = (int) (value * 255);
                    g2d.setColor(new Color(grayValue, grayValue, grayValue));

                    // Dibujar el píxel escalado
                    int pixelX = (int) (x * scale);
                    int pixelY = (int) (y * scale);
                    int pixelW = (int) Math.ceil(scale);
                    int pixelH = (int) Math.ceil(scale);
                    g2d.fillRect(pixelX, pixelY, pixelW, pixelH);
                }
            }
        }
    }
}