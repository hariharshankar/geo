package org.geo.resources;

import com.yammer.metrics.annotation.Timed;
import org.geo.core.Geo
import org.geo.core.db.Select
import org.geo.core.serializations.html.templates.SearchResults;
import org.geo.core.serializations.html.Html;
import org.geo.core.utils.Tokens;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType
import java.sql.Connection;

/**
 * @author: Harihar Shankar, 4/29/13 10:09 PM
 */

@Path("/summary")
@Produces(MediaType.TEXT_HTML)
public class GetSummaryResults {

    private Connection connection

    public GetSummaryResults(Connection connection) {
        this.connection = connection
    }

    @Timed
    @GET
    public String getSearchResults(@QueryParam("country") String cntry,
                                @QueryParam("type") String typ,
                                @QueryParam("state") String state) {

        String html = "";

        Select type = new Select();
        Geo typeGeo = type.read(connection, "Type", "", "Type LIKE '" + typ + "'", "", "0,1");
        String typeName = typeGeo.getValueForKey("Type", 0);
        String typeId = typeGeo.getValueForKey("Type_ID", 0);

        if (typeId != null && Integer.parseInt(typeId) <= 0) {
            return "Error: Invalid plant type";
        }

        Select country = new Select();
        Geo countryGeo = country.read(connection, "Country", "", "Country LIKE '" + cntry + "'", "", "0,1");
        String countryName = countryGeo.getValueForKey("Country", 0);
        String countryId = countryGeo.getValueForKey("Country_ID", 0);

        if (countryId != null && Integer.parseInt(countryId) <= 0) {
            return "Error: Invalid country name provided";
        }

        String content = "";
        content += Html.createHiddenField("summary_json", Tokens.BASE_URL+"services/json/summary?country_id="+countryId+"&type_id="+typeId, "widget_urls");
        content += Html.createHiddenField("map_json", Tokens.BASE_URL+"services/json/map?country_id="+countryId+"&type_id="+typeId, "widget_urls");
        content += Html.createHiddenField("performance_linechart_cumulative_json_url", Tokens.BASE_URL+"services/json/linechart?country_id="+countryId+"&type_id="+typeId+"&module=performance&fields=Total_Gigawatt_Hours_Generated_nbr,CO2_Emitted_(Tonnes)_nbr&chart=cumulative", "widget_urls");
        content += Html.createHiddenField("unit_linechart_cumulative_json_url", Tokens.BASE_URL+"services/json/linechart?country_id="+countryId+"&type_id="+typeId+"&module=unit&fields=Capacity_(MWe)_nbr&chart=cumulative", "widget_urls");


        String template = SearchResults.getTemplate();
        html = template.replace("{{content}}", content);
        String h = html.replace("{{title}}", typeName + " - " + countryName);

        return h;
    }
}
