package org.geo.core.serializations.html.templates

/**
 @author: Harihar Shankar, 8/7/13 9:33 AM
 */
class Map {
    public static String getTemplate() {

        String template = "";

        template += "<div id=\"map-metadata\">{{content}}</div>\n";
        template += "<div id=\"map-container\">\n";
        template += "</div>\n";

        return template;
    }
}
