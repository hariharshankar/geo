package org.geo.core.templates

import org.geo.core.utils.Tokens

/**
 @author: Harihar Shankar, 7/1/13 4:01 PM
 */

class Search {
    public static String getTemplate() {
        String template = "";
        template = '''
<!doctype html>
<head>
    <meta charset="utf-8" />
    <title>Search - GEO</title>

'''
        template += "<link rel=\"stylesheet\" href=\""+ Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/css/custom-theme/jquery-ui-1.10.2.custom.min.css\">\n";
        template += "<link rel=\"stylesheet\" href=\""+ Tokens.STATIC_BASE_URL+"static/css/geo.css\">\n";
        template += "<script src=\""+ Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/js/jquery-1.9.1.js\"></script>\n";
        template += "<script src=\""+ Tokens.STATIC_BASE_URL+"static/jquery-ui-1.10.2.custom/js/jquery-ui-1.10.2.custom.min.js\"></script>\n";
        template += "<script src=\""+ Tokens.STATIC_BASE_URL+"static/js/geo.js\"></script>\n";
        template += "<script src=\"http://d3js.org/d3.v3.js\"></script>";
        /*
    <!-- css -->
    <link rel="stylesheet" href="../static/jquery-ui-1.10.2.custom/css/custom-theme/jquery-ui-1.10.2.custom.min.css">
    <link rel="stylesheet" href="../static/css/geo.css">

    <!-- jquery -->
    <script src="../static/jquery-ui-1.10.2.custom/js/jquery-1.9.1.js"></script>
    <script src="../static/jquery-ui-1.10.2.custom/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script src="../static/js/geo.js"></script>
          */
        template += '''
    <!-- Map -->
    <script src="http://maps.googleapis.com/maps/api/js?sensor=false&amp;key=AIzaSyAveV8vQj-utZITKgBnVCbovB03hrgNvWE&amp;libraries=drawing,geometry"></script>
</head>
<body onload="Search.init()">
    <div id="searchWrapper">
        <div id="searchLeftPane">
            {{leftPaneContent}}
        </div>
        <div id="searchRightPane">
            {{rightPaneContent}}
        </div>
    </div>
</body>
</html>
'''
        return template
    }

}
