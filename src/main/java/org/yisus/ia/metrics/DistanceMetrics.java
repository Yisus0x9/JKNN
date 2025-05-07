package org.yisus.ia.metrics;

import java.util.HashMap;

/**
 * Interfaz para definir métricas de distancia utilizadas en el algoritmo KNN.
 * Las clases que implementen esta interfaz deben proporcionar un método para
 * calcular la distancia entre dos vectores de características.
 */
public interface DistanceMetrics {

    /**
     * Calcula la distancia entre dos vectores de características.
     *
     * @param features1 Primer vector de características
     * @param features2 Segundo vector de características
     * @return Valor de la distancia
     */
    double calculate(HashMap<String, Double> features1, HashMap<String, Double> features2);

    /**
     * Devuelve el nombre de la métrica de distancia.
     * @return Nombre de la métrica
     */
    String getName();
}