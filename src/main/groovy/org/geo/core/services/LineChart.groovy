package org.geo.core.services;


/**
 * @author: Harihar Shankar, 5/1/13 10:16 PM
 */
public class LineChart {

    private final HashMap<String, HashMap<String, Double>> lines;

    public LineChart(final HashMap<String, HashMap<String, Double>> lines) {
        this.lines = lines;
    }

    public HashMap<String, HashMap<String, Double>> getLines() {
        return lines;
    }
}
