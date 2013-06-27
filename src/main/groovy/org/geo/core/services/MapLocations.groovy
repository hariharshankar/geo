package org.geo.core.services

import org.geo.core.db.GeoPoint;


/**
 * @author: Harihar Shankar, 5/1/13 10:09 PM
 */
public class MapLocations {

    //private final ArrayList<ArrayList<String>> locations;
    private final ArrayList<GeoPoint> locations;
    private final String boundLocation;

    public MapLocations(ArrayList<GeoPoint> locations, String boundLocation) {
        this.locations = locations;
        this.boundLocation = boundLocation;
    }

    public ArrayList<GeoPoint> getLocations() {
        return locations;
    }

    public String getBoundLocation() {
        return boundLocation;
    }

}
