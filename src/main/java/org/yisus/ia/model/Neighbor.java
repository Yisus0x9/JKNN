package org.yisus.ia.model;

/**
 * Clase que representa un vecino en el algoritmo KNN.
 * Contiene un punto de datos y su distancia al punto de consulta.
 */
public class Neighbor {
    private final DataPoint dataPoint;
    private final double distance;

    /**
     * Constructor para un vecino.
     *
     * @param dataPoint Punto de datos
     * @param distance Distancia al punto de consulta
     */
    public Neighbor(DataPoint dataPoint, double distance) {
        this.dataPoint = dataPoint;
        this.distance = distance;
    }

    /**
     * Obtiene el punto de datos.
     *
     * @return Punto de datos
     */
    public DataPoint getDataPoint() {
        return dataPoint;
    }

    /**
     * Obtiene la distancia al punto de consulta.
     *
     * @return Distancia
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Obtiene la etiqueta del punto de datos.
     *
     * @return Etiqueta
     */
    public Object getLabel() {
        return dataPoint.getLabel();
    }

    @Override
    public String toString() {
        return "Neighbor [dataPoint=" + dataPoint + ", distance=" + distance + "]";
    }
}