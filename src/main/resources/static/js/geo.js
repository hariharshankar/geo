var Form;
var Search;
var Map;
var Chart;

Form = {
    createSingleRowButtons: function() {
        var button = '';
        //button += "<div><span>";
        button += "<button class='add-single-row-button'>Add another row</button>";
        button += "</span><span>";
        button += "<button class='remove-single-row-button'>Delete selected row</button>";
        //button += "</span></div>"
        return button;
    },

    initPerformance: function() {
        // adjust performance value/label height to be in sync
        $(".performance-label").offset({top: $(".performance-values").offset().top})
        $(".performance-values").width($("#Annual_Performance").width() 
                                            - $(".performance-label").width()
                                            - 20); 

        // setting the scrollbar to point to 2010
        // TODO: try to calculate this dynamically
        $(".performance-values").scrollLeft(2868);
    },

    init: function() {

        $(".module-header").click( function(event) {
            var panel = $(this).next();
            var isOpen = panel.is(":visible");

            panel[isOpen? 'slideUp': 'slideDown']()
            .trigger(isOpen? 'hide': 'show');

            if (isOpen) {
                $($(this).children()[0]).switchClass("ui-icon-circle-minus", "ui-icon-circle-plus");
                $(this).removeClass("ui-widget-header");
                $(this).addClass("ui-corner-bottom");
                $(this).addClass("ui-accordion-header")
                $(this).addClass("ui-state-default")
            }
            else {
                $($(this).children()[0]).switchClass("ui-icon-circle-plus", "ui-icon-circle-minus");
                $(this).addClass("ui-widget-header");
                $(this).removeClass("ui-corner-bottom");
                $(this).removeClass("ui-accordion-header")
                $(this).removeClass("ui-state-default")
            }

            return false;
        });

        $(".single-row-module").each( function() {
            $(this).next().append(Form.createSingleRowButtons());
        });

        $(".add-single-row-button")
            .button()
            .click(function(event) {
                event.preventDefault();
                var parentTable = $(this).parent().children().first()
                var newRow = $(this).parent().children().first().find(".single-rows").first().html()
                parentTable.append(newRow)
            })

        $(".remove-single-row-button")
            .button()
            .click(function(event) {
                event.preventDefault();
                //var parentTable = $(this).parent().children().first()
                //var newRow = $(this).parent().children().first().find(".single-rows").first().html()
                //parentTable.append(newRow)
            })

        Map.init(true);
        Form.initPerformance();
    }

}


Chart = {
    plotLineDataTable: function(lineData, tableContainer) {

        var data = lineData.data
        var keys = lineData.keys
        var years = lineData.years
        var w = $("#"+tableContainer).width();

        var html = ''
        html += "<table style='width: "+w+"; height: 400px;' class='line-html-table'>";
        html += "<tr class='line-html-table-header'>";
        html += "<th class='ui-widget-header'>Year</th>";
        for (var k in keys) {
            html += "<th class='ui-widget-header'>"+keys[k]+"</th>";
        }
        html += "</tr>";

        var count = 0;
        for (var y in years) {    
            html += "<tr>";
            html += "<td class='line-html-table-years'>"+years[y]+"</td>";

            for (var k in keys) {
                html += "<td align='center'>"+data[k][count]+"</td>";
            }
            html += "</tr>";
            count++;
        }
        html += "</table>";
        $("#"+tableContainer).append(html)
    },

    plotLineChart: function(lineData, chartContainer) {

        var data = lineData.data
        var keys = lineData.keys
        var years = lineData.years

        // define dimensions of graph
        var m = [80, 85, 80, 80]; // margins
        var w = $("#"+chartContainer).width() - m[1] - m[3]; // width
        var h = $("#"+chartContainer).height() - m[0] - m[2]; // height

        // Add an SVG element with the desired dimensions and margin.
        var graph = d3.select("#"+chartContainer).append("svg:svg")
        .attr("width", w + m[1] + m[3])
        .attr("height", h + m[0] + m[2])
        .append("svg:g")
        .attr("transform", "translate(" + m[3] + "," + m[0] + ")");

        var colors = ["crimson", "steelblue", "forestgreen", "mediumvioletred", "black"] 

        setTimeout(function() {

            // X scale will fit all values from data[] within pixels 0-w
            var x = d3.scale.linear().domain([years[0], years[years.length-1]]).range([0, w]);

            // create xAxis
            var xAxis = d3.svg.axis().scale(x).tickSize(-h).tickSubdivide(true);
            // Add the x-axis.
            graph.append("svg:g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + h + ")")
            .call(xAxis);


            for (var lineCount=0; lineCount<data.length; lineCount++) {
                var y = d3.scale.linear().domain([0, d3.max(data[lineCount])]).range([h, 0]);

                // create a line function that can convert data[] into x and y points
                var line = d3.svg.line()
                // assign the X function to plot our line as we wish
                .x(function(d,i) { 
                    return x(years[i]); 
                })
                .y(function(d) { 
                    return y(d); 
                })

                // left side y axis
                if (lineCount == 0) {
                    // create left yAxis
                    var yAxisLeft = d3.svg.axis().scale(y).ticks(4).orient("left");
                    // Add the y-axis to the left
                    graph.append("svg:g")
                    .attr("class", "y axis axisLeft")
                    .attr("transform", "translate(-15,0)")
                    .style("fill", colors[lineCount])
                    .call(yAxisLeft)
                    .append("text")
                    .attr("transform", "rotate(-90)")
                    .attr("y", 6)
                    .attr("dy", ".71em")
                    .style("fill", colors[lineCount])
                    .style("text-anchor", "end")
                    .text(keys[lineCount]);
                }
                // second, right side y axis
                else {
                    // create right yAxis
                    var yAxisRight = d3.svg.axis().scale(y).ticks(6).orient("right");
                    // Add the y-axis to the right
                    graph.append("svg:g")
                    .attr("class", "y axis axisRight")
                    .attr("transform", "translate(" + (w+15) + ",0)")
                    .style("fill", colors[lineCount])
                    .call(yAxisRight)
                    .append("text")
                    .attr("transform", "rotate(-90)")
                    .attr("y", -12)
                    .attr("dy", ".71em")
                    .style("text-anchor", "end")
                    .style("fill", colors[lineCount])
                    .text(keys[lineCount]);
                }
                // add lines
                graph.append("svg:path")
                .attr("d", line(data[lineCount]))
                .style("stroke", colors[lineCount])
                .attr("class", "data");
            }
        }, 1500);
    },

    getPerformanceCumulativeChart: function() {

        var years = []
        var keys = []
        var data = []

        $.getJSON($("#performance_linechart_cumulative_json_url").attr("value"), function(d) {
            for (key in d.lines) {
                var dLine = [];
                for (year in d.lines[key]) {
                    if (years.indexOf(year) < 0)
                        years.push(year);
                    dLine.push(Math.round(d.lines[key][year]))
                }
                data.push(dLine)
                keys.push(key)
            }
        })
        .done( function() { 
            var lineData = { "years": years, "data": data, "keys": keys };
            Chart.plotLineChart(lineData, "performance_linechart_cumulative_chart")
            Chart.plotLineDataTable(lineData, "performance_linechart_cumulative_table")
        })
        .fail( function() { return null; } );
    },

    getUnitCumulativeCapacityChart: function() {

        var years = []
        var keys = []
        var data = []

        $.getJSON($("#unit_linechart_cumulative_json_url").attr("value"), function(d) {
            for (key in d.lines) {
                var dLine = [];
                for (year in d.lines[key]) {
                    if (years.indexOf(year) < 0)
                        years.push(year);
                    dLine.push(Math.round(d.lines[key][year]))
                }
                data.push(dLine)
                keys.push(key)
            }
        })
        .done( function() { 
            var lineData = { "years": years, "data": data, "keys": keys };
            Chart.plotLineChart(lineData, "unit_linechart_cumulative_chart")
            Chart.plotLineDataTable(lineData, "unit_linechart_cumulative_table")
        })
        .fail( function() { return null; } );
    },
}

Map = {
    bounds: null,
    map: null,
    mapColors: ["#FF0000", "#0008FF", "#187F00", "#9F9C00", "#FAA701", "#01DDD9"],
    overlaysCount: 0,
    mapOpacity: 0.4,
    mapStrokeWeight: 1,
    overlaysArray: [],

    addOverlayDetails: function(overlayType, overlayArea, overlayLength, overlayNumber) {
        var overlayColor = Map.mapColors[overlayNumber]
        var details = "";
        details += "<div id='overlay_"+overlayNumber+"' name='overlay_"+overlayNumber+"' >";
        details += "<span id='overlay_color_"+overlayNumber+"' name='overlay_color_"+overlayNumber+"' style='background: "+overlayColor+"; width: 10px; height: 10px;'>&nbsp;&nbsp;&nbsp;</span>&nbsp;"
        details += "<span>Area: <b>"+overlayArea+" km<sup>2</sup></b>&nbsp;|&nbsp;"
        details += "<span>Description: <input type='input' size='15' id='overlay_description_"+overlayNumber+"' name='overlay_description_"+overlayNumber+"' />";
        details += "</div>";
        $("#overlay-details").append(details);
    },

    updateOverlayData: function(overlay, overlayType) {
        overlayArea = google.maps.geometry.spherical.computeArea(overlay.getPath())
        overlayLength = google.maps.geometry.spherical.computeLength(overlay.getPath())

        Map.addOverlayDetails(overlayType, overlayArea, overlayLength, Map.overlaysCount)
    },

    addOverlayEvents: function(overlay, overlayType) {
        google.maps.event.addListener(overlay, "mouseover", function() {
            overlay.setEditable(true)
        })
        google.maps.event.addListener(overlay, "mouseout", function() {
            overlay.setEditable(false)
        })
        google.maps.event.addListener(overlay.getPath(), "insert_at", function() {
            Map.updateOverlayData(overlay, overlayType)
        })
        google.maps.event.addListener(overlay.getPath(), "set_at", function() {
            Map.updateOverlayData(overlay, overlayType)
        })
    },

    getDrawingTools: function(count) {
        var drawingManager = new google.maps.drawing.DrawingManager({
                drawingControl: true,
                drawingControlOptions: {
                    position: google.maps.ControlPosition.TOP_CENTER,
                    drawingModes: [
                        google.maps.drawing.OverlayType.POLYGON,
                        google.maps.drawing.OverlayType.POLYLINE
                    ]
                }
        });

        var overlayType;
        var overlayArea;
        var overlayLength;

        google.maps.event.addListener(drawingManager, "overlaycomplete", function(event) {
            var options = {
                fillColor: Map.mapColors[Map.overlaysCount],
                fillOpacity: Map.mapOpacity,
                strokeWeight: Map.mapStrokeWeight,
                clickable: false,
                zIndex: 1,
                editable: true 
            }
            event.overlay.setOptions({'options': options})
            overlayType = drawingManager.getDrawingMode()
        
            Map.addOverlayEvents(event.overlay, overlayType)
            Map.updateOverlayData(event.overlay, overlayType)

            Map.overlaysCount++
        })
        return drawingManager;
    },

    getMapBounds: function(point, status) {
        if( point && point.length > 0 ) {
            Map.bounds = point[0].geometry.viewport
            if (Map.bounds) {
                Map.map.fitBounds(Map.bounds)
            }
        }
    },

    drawSavedOverlays: function(overlays) {
        for (var o in overlays) {
            var overlay = overlays[o]
            var pointsString = overlay.points
            // ugly hack to eval the string as json array
            // TODO: make the overlay points in db as json array
            pointsString = pointsString.replace(/\(/g, "[")
            pointsString = pointsString.replace(/\)/g, "]")

            var points = eval(pointsString)
            var pointsArray = []
            
            for (var p=0; p<points.length; p++) {
                pointsArray.push(new google.maps.LatLng(points[p][0], points[p][1]))
            }
            Map.overlaysArray[Map.overlaysCount] = new google.maps.Polygon({
                path: pointsArray,
                fillColor: overlays[o].color,
                fillOpacity: Map.mapOpacity,
                strokeWeight: Map.mapStrokeWeight,
                map: Map.map
            })
            Map.addOverlayEvents(Map.overlaysArray[Map.overlaysCount], overlays[o].overlayType)
            Map.updateOverlayData(Map.overlaysArray[Map.overlaysCount], overlays[o].overlayType)
            Map.overlaysCount++;
        }
    },

    init: function(showDrawingTools) {

        if( document.getElementById('map-container') == undefined )
            return;

        $.getJSON($("#map_json").attr("value"), function(data) {

            var searchLocation = data.boundLocation;

            if (searchLocation != null || searchLocation != "") {
                var geoCoder = new google.maps.Geocoder();
                geoCoder.geocode({'address': searchLocation, 'partialmatch':true}, Map.getMapBounds);
            }

            Map.map = new google.maps.Map(document.getElementById('map-container'), {
                zoom: 15,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            });

            // adding drawing tools
            if (showDrawingTools) {
                Map.getDrawingTools().setMap(Map.map)
            }

            var marker;
            $.each(data.locations, function(location) {
                var lat,lng,name;
                if (data.locations[location][0] != undefined) {
                    lat = data.locations[location][0]
                    lng = data.locations[location][1]
                    name = data.locations[location][2]
                }
                else {
                    lat = data.locations[location]['lat']
                    lng = data.locations[location]['lng']

                    // drawing saved overlays
                    name = data.locations[location]['name']
                    if (data.locations[location]['overlays'].length > 0) {
                        var overlays = data.locations[location]['overlays']
                        Map.drawSavedOverlays(overlays)
                    }
                }

                // adjusting the map bounds for a single location map
                // TODO: set zoom level
                if (searchLocation == null || searchLocation == "") {
                    geoCoder.geocode({'location': new google.maps.LatLng(lat, lng)}, Map.getMapBounds)
                }
                marker = new google.maps.Marker({
                    position: new google.maps.LatLng(lat,lng),
                    map: Map.map
                });

                google.maps.event.addListener(marker, 'click', (function(marker, location) {
                    return function() {
                        var infoWindow = new google.maps.InfoWindow();
                        infoWindow.setContent(name);
                        infoWindow.open(Map.map, marker)
                    }
                })(marker, location));

            });
        });
    }
}

Search = {

    init: function() {
        Map.init();
        Chart.getPerformanceCumulativeChart();
        Chart.getUnitCumulativeCapacityChart();
        //plotLineChart(lineData);
    }
}
