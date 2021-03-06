var Form;
var Search;
var Map;
var Chart;
var Summary;
var Geo;

Geo = {
    searchDatabase_Type_Default: "PowerPlants",
    searchType_Default: "coal",
    searchCountry_Default: "india", 
    searchState_Default: "all",
    searchTab_Default: "summary",

    getUrlParameter: function(name) {
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
    },

    getPageUrl: function() {
        return window.location.href.split("?")[0]
    }
} 

Search = {

    searchDatabase_Type: "",
    searchType: "",
    searchCountry: "",
    searchState: "",
    searchTab: "",

    selectableIds: [],

    createHtmlForSelectables: function (element, data) {
        select = "";
        var k = ""
        if (data["keys"].length == 2)
            k = data['keys'][1]
        else
            k = data['keys'][0]

        var elementContentClass = element + "Content" 
            
        select += "<select data-placeholder='Choose a "+k+"'class='chosen-select' style='width: 90%;'>"
        for (var v in data['values']) {
            var value = data['values'][v]
            var v = ""
            if (value.length == 2)
                v = value[1]
            else
                v = value[0]
            if (v && Search[element].toLowerCase() == v.toLowerCase()) 
                select += "<option class='ui-widget-content ui-selected "+elementContentClass+"' value='"+v+"' selected='selected'>"+v+"</option>";
            else
                select += "<option class='ui-widget-content "+elementContentClass+"' value='"+v+"'>"+v+"</option>";
        }
        select += "</select>"
        $(select).insertAfter("#"+element)
        $(".chosen-select").chosen()

        heading = "<h3 class='ui-widget-header module-header searchSelectableHeader'>"+k+"</h3>";
        $("#"+element).before(heading)
        $("."+elementContentClass).click(function() {
            $(this).addClass("ui-selected").siblings().removeClass("ui-selected")
        })
    },
    /*
    createHtmlForSelectables: function (element, data) {
        select = "";
        var k = ""
        if (data["keys"].length == 2)
            k = data['keys'][1]
        else
            k = data['keys'][0]

        var elementContentClass = element + "Content" 
            
        for (var v in data['values']) {
            var value = data['values'][v]
            var v = ""
            if (value.length == 2)
                v = value[1]
            else
                v = value[0]
            if (Search[element].toLowerCase() == v.toLowerCase()) 
                select += "<div class='ui-widget-content ui-selected "+elementContentClass+"'>"+v+"</div>";
            else
                select += "<div class='ui-widget-content "+elementContentClass+"'>"+v+"</div>";
        }
        $("#"+element).append(select)

        heading = "<h3 class='ui-widget-header module-header searchSelectableHeader'>"+k+"</h3>";
        $("#"+element).before(heading)
        $("."+elementContentClass).click(function() {
            $(this).addClass("ui-selected").siblings().removeClass("ui-selected")
        })
    },
    */
    getUserValues: function() {

        params = {}
        $(".searchSelectable").each(function() {
            var key = $(this).attr("id").replace("search", "").toLowerCase()
            /*
            var value = $(this).children(".ui-selected").text()
            if (value != "") {
                params[key] = value.toLowerCase()
            }
            */
            var value = $(this).next().next().find(".chosen-single").text()
            if (value != "") {
                params[key] = value.toLowerCase()
            }
        })
        return params
    },

    getSelectValues: function (reqUrl, reqData, callbackElement) {
        $.ajax({
            type: "POST",
            url: reqUrl,
            data: reqData,
            dataType: "json",
            contentType: "application/json; charset=utf-8",            
            success: function(data, textStatus, jqXHR) {
                Search.createHtmlForSelectables(callbackElement, data)
            }
        })    
    },

    createRightPaneTabs: function() {
        $( "#rightPaneTabs" ).tabs({
            load: function (event, ui) {
                if (ui.tab[0].textContent == "Summary") {
                    Summary.init()
                }
                if (ui.tab[0].textContent == "Map") {
                    var map_container = $("#map-container")
                    map_container.css("weight", map_container.parent().width()-5)
                    map_container.css("height", "800")
                    map_container.css("padding", "1px")

                    Map.init(false)
                }
            },
            beforeLoad: function (event, ui) {
                // reset the url to take new config params
                params = {}
                params['type'] = Geo.getUrlParameter("type")
                params['country'] = Geo.getUrlParameter("country")
                params['state'] = Geo.getUrlParameter("state")

                var tab = ui.tab.text().split(" ")[0].toLowerCase()
                if (tab != Geo.getUrlParameter("tab")) {
                    event.preventDefault()
                    if (!event.originalEvent) {
                        console.log("cancelled: " + tab)
                        return
                    }
                    params['tab'] = tab
                    var url = Geo.getPageUrl() + "?" + $.param(params)
                    document.location.href = url
                }
                ui.ajaxSettings.url = ui.ajaxSettings.url + "?" + $.param(params)
            }
        });
        var reqTab = Search.searchTab
        var i=0
        $("#rightPaneTabs ul li").each( function() {
            var tabTitle = $(this).text().toLowerCase()
            if(tabTitle.search(reqTab.toLowerCase()) >= 0) {
                $("#rightPaneTabs").tabs({active: i})
            }
            i++
        })
    },

    createSelectables: function(t) {
        if ($(t).attr("id") == "searchUpdateButton") {
            Search.createRightPaneTabs()
            return;
        }
        var type = $(t).attr("id").replace("search", "")
        var postUrl = $("#jsonListService").attr("value")
        var data = {}

        data["return_type"] = type
        var sel = Search.selectableIds

        for (var s in sel) {
            var prevType = sel[s].replace("search", "")
            var prevValue = ""
            if ($("#"+ sel[s] +" .ui-selected").text() != "")
                prevValue = $("#"+ sel[s] +" .ui-selected").text()
            else 
                prevValue = Search[sel[s]]
            data[prevType] = [prevValue]
        }
        var postData = JSON.stringify(data)
        Search.getSelectValues(postUrl, postData, "search"+type)
            
        var id = $(t).attr("id")

        Search.selectableIds.push($(t).attr("id"))
        Search.createSelectables($(t).next())
        
        
        /*
        $(t).selectable({
            selected: function(event, ui) {
            },
            create: function(event, ui) {
                Search.selectableIds.push($(t).attr("id"))
                Search.createSelectables($(t).next())
            }
        });
        */
    },
    
    plantListPostData: {},

    init: function() {
        var shdReloadPage = false
        var params = {}
        if (Geo.getUrlParameter("database_type") == null) {
            params['database_type'] = Geo.searchDatabase_Type_Default 
            shdReloadPage = true
        }
        else {
            params['database_type'] = Geo.getUrlParameter("database_type")
        }

        if (Geo.getUrlParameter("type") == null) {
            params['type'] = Geo.searchType_Default
            shdReloadPage = true
        }
        else {
            params['type'] = Geo.getUrlParameter("type")
        }
        if (Geo.getUrlParameter("country") == null) {
            params['country'] = Geo.searchCountry_Default
            shdReloadPage = true
        }
        else {
            params['country'] = Geo.getUrlParameter("country")
        }
        if (Geo.getUrlParameter("state") == null) {
            params['state'] = Geo.searchState_Default
            shdReloadPage = true
        }
        else {
            params['state'] = Geo.getUrlParameter("state")
        }

        if (Geo.getUrlParameter("tab") == null) {
            params['tab'] = Geo.searchTab_Default
            shdReloadPage = true
        }
        else {
            params['tab'] = Geo.getUrlParameter("tab")
        }
        if (shdReloadPage) {
            window.location.href = window.location.href.split("?")[0] + "?" + $.param(params)
            return;
        }
        Search.searchDatabase_Type = params['database_type']
        Search.searchType = params['type']
        Search.searchCountry = params['country']
        Search.searchState = params['state']
        Search.searchTab = params['tab']

        Search.createSelectables($(".searchSelectable").first())

        $("#updateSearch")
        .button()
        .click (function(event) {
            event.preventDefault()
            params = Search.getUserValues()
            base_url = Geo.getPageUrl()
            updateUrl = base_url + "?" + $.param(params)
            window.location.href = updateUrl
        })
    }
}


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

        /*
        $(".single-rows").parent().each( function() {
            $(this).children().first().each( function() {
                $(this).first().prepend("<th></th>")
            })
        })

        $(".single-rows").each( function() {
            var checkbox = "<td><input type='checkbox'></td>"
            $(this).prepend(checkbox)
        })
        */

        $(".add-single-row-button")
            .button()
            .click(function(event) {
                event.preventDefault();
                //var parentTable = $(this).parent().children().first()
                $(this).parent().children().each( function() {
                    if ($(this)[0].tagName.toLowerCase() == "table") {
                        var parentTable = $(this) 
                        var newRow = $(parentTable).find(".single-rows").last().html()
                        newRow = "<tr>" + newRow + "</tr>"
                        
                        /*
                        var indexElement = $(newRow).children().first()
                        var index = parseInt(indexElement.html()) + 1
                        indexElement.html(index.toString())
                        */
                        
                        var index = 0;
                        $(parentTable).children().first().children().each( function() {
                            if ($(this).attr && $(this).attr("type") == "hidden") {
                                index = parseInt($(this).attr("value")) + 1
                                $(this).attr("value", index.toString())
                            }
                        })
                        var n = ""
                        for (var i=0, c; c=$(newRow).children()[i]; i++) {
                            if ($(c).children().length > 0) {
                                var t = $(c).children().first()
                                $(t).attr("value", "")
                                if ($(t).attr("name")) {
                                    var elementName = $(t).attr("name").split("_###_")[0] + "_###_" + index
                                    $(t).attr("name", elementName)
                                    $(t).attr("id", elementName)
                                    n += "<td>" + $(t)[0].outerHTML + "</td>"
                                }
                            }
                            else if (i == 1) {
                                n += "<td>" + index + "</td>"
                            }
                        }

                        $("<tr class='single-rows'>"+n+"</tr>").insertAfter($(parentTable).find(".single-rows").last())
                    }
                })
            })


        $(".remove-single-row-button")
            .button()
            .click(function(event) {
                event.preventDefault();
                $(this).parent().parent().find(".single-rows").each( function() {
                    if ($(this).children().first().children().first()[0].checked) {
                        $(this).parent().parent().children().first().children().each( function() {
                            if ($(this).attr && $(this).attr("type") == "hidden") {
                                var v = parseInt($(this).attr("value")) - 1
                                $(this).attr("value", v.toString())
                            }
                        })
                        $(this).remove()
                    }
                })
                var i =0
                $(this).parent().parent().find(".single-rows").each( function() {
                    i++
                    var k=0;
                    $(this).children().each( function() {
                        k++
                        var ele = $(this).children().first()
                        var id = ele.attr("id")
                        if (id && id.search("_###_") > 0) {
                            ele.attr("id", id.split("_###_")[0] + "_###_" + i)
                            ele.attr("name", id.split("_###_")[0] + "_###_" + i)
                        }
                        else if (k == 2) {
                            ele.context.innerHTML = i
                        }
                    })
                })
                    
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
        //html += "<th class='ui-widget-header'>Year</th>";
        for (var k in keys) {
            html += "<th class='ui-widget-header'>"+keys[k]+"</th>";
        }
        html += "</tr>";

        var count = 0;
        
        for (var y in years) {    
            html += "<tr>";
            html += "<td class='line-html-table-years'>"+years[y]+"</td>";

            // keys array contain "years" and data array does not... 
            // TODO: better data structure
            for (k=0; k <keys.length-1; k++) {
                html += "<td align='center'>"+data[k][count]+"</td>";
            }
            html += "</tr>";
            count++;
        }
        
        html += "</table>";
        $("#"+tableContainer).append(html)
    },

    /*
    plotPieChart: function(pieData, chartContainer) {

        var data = []
        for (var k in pieData) {
            data.push(pieData[k])
        }

        // define dimensions of graph
        var m = [80, 85, 80, 80]; // margins
        var w = $("#"+chartContainer).width() - m[1] - m[3]; // width
        var h = $("#"+chartContainer).height() - m[0] - m[2]; // height
        var radius = Math.min(w, h) / 2;
        var color = ["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00", "crimson", "steelblue", "forestgreen"]

        var arc = d3.svg.arc()
        .outerRadius(radius - 10)
        .innerRadius(0);

        var pie = d3.layout.pie()
        .sort(null)
        .value(function(d) { return d; });

        var svg = d3.select("#"+chartContainer).append("svg")
        .attr("width", w)
        .attr("height", h)
        .append("g")
        .attr("transform", "translate(" + w / 2 + "," + h / 2 + ")");

        var g = svg.selectAll(".arc")
        .data(pie(data))
        .enter().append("g")
        .attr("class", "arc");

        var dCount = 0
        g.append("path")
        .attr("d", arc)
        .style("fill", function(d) { dCount++; console.log(color[dCount]);return color[dCount]; });

        g.append("text")
        .attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
        .attr("dy", ".35em")
        .style("text-anchor", "middle")
        .text(function(d) { return d.data; });
    },
    */
    /*
    plotBubbleChart: function(bubbleData, chartContainer) {
        var values = []
        for (var k in bubbleData) {
            values.push({"value": bubbleData[k], "key": k, "package": "type"})
        }
        var m = [80, 85, 80, 80]; // margins
        var w = $("#"+chartContainer).width() - m[1] - m[3]; // width
        var h = $("#"+chartContainer).height() - m[0] - m[2]; // height
        
        //var diameter = Math.min(w, h);
        var diameter = 500

        format = d3.format(",d"),
        color = ["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00", "crimson", "steelblue", "forestgreen"];

        var bubble = d3.layout.pack()
            .sort(null)
            .size([diameter, diameter])
            //.padding(1.5);

        var svg = d3.select("#"+chartContainer).append("svg")
            .attr("width", diameter)
            .attr("height", diameter)
            .attr("class", "bubble");

        var node = svg.selectAll(".node")
            .data(bubble.nodes({children: values}))
            .enter().append("g")
            .attr("class", "node")
            .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

            node.append("title")
            .text(function(d) { console.log(d);return d.key + ": " + format(d.value); });

            var dCount = 0
            node.append("circle")
            .attr("r", function(d) { return d.r; })
            .style("fill", function(d) { return color[dCount++]; });

            node.append("text")
            .attr("dy", ".3em")
            .style("text-anchor", "middle")
            .text(function(d) { return d.key; });

        // Returns a flattened hierarchy containing all leaf nodes under the root.
        function classes(root) {
            var classes = [];

            function recurse(name, node) {
                if (node.children) node.children.forEach(function(child) { recurse(node.name, child); });
                else classes.push({packageName: name, className: node.name, value: node.size});
            }

            recurse(null, root);
            return {children: classes};
        }

        d3.select(self.frameElement).style("height", diameter + "px");
    },
    */

    plotBubbleChart: function(bubbleData, chartContainer) {
        var values = []
        for (var k in bubbleData) {
            values.push({"size": bubbleData[k], "name": k})
        }
        var json = {
            "name": "type",
            "children": values
        };

        var r = 500,
        format = d3.format(",d"),
        fill = d3.scale.category20c();

        var bubble = d3.layout.pack()
        .sort(null)
        .size([r, r])
        .padding(1.5);

        var vis = d3.select("#"+chartContainer).append("svg")
        .attr("width", r)
        .attr("height", r)
        .attr("class", "bubble");


        var node = vis.selectAll("g.node")
        .data(bubble.nodes(classes(json))
        .filter(function(d) { return !d.children; }))
        .enter().append("g")
        .attr("class", "node")
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

        node.append("title")
        .text(function(d) { return d.className + ": " + format(d.value); });

        node.append("circle")
        .attr("r", function(d) { return d.r; })
        .style("fill", function(d) { return fill(d.packageName); });

        node.append("text")
        .attr("text-anchor", "middle")
        .attr("dy", ".3em")
        .text(function(d) { return d.className.substring(0, d.r / 3); });

        // Returns a flattened hierarchy containing all leaf nodes under the root.
        function classes(root) {
            var classes = [];

            function recurse(name, node) {
                if (node.children) node.children.forEach(function(child) { recurse(node.name, child); });
                else classes.push({packageName: name, className: node.name, value: node.size});
            }

            recurse(null, root);
            return {children: classes};
        }
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


    parseChartData: function(d) {
        var keys = []
        var data = []
        var years = []
        for (k=1; k<d.keys.length; k++) {
            var dLine = [];
            for (v=0; v<d.values.length; v++) {
                if (years.indexOf(d.values[v][0]) < 0)
                    years.push(d.values[v][0]);
                dLine.push(Math.round(d.values[v][k]))
            }
            data.push(dLine)
            keys = d.keys
        }
        return [keys, years, data]
    },

    getPerformanceCumulativeChart: function() {

        var years = []
        var keys = []
        var data = []

        $.getJSON($("#performance_linechart_cumulative_json_url").attr("value"), function(d) {
            var arr = Chart.parseChartData(d)
            keys = arr[0]
            years = arr[1]
            data = arr[2]
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
            var arr = Chart.parseChartData(d)
            keys = arr[0]
            years = arr[1]
            data = arr[2]
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

    addOverlayDetails: function(overlayType, overlayArea, overlayLength, overlayNumber, points) {
        var overlayColor = Map.mapColors[overlayNumber]
        var details = "";
        overlayNumber = parseInt(overlayNumber) + 1
        details += "<div id='overlay_"+overlayNumber+"' name='overlay_"+overlayNumber+"' >";
        details += "<span id='overlay_color_"+overlayNumber+"' name='overlay_color_"+overlayNumber+"' style='background: "+overlayColor+"; width: 10px; height: 10px;'>&nbsp;&nbsp;&nbsp;</span>&nbsp;"
        details += "<span>Area: <b>"+overlayArea+" km<sup>2</sup></b>&nbsp;|&nbsp;"
        details += "<span>Description: <input type='input' size='15' id='Overlay_Name_###_"+overlayNumber+"' name='Overlay_Name_###_"+overlayNumber+"' />";
        details += "</div>";
        details += this.createHiddenHtmlElement("Color_###_"+overlayNumber, overlayColor)
        details += this.createHiddenHtmlElement("Points_###_"+overlayNumber, points)
        details += this.createHiddenHtmlElement("Overlay_Type_###_"+overlayNumber, overlayType)
        overlayCounter = Map.overlaysCount + 1
        if (!document.getElementById("numberOfCoal_Overlays")) {
            details += this.createHiddenHtmlElement("numberOfCoal_Overlays", overlayCounter)
        }
        else {
            $("numberOfCoal_Overlays").attr("value", overlayCounter)
        }

        $("#overlay-details").append(details);
    },

    updateOverlayData: function(overlay, overlayType, overlayNumber) {
        var overlayPoints = overlay.getPath().getArray()
        var points = ""
        for (var i=0,p; p=overlayPoints[i]; i++) {
            points += "[" + p.lat() + "," + p.lng() + "],"
        }
        // removing the trailing comma
        points = points.substr(0, points.length-1)

        points = "[" + points + "]"
        
        overlayArea = Math.round(google.maps.geometry.spherical.computeArea(overlay.getPath()) / 10000) / 100
        overlayLength = google.maps.geometry.spherical.computeLength(overlay.getPath())

        Map.addOverlayDetails(overlayType, overlayArea, overlayLength, Map.overlaysCount, points)
    },

    addOverlayEvents: function(overlay, overlayType, overlayNumber) {
        google.maps.event.addListener(overlay, "mouseover", function() {
            overlay.setEditable(true)
        })
        google.maps.event.addListener(overlay, "mouseout", function() {
            overlay.setEditable(false)
        })
        google.maps.event.addListener(overlay.getPath(), "insert_at", function() {
            Map.updateOverlayData(overlay, overlayType, overlayNumber)
        })
        google.maps.event.addListener(overlay.getPath(), "set_at", function() {
            Map.updateOverlayData(overlay, overlayType, overlayNumber)
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
        
            Map.addOverlayEvents(event.overlay, overlayType, Map.overlaysCount)
            Map.updateOverlayData(event.overlay, overlayType, Map.overlaysCount)

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

    createHiddenHtmlElement: function(name, value) {
        return '<input type="hidden" name="'+name+'" id="'+name+'" value="'+value+'" />';
    },

    drawSavedOverlays: function(overlays) {
        var overlayHtml = []
        for (var o in overlays) {
            var overlay = overlays[o]
            var pointsString = overlay.points

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
            var overlayNumber = parseInt(o) + 1
            /*
            overlayHtml.push(this.createHiddenHtmlElement("Color_###_"+overlayNumber, overlays[o].color))
            overlayHtml.push(this.createHiddenHtmlElement("Opacity_###_"+overlayNumber, overlays[o].opacity))
            overlayHtml.push(this.createHiddenHtmlElement("Weight_###_"+overlayNumber, overlays[o].weight))
            overlayHtml.push(this.createHiddenHtmlElement("Points_###_"+overlayNumber, overlays[o].points))
            overlayHtml.push(this.createHiddenHtmlElement("Overlay_Type_###_"+overlayNumber, overlays[o].overlayType))
            overlayHtml.push(this.createHiddenHtmlElement("Overlay_Name_###_"+overlayNumber, overlays[o].overlayName))
            */
            Map.addOverlayEvents(Map.overlaysArray[Map.overlaysCount], overlays[o].overlayType)
            Map.updateOverlayData(Map.overlaysArray[Map.overlaysCount], overlays[o].overlayType, overlayNumber)
            Map.overlaysCount++;
        }
        //overlayHtml.push(this.createHiddenHtmlElement("numberOfCoal_Overlays", overlays.length))
        //$("#overlay-details").append(overlayHtml.join(""))
    },

    showLatLng: function(lat, lng) {
        var html = ""
        html += "<b>Latitude:</b>&nbsp;<input type='text' size='10' name='Latitude_Start' id='Latitude_Start' value='"+lat+"' />&nbsp;"
        html += "<b>Longitude:</b>&nbsp;<input type='text' size='10' name='Longitude_Start' id='Longitude_Start' value='"+lng+"' />&nbsp;"

        html += "<br/>"

        $("#overlay-details").prepend(html)
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

            $("#map-resize").resizable({
                maxWidth: $("#map-resize").width(),
                minWidth: $("#map-resize").width(),
                maxHeight: 900,
                minHeight: $("#map-resize").height() + 15,
                stop: function(e, ui) {
                    $("#map-container").height( $("#map-resize").height() );
                    google.maps.event.trigger(Map.map, 'resize');
                }
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
                    if (data.locations[location]['overlays'] != null) {
                        if (data.locations[location]['overlays'].length > 0) {
                            var overlays = data.locations[location]['overlays']
                            Map.drawSavedOverlays(overlays)
                        }
                    }
                }

                // adjusting the map bounds for a single location map
                // TODO: set zoom level
                if (searchLocation == null || searchLocation == "") {
                    Map.showLatLng(lat, lng)
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

Summary = {

    displaySummaryResults: function(numberOfPlants, cumulativeCapacity, tableContainer) {

        var w = $("#"+tableContainer).width();

        var html = ''
        html += "<table style='width: 50%; height: 100%; overflow: auto; margin: auto;' class='line-html-table'>";
        html += "<tr>";
        html += "<td class='line-html-table-years'>Total Number of Plants: </td>";
        html += "<td align='right'>"+numberOfPlants+"</td>";
        html += "</tr>";
        
        html += "<tr>";
        html += "<td class='line-html-table-years'>Total Cumulative Capacity: </td>";
        html += "<td align='right'>"+cumulativeCapacity+"</td>";
        html += "</tr>";
        html += "<tr style='height: 10px;' />";
        html += "<tr/>";

        html += "<tr>";
        html += "<td colspan='2' class='line-html-table-years' style='font-weight: bold; font-size: 1.2em;'>Cumulative Capacity by Type (in MWe)</td>";
        html += "</tr>";

        var url = $("#summary_json").attr("value")
        url = url.replace(new RegExp(".type_id=[0-9]{1,2}"), "")
        $.getJSON(url, function(d) {
            cumulativeCapacity = {}
            numberOfPlants = {}
            for (j=0,k; k=d.keys[j]; j++) {
                if (k.search("Cumulative_Capacity") == 0) {
                    for (i=0; i<d.values.length; i++) {
                        var type = ""
                        for (t=0; t<Summary.typeValues.values.length; t++) {
                            if (Summary.typeValues.values[t][0] == d.values[i][0]) {
                                type = Summary.typeValues.values[t][1]
                                break
                            }
                        }
                        cumulativeCapacity[type] = d.values[i][j]
                        
                        html += "<tr>";
                        html += "<td class='line-html-table-years'>"+type+": </td>";
                        html += "<td align='right'>"+d.values[i][j]+"</td>";
                        html += "</tr>";
        
                    }
                }
                else if (k.search("Number_of_Plants") == 0) {
                    for (i=0; i<d.values.length; i++) {
                        numberOfPlants[d.values[i][0]] = d.values[i][j]
                    }
                }
            }
            html += "</table>";
            $("#"+tableContainer).append(html)
            //Chart.plotBubbleChart(cumulativeCapacity, tableContainer)
        })
    },

    typeValues: {},

    init: function() {

        var reqUrl = $("#jsonListService").attr("value")
        var reqData = {}
        reqData["return_type"] = "Type"
        reqData["Database_Type"] = ["powerplants"]
        $.ajax({
            type: "POST",
            url: reqUrl,
            data: JSON.stringify(reqData),
            dataType: "json",
            contentType: "application/json; charset=utf-8",            
            success: function(data, textStatus, jqXHR) {
                Summary.typeValues = data
            }
        })    


        $.getJSON($("#summary_json").attr("value"), function(d) {
            var numberOfPlants = 0
            var cumulativeCapacity = 0
            var newCapAddedIndex = 0
            var annGWhGenIndex = 0
            var annCO2EmIndex = 0

            for (var i=0, k; k=d.keys[i]; i++) {
                if (k.search("New_Capacity_Added") == 0) {
                    newCapAddedIndex = i
                }
                else if (k.search("Annual_Gigawatt_Hours_Generated") == 0) {
                    annGWhGenIndex = i
                }
                else if (k.search("Annual_CO2_Emitted") == 0) {
                    annCO2EmIndex = i
                }
                else if (k.search("Cumulative_Capacity") == 0) {
                    cumulativeCapacity = d.values[0][i]
                }
                else if (k.search("Number_of_Plants") == 0) {
                    numberOfPlants = d.values[0][i]
                }
            }

            Summary.displaySummaryResults(numberOfPlants, cumulativeCapacity, "summary-overview")

            var newCapArr = Chart.parseChartData($.parseJSON(d.values[0][newCapAddedIndex]))
            newCapKeys = newCapArr[0]
            newCapYears = newCapArr[1]
            newCapData = newCapArr[2]

            var perfCumulativeChartData = []
            var perfCumulativeChartKeys = []
            var annCO2Em = $.parseJSON(d.values[0][annCO2EmIndex])
            var annGWhGen = $.parseJSON(d.values[0][annGWhGenIndex])
            perfCumulativeChartKeys = annGWhGen.keys
            perfCumulativeChartKeys.push(annCO2Em.keys[1])

            for (v in annGWhGen.values) {
                perfCumulativeChartData.push([annGWhGen.values[v][0], annGWhGen.values[v][1], annCO2Em.values[v][1]])
            }

            var arr = Chart.parseChartData({"keys": perfCumulativeChartKeys, "values": perfCumulativeChartData})
            cumKeys = arr[0]
            cumYears = arr[1]
            cumData = arr[2]
        }) 
        .done( function() { 
            var lineData = { "years": cumYears, "data": cumData, "keys": cumKeys };
            Chart.plotLineChart(lineData, "performance_linechart_cumulative_chart")
            Chart.plotLineDataTable(lineData, "performance_linechart_cumulative_table")
            
            lineData = { "years": newCapYears, "data": newCapData, "keys": newCapKeys };
            Chart.plotLineChart(lineData, "unit_linechart_cumulative_chart")
            Chart.plotLineDataTable(lineData, "unit_linechart_cumulative_table")
        })
        .fail( function() { return null; } );
    }
}
