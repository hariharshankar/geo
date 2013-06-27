package org.geo.core.db

/**
 @author: Harihar Shankar, 6/20/13 2:20 PM
 */

class GeoPoint implements Serializable {
    private double lat;
    private double lng;
    private String name;
    private List<Map<String,String>> overlays;

    public GeoPoint(double lat, double lng, String name, List<Map<String,String>> overlays) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.overlays = overlays;
    }

    public GeoPoint(double lat, double lng, String name) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    public GeoPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

    public String getName() {
        return name;
    }

    public List<Map<String,String>> getOverlays() {
        return overlays;
    }

    /*
    @Override
    public String toString(){
        return "("+lat+", "+lng+")";
    }
    */

    /**
     * We consider that two point are equals if both latitude and longitude are "nearly" the same.
     * With a precision of 1e-3 degree
     */

    @Override
    public boolean equals(Object o){
        if ( ! (o instanceof GeoPoint)) return false;
        GeoPoint that = (GeoPoint)o;
        if (Math.abs( that.getLat() - lat) > 0.001) return false;
        if (Math.abs( that.getLng() - lng) > 0.001) return false;
        return true;
    }


}

