package org.yisus.ia.demo;

import org.yisus.ia.core.JKNNClassifier;
import org.yisus.ia.model.Dataset;
import org.yisus.ia.evaluation.PerformanceMetrics;
import org.yisus.ia.evaluation.ModelEvaluator;
import org.yisus.ia.model.DataPoint;
import org.yisus.ia.utils.DataNormalizer;
import org.yisus.ia.utils.MultiSeriesLineChartCreator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KNN_IRIS {
        public static void main(String[] args) {
            String irisFilePath = "src/main/java/org/yisus/ia/utils/dataset/iris_dataset.csv";
            try {
                Dataset irisDataset = loadIrisDataset(irisFilePath);
                DataNormalizer dataNormalizer = new DataNormalizer();
                dataNormalizer.fit(irisDataset);
                dataNormalizer.transform(irisDataset);
                System.out.println("Dataset Iris cargado y normalizado: " + irisDataset.size() + " muestras");
                int[] kValues = {3, 5, 7, 14};
                String[] distanceMetrics = {"euclidean", "manhattan", "cosine"};
                int folds = 2;
                double[][] accuracy = new double[distanceMetrics.length][kValues.length];
                double[][] ks = new double[distanceMetrics.length][kValues.length];
                int metricIndex = 0;

                System.out.println("\n===== RESULTADOS DE EVALUACIÓN =====");
                System.out.printf("%-10s %-12s %-10s %-10s %-10s %-10s%n",
                        "k", "Métrica", "Accuracy", "Precision", "Recall", "F1-Score");
                System.out.println("--------------------------------------------------------------");

                for (int k : kValues) {
                    int kIndex = 0;

                    for (String metricName : distanceMetrics) {
                        JKNNClassifier classifier = new JKNNClassifier(k, metricName);
                        //PerformanceMetrics metrics = ModelEvaluator.evaluateWithCrossValidation(classifier, irisDataset, folds);
                        PerformanceMetrics metrics = ModelEvaluator.evaluateWithHoldout(classifier, irisDataset, 0.7);
                        System.out.printf("%-10d %-12s %-10.4f %-10.4f %-10.4f %-10.4f%n",
                                k, metricName,
                                metrics.getAccuracy(),
                                metrics.getMacroAveragePrecision(),
                                metrics.getMacroAverageRecall(),
                                metrics.getMacroAverageF1Score());
                        accuracy[kIndex][metricIndex] = metrics.getAccuracy();
                        ks[kIndex][metricIndex] = k;
                        kIndex++;
                    }
                    System.out.println("--------------------------------------------------------------");
                    metricIndex++;
                }
                MultiSeriesLineChartCreator graficAccuracy= new MultiSeriesLineChartCreator("Accuracy", "K", "Accuracy");
                graficAccuracy.addMultipleSeries(distanceMetrics,ks,accuracy,null);
                graficAccuracy.display();

                /*
                // Encontrar el mejor k para métrica euclídea
                System.out.println("\n===== BÚSQUEDA DEL MEJOR VALOR DE k (EUCLIDEAN) =====");
                org.yisus.iaClassifier classifier = new org.yisus.iaClassifier(3, "euclidean");
                int bestK = ModelEvaluator.findBestK(classifier, irisDataset, 1, 20, folds);
                System.out.println("Mejor valor de k encontrado: " + bestK);

                // Ejemplo de clasificación de una nueva muestra
                System.out.println("\n===== EJEMPLO DE CLASIFICACIÓN =====");
                // Configurar con el mejor k encontrado
                classifier = new org.yisus.iaClassifier(bestK, "euclidean");
                classifier.train(irisDataset);

                // Crear un punto de prueba (ejemplo: Setosa)
                HashMap<String, Double> features = new HashMap<>();
                features.put("sepal_length", 5.1);
                features.put("sepal_width", 3.5);
                features.put("petal_length", 1.4);
                features.put("petal_width", 0.2);

                Object predictedClass = classifier.predict(features);
                System.out.println("Clase predicha: " + predictedClass);
                 */

            } catch (IOException e) {
                System.err.println("Error al cargar el dataset: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Carga el dataset Iris desde un archivo CSV.
         * Formato esperado: sepal_length,sepal_width,petal_length,petal_width,species
         *
         * @param filePath Ruta al archivo CSV
         * @return Dataset con los datos de Iris
         * @throws IOException Si hay un error al leer el archivo
         */
        private static Dataset loadIrisDataset(String filePath) throws IOException {
            List<DataPoint> dataPoints = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Leer encabezado
                String header = br.readLine();

                // Leer datos
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length != 5) {
                        continue; // Saltar líneas con formato incorrecto
                    }

                    // Extraer características
                    HashMap<String, Double> features = new HashMap<>();
                    features.put("sepal_length", Double.parseDouble(values[0]));
                    features.put("sepal_width", Double.parseDouble(values[1]));
                    features.put("petal_length", Double.parseDouble(values[2]));
                    features.put("petal_width", Double.parseDouble(values[3]));

                    // Extraer etiqueta (especie)
                    String label = values[4].trim();

                    // Crear punto de datos
                    DataPoint dataPoint = new DataPoint(features, label);
                    dataPoints.add(dataPoint);
                }
            }

            return new Dataset(dataPoints);
        }

}
