package org.geo.core.templates;

import org.geo.core.utils.Tokens;

/**
 * @author: Harihar Shankar, 4/30/13 10:29 AM
 */
public class SearchResults {

    public static String getTemplate() {

        String template = "";

        template += "<!doctype html>\n<head>\n";
        template += "<meta charset=\"utf-8\" />\n";
        template += "<title>{{title}} - GEO</title>\n";
        template += "<link rel=\"stylesheet\" href=\""+ Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/css/custom-theme/jquery-ui-1.10.2.custom.min.css\">\n";
        template += "<link rel=\"stylesheet\" href=\""+Tokens.STATIC_BASE_URL+"static/css/geo.css\">\n";
        template += "<script src=\""+Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/js/jquery-1.9.1.js\"></script>\n";
        template += "<script src=\""+Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/js/jquery-ui-1.10.2.custom.min.js\"></script>\n";
        //template += "<script src=\""+Tokens.STATIC_BASE_URL+"static/d3/d3.min.js\"></script>\n";
        template += "<script src=\"http://d3js.org/d3.v3.js\"></script>";
        template += "<!-- Map -->\n" +
                "<script src=\"http://maps.googleapis.com/maps/api/js?sensor=false&amp;key=AIzaSyAveV8vQj-utZITKgBnVCbovB03hrgNvWE&amp;libraries=drawing,geometry\"></script>";
        template += "<script src=\""+Tokens.STATIC_BASE_URL+"static/js/geo.js\"></script>\n";

        template += "</head>\n" +
                "<body onload=\"Summary.init()\">\n";

        template += "<table id=\"search-results ui-widget ui-helper-reset\" style='width: 1200px; height: 100%;'>\n";

        template += "<tr style='height: 500px;'>\n";
        template += "<td id='abstract' style='width: 400px;'>";
        template += "<h3 class='ui-widget-header search-widget-header'>Summary</h3>";
        template += "</td>\n";
        template += "<td id='map' style='width: 800px;'>";
        template += "<h3 class='ui-widget-header search-widget-header'>Map</h3>";
        template += "<div id='map-container' style='height: 480px;'></div>";
        template += "</td>\n";
        template += "</tr>\n";

        template += "<tr>\n";
        template += "<td id='line-chart-heading' colspan=2>";
        template += "<h3 class='ui-widget-header search-widget-header'>Total Gigawatt Hours Generated and CO2 Emitted (Tonnes)</h3></td>\n";
        template += "</tr>\n";
        template += "<tr style='height: 500px;'>\n";
        template += "<td id='performance_linechart_cumulative_table' style='width: 400px;'></td>\n";
        template += "<td id='performance_linechart_cumulative_chart' style='width: 800px;'></td>\n";
        template += "</tr>\n";

        template += "<tr>\n";
        template += "<td id='line-chart-heading' colspan=2>";
        template += "<h3 class='ui-widget-header search-widget-header'>Cumulative Capacity Added vs Years (Aggregated over the Country)</h3></td>\n";
        template += "</tr>\n";
        template += "<tr style='height: 500px;'>\n";
        template += "<td id='unit_linechart_cumulative_table' style='width: 400px;'></td>\n";
        template += "<td id='unit_linechart_cumulative_chart' style='width: 800px;'></td>\n";
        template += "</tr>\n";

        template += "</table>";
        template += "{{content}}";
        template += "</div>\n";
        template += "</form>\n";
        template += "</body>\n</html>\n";

        return template;
    }
}
