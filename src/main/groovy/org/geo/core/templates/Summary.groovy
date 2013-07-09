package org.geo.core.templates;

/**
 * @author: Harihar Shankar, 4/29/13 9:06 PM
 */
public class Summary {

    public static String getTemplate() {

        String template = "";

        template += "<!doctype html>\n<head>\n";
        template += "<meta charset=\"utf-8\" />\n";
        template += "<title>Summary - GEO</title>\n";
        template += "<link rel=\"stylesheet\" href=\"static/jquery-ui-1.10.2.custom/css/custom-theme/jquery-ui-1.10.2.custom.min.css\">\n";
        template += "<link rel=\"stylesheet\" href=\"static/css/geo.css\">\n";
        template += "<script src=\"static/jquery-ui-1.10.2.custom/js/jquery-1.9.1.js\"></script>\n";
        template += "<script src=\"static/jquery-ui-1.10.2.custom/js/jquery-ui-1.10.2.custom.min.js\"></script>\n";
        template += "<script src=\"static/js/geo.js\"></script>\n";

        template += "</head>\n" +
                "<body onload=\"Summary.init()\">\n";
        template += "<form action=\"./results\" method=\"get\" style=\"padding-top: 150px;\">";
        template += "<div id=\"ui-widget ui-helper-reset\">\n";
        template += "<input style=\"margin-left: 20%; height: 1.1em; font-size: 1.1em;\" type=\"text\" size=\"75\"  name=\"q\" autocomplete=\"off\" />";
        template += "<input class=\"ui-button ui-widget ui-state-default ui-corner-all font-size: 80%;\" type=\"submit\" value=\"search\" />";
        template += "</div>\n";
        template += "</form>";
        template += "</body>\n</html>\n";

        return template;
    }
}
