package org.geo.core.serializations.html.templates;

import org.geo.core.utils.Tokens;

/**
 * @author: Harihar Shankar, 4/22/13 2:22 PM
 */
public class Form {

    public static String getTemplate() {
        String template = "";

        template += "<!doctype html>\n<head>\n";
        template += "<meta charset=\"utf-8\" />\n";
        template += "<title>{{title}} - GEO</title>\n";
        template += "<link rel=\"stylesheet\" href=\""+ Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/css/custom-theme/jquery-ui-1.10.2.custom.min.css\">\n";
        template += "<link rel=\"stylesheet\" href=\""+ Tokens.STATIC_BASE_URL+"static/css/geo.css\">\n";
        template += "<script src=\""+ Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/js/jquery-1.9.1.js\"></script>\n";
        template += "<script src=\""+ Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/js/jquery-ui-1.10.2.custom.min.js\"></script>\n";
        template += "<script src=\""+ Tokens.STATIC_BASE_URL+"static/js/geo.js\"></script>\n";
        template += "<!-- Map -->\n" +
                "<script src=\"http://maps.googleapis.com/maps/api/js?sensor=false&amp;key=AIzaSyAveV8vQj-utZITKgBnVCbovB03hrgNvWE&amp;libraries=drawing,geometry,projection\"></script>";
        template += "</head>\n" +
                "<body onload=\"Form.init()\">\n";
        template += "<form action=\"factsheet/submit\" method=\"POST\" id=\"form\">\n";
        template += "<div id=\"all-modules ui-widget ui-helper-reset\">\n";
        template += "{{content}}\n";
        template += "<input type=\"submit\" id=\"submit\" name=\"submit\" value=\"Submit\"/>\n";
        template += "</div>\n";
        template += "</form>\n";
        template += "</body>\n</html>\n";

        return template;
    }



    public static String getModuleTemplate() {
        String template = "";
        template += "<div class=\"ui-widget-header ui-corner-top module-header {{module_header_class}}\">" +
                "<span class=\"ui-icon ui-icon-circle-minus\" style=\"display: inline-block; float: left;\"></span>" +
                "{{module_heading}}</div>\n" +
                "<div class=\"module-content ui-corner-bottom ui-widget-content\" id=\"{{module_id}}\">{{module_content}}</div>\n";

        return template;
    }
}
