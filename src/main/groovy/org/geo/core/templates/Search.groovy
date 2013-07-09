package org.geo.core.templates

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

    <!-- css -->
    <link rel="stylesheet" href="../static/jquery-ui-1.10.2.custom/css/custom-theme/jquery-ui-1.10.2.custom.min.css">
    <link rel="stylesheet" href="../static/css/geo.css">

    <!-- jquery -->
    <script src="../static/jquery-ui-1.10.2.custom/js/jquery-1.9.1.js"></script>
    <script src="../static/jquery-ui-1.10.2.custom/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script src="../static/js/geo.js"></script>

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
