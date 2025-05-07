package org.yisus.ia.model;

import java.util.Collections;
import java.util.List;

/**
 * Clase que representa el resultado de la búsqueda de vecinos más cercanos.
 * Contiene el punto de consulta y la lista de vecinos encontrados.
 */
public class KNNResult {
    private final DataPoint queryPoint;
    private final List<Neighbor> neighbors;

    /**
     * Constructor para el resultado de KNN.
     *
     * @param queryPoint Punto de datos de consulta
     * @param neighbors Lista de vecinos encontrados
     */
    public KNNResult(DataPoint queryPoint, List<Neighbor> neighbors) {
        this.queryPoint = queryPoint;
        this.neighbors = neighbors;
    }

    /**
     * Obtiene el punto de datos de consulta.
     *
     * @return Punto de datos de consulta
     */
    public DataPoint getQueryPoint() {
        return queryPoint;
    }

    /**
     * Obtiene la lista de vecinos.
     *
     * @return Lista inmutable de vecinos
     */
    public List<Neighbor> getNeighbors() {
        return Collections.unmodifiableList(neighbors);
    }

    /**
     * Obtiene el número de vecinos.
     *
     * @return Número de vecinos
     */
    public int getNeighborCount() {
        return neighbors.size();
    }

    /**
     * Obtiene un vecino específico por su índice.
     *
     * @param index Índice del vecino
     * @return Vecino en el índice especificado
     */
    public Neighbor getNeighbor(int index) {
        return neighbors.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KNNResult [queryPoint=").append(queryPoint);
        sb.append(", neighborCount=").append(neighbors.size());
        sb.append(", neighbors=[");

        for (int i = 0; i < neighbors.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(neighbors.get(i));
        }

        sb.append("]]");
        return sb.toString();
    }
}