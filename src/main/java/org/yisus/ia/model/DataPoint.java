package org.yisus.ia.model;

import java.util.Collection;
import java.util.HashMap;

/**
 * Representa un punto de datos con características y etiqueta.
 * Contiene un arreglo de las caracteristicas por medio de clave: nombre de la Caracteristica, valor:Valor asignado
 * Etiqueta que contiene el dataPoint
 * Esta clase es la unidad básica de datos para el algoritmo KNN.
 */
public class DataPoint {
    private HashMap<String,Double> features;    // Vector de características
    private Object label;         // Etiqueta de clase (puede ser String, Integer, etc.)
    private int id;               // Identificador único opcional

    /**
     * Constructor para un punto de datos con características y etiqueta.
     *
     * @param features Vector de características
     * @param label Etiqueta de clase
     */
    public DataPoint(HashMap<String,Double> features, Object label) {
        this.features = features;
        this.label = label;
        this.id = -1;  // Valor por defecto si no se especifica
    }

    /**
     * Constructor para un punto de datos con características, etiqueta e identificador.
     *
     * @param features Vector de características
     * @param label Etiqueta de clase
     * @param id Identificador único
     */
    public DataPoint(HashMap<String,Double> features, Object label, int id) {
        this.features = features;
        this.label = label;
        this.id = id;
    }

    /**
     * Obtiene el vector de características.
     *
     * @return Vector de características
     */
    public HashMap<String,Double> getFeatures() {
        return features;
    }

    /**
     * Establece el vector de características.
     *
     * @param features Nuevo vector de características
     */
    public void setFeatures(HashMap<String,Double> features) {
        this.features = features;
    }

    /**
     * Obtiene la etiqueta de clase.
     *
     * @return Etiqueta de clase
     */
    public Object getLabel() {
        return label;
    }

    /**
     * Establece la etiqueta de clase.
     *
     * @param label Nueva etiqueta de clase
     */
    public void setLabel(Object label) {
        this.label = label;
    }

    /**
     * Obtiene el identificador único.
     *
     * @return Identificador único
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador único.
     *
     * @param id Nuevo identificador único
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el valor de una característica específica.
     *
     * @param feature Índice de la característica
     * @return Valor de la característica
     */
    public double getFeature(String feature) {
        return features.get(feature);
    }

    /**
     * Establece el valor de una característica específica.
     *
     * @param feature Índice de la característica
     * @param value Nuevo valor de la característica
     */
    public void setFeature(String feature, double value) {
        if (features.containsKey(feature)) {
            throw new IndexOutOfBoundsException("característica no existente");
        }
        features.replace(feature, value);
    }

    /**
     * Obtiene el número de características.
     *
     * @return Número de características
     */
    public int getFeatureCount() {
        return features.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataPoint [id=").append(id).append(", features=[");
        Collection<Double> f = features.values();
        for (int i = 0; i < features.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(features.get(i));
        }
        sb.append("], label=").append(label).append("]");
        return sb.toString();
    }
}