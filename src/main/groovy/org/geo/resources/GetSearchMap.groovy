package org.geo.resources

import com.yammer.metrics.annotation.Timed
import org.geo.core.db.Geo
import org.geo.core.db.Select
import org.geo.core.serializations.html.Html
import org.geo.core.utils.Tokens

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

/**
 @author: Harihar Shankar, 8/7/13 9:40 AM
 */

@Path("/map")
@Produces(MediaType.TEXT_HTML)
class GetSearchMap {

    @Timed
    @GET
    public String getSearchMap(@QueryParam("country") String cntry,
                                @QueryParam("type") String typ,
                                @QueryParam("state") String state) {

        Select type = new Select();
        Geo typeGeo = type.read("Type", "", "Type LIKE '" + typ + "'", "", "0,1");
        String typeName = typeGeo.getValueForKey("Type", 0);
        String typeId = typeGeo.getValueForKey("Type_ID", 0);

        if (typeId != null && Integer.parseInt(typeId) <= 0) {
            return "Error: Invalid plant type";
        }

        Select country = new Select();
        Geo countryGeo = country.read("Country", "", "Country LIKE '" + cntry + "'", "", "0,1");
        String countryName = countryGeo.getValueForKey("Country", 0);
        String countryId = countryGeo.getValueForKey("Country_ID", 0);

        if (countryId != null && Integer.parseInt(countryId) <= 0) {
            return "Error: Invalid country name provided";
        }

        String content = "";
        content += Html.createHiddenField("map_json", Tokens.BASE_URL+"services/json/map?country_id="+countryId+"&type_id="+typeId, "widget_urls");
        String html = org.geo.core.serializations.html.templates.Map.getTemplate();
        html = html.replace("{{content}}", content);
        return html;
    }

}
