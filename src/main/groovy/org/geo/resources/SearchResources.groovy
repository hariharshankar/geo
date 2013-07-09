package org.geo.resources;

import com.yammer.metrics.annotation.Timed
import org.geo.core.db.Geo
import org.geo.core.db.Select
import org.geo.core.templates.Search
import org.geo.core.templates.Summary
import org.geo.core.utils.HTMLMarkup
import org.geo.core.utils.Tokens;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType;

/**
 * @author: Harihar Shankar, 4/29/13 8:51 PM
 */

@Path("/search")
@Produces(MediaType.TEXT_HTML)
public class SearchResources {

    @GET
    @Timed
    public String getSearchResources() {
        String html = "";

        String template = Search.getTemplate();

        // left pane content
        String leftPaneContent = "";

        // geo db
        leftPaneContent += "<div id='searchDatabase_Type' class='searchSelectable'>"
        /*
        leftPaneContent += "<div class='ui-widget-header module-header'>Database</div>"
        for (String db : Tokens.GEO_DB) {
            if (db == "PowerPlants")
                leftPaneContent += "<div class='ui-widget-content ui-selected'>"+db+"</div>";
            else
                leftPaneContent += "<div class='ui-widget-content'>"+db+"</div>";
        }
        */
        leftPaneContent += "</div>"

        leftPaneContent += "<div id='searchType' class='searchSelectable'></div>"
        leftPaneContent += "<div id='searchCountry' class='searchSelectable'></div>"
        leftPaneContent += "<div id='searchState' class='searchSelectable'></div>"

        //json services for type, country, state and plant list

        leftPaneContent += HTMLMarkup.createHiddenField("jsonListService", Tokens.BASE_URL + "services/json/list", "")

        //right pane tabs
        String rightPaneTabs = "";

        rightPaneTabs += "<div id='rightPaneTabs'>"
        rightPaneTabs += "<ul>"
        rightPaneTabs += "<li><a href='"+Tokens.BASE_URL+"plantlist'>List of Resources</a></li>";
        rightPaneTabs += "<li><a href='/summary'>Summary</a></li>";
        rightPaneTabs += "<li><a href='/analysis'>Analysis</a></li>";
        rightPaneTabs += "</ul>"
        rightPaneTabs += "</div>"

        html = template.replace("{{leftPaneContent}}", leftPaneContent);
        html = html.replace("{{rightPaneContent}}", rightPaneTabs);

        return html;
    }

}
