package org.yisus.ia.demo;

import org.yisus.ia.core.JKNNClassifier;
import org.yisus.ia.metrics.DistanceFactory;
import org.yisus.ia.model.Dataset;
import org.yisus.ia.utils.MNISTDataLoader;
import org.yisus.ia.utils.MNISTVisualizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Ejemplo de uso del clasificador KNN con el conjunto de datos MNIST.
 * Este ejemplo muestra cómo cargar los datos MNIST y usar el clasificador KNN
 * para reconocer dígitos manuscritos.
 */
public class KNN_MNIST {

    public static void main(String[] args) {
        try {
            // Rutas a los archivos MNIST (ajustar según la ubicación de los archivos)
            String trainImagesPath = "src/main/java/org/yisus/ia/utils/dataset/mnist/train-images-idx3-ubyte/train-images-idx3-ubyte";
            String trainLabelsPath = "src/main/java/org/yisus/ia/utils/dataset/mnist/train-labels-idx1-ubyte/train-labels-idx1-ubyte";
            String testImagesPath = "src/main/java/org/yisus/ia/utils/dataset/mnist/t10k-images-idx3-ubyte/t10k-images-idx3-ubyte";
            String testLabelsPath = "src/main/java/org/yisus/ia/utils/dataset/mnist/t10k-labels-idx1-ubyte/t10k-labels-idx1-ubyte";

            // Cargar conjuntos de entrenamiento y prueba
            System.out.println("Cargando conjunto de entrenamiento MNIST...");
            MNISTDataLoader loader = MNISTDataLoader.getInstance();
            Dataset trainingSet = loader.loadMNIST(trainImagesPath, trainLabelsPath);

            System.out.println("Cargando conjunto de prueba MNIST...");
            Dataset testingSet = loader.loadMNIST(testImagesPath, testLabelsPath);

            System.out.println("Conjuntos de datos cargados:");
            System.out.println("Entrenamiento: " + trainingSet.size() + " ejemplos");
            System.out.println("Prueba: " + testingSet.size() + " ejemplos");

            // Si el conjunto de entrenamiento es muy grande, podemos usar un subconjunto para acelerar
            // la clasificación (opcional)
            if (trainingSet.size() > 5000) {
                System.out.println("Usando un subconjunto de entrenamiento para mayor eficiencia...");
                Dataset[] split = trainingSet.split(5000.0 / trainingSet.size());
                trainingSet = split[0]; // Usar solo una parte para entrenamiento
                System.out.println("Subconjunto: " + trainingSet.size() + " ejemplos");
            }

            // Crear y entrenar el clasificador KNN
            System.out.println("Entrenando clasificador KNN...");
            JKNNClassifier classifier = new JKNNClassifier(3, DistanceFactory.createDistance("euclidean"));
            classifier.train(trainingSet);

            // Evaluar en un subconjunto pequeño para obtener rápidamente una idea del rendimiento
            int evalSize = Math.min(100, testingSet.size());
            System.out.println("Evaluando en " + evalSize + " ejemplos de prueba...");

            int correct = 0;
            for (int i = 0; i < evalSize; i++) {
                Object predicted = classifier.predict(testingSet.get(i).getFeatures());
                Object actual = testingSet.get(i).getLabel();

                if (predicted.equals(actual)) {
                    correct++;
                }

                System.out.printf("Ejemplo %d - Predicción: %s, Real: %s%n",
                        i, predicted, actual);
            }

            double accuracy = (double) correct / evalSize;
            System.out.printf("Precisión en el subconjunto de prueba: %.2f%%%n", accuracy * 100);

            // Visualizar algunos ejemplos
            System.out.println("Mostrando visualización de algunos dígitos...");
            Object[] predictions = new Object[evalSize];
            for (int i = 0; i < evalSize; i++) {
                predictions[i] = classifier.predict(testingSet.get(i).getFeatures());
            }

            // Mostrar una cuadrícula con los primeros 25 dígitos y sus predicciones
            MNISTVisualizer.showDigits(testingSet, predictions, 0, 25);

        } catch (IOException e) {
            System.err.println("Error al cargar el conjunto de datos MNIST: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error durante la clasificación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}