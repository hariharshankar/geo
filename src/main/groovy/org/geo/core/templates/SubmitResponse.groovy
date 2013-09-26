package org.geo.core.templates

import org.geo.core.utils.Tokens

/**
 @author: Harihar Shankar, 9/25/13 9:21 AM
 */
class SubmitResponse {
    public static String getTemplate() {

        String template = "";

        template += "<!doctype html>\n<head>\n";
        template += "<meta charset=\"utf-8\" />\n";
        template += "<title>Fact Sheet - GEO</title>\n";
        template += "<link rel=\"stylesheet\" href=\""+ Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/css/custom-theme/jquery-ui-1.10.2.custom.min.css\">\n";
        template += "<link rel=\"stylesheet\" href=\""+Tokens.STATIC_BASE_URL+"static/css/geo.css\">\n";
        template += "<script src=\""+Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/js/jquery-1.9.1.js\"></script>\n";
        template += "<script src=\""+Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/js/jquery-ui-1.10.2.custom.min.js\"></script>\n";
        template += "<script src=\""+Tokens.STATIC_BASE_URL+"static/js/geo.js\"></script>\n";

        template += "</head>\n";

        template += "<body>\n"

        template += "<div class=\"information ui-state-highlight ui-corner-all\">"
        template += "<strong>Submission successful.</strong>"
        template += "<p>Your submission is awaiting moderation. It will be processed in the order it was received.</p>"
        template += "</div>"

        template += "<strong>Go to: </strong>"
        template += "<ul>"
        template += "<li>{{newSubmission}}</li>"
        template += "<li>{{originalSubmission}}</li>"
        template += "<li>{{overviewPage}}</li>"
        template += "</ul>"

        template += "</body>"
        template += "</html>"

        return template
    }

}
