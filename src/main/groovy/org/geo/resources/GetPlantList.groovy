package org.geo.resources

import org.geo.core.db.Geo
import org.geo.core.db.Moderation
import org.geo.core.db.Select
import org.geo.core.utils.Tokens

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

/**
 @author: Harihar Shankar, 7/9/13 2:07 PM
 */

@Path("/plantlist")
@Produces(MediaType.TEXT_HTML)

class GetPlantList {

    @GET
    public String getPlantList (@QueryParam("country") String country,
                                @QueryParam("type") String type,
                                @QueryParam("state") String state) {

        final Select typeSelect =  new Select();
        final Geo typeGeo = typeSelect.read("Type", null, "Type LIKE '" + type + "'");
        final String typeId = typeGeo.getValueForKey("Type_ID", 0);


        Select countrySelect = new Select();
        Geo countryGeo = countrySelect.read("Country", "Country_ID", "Country LIKE '"+country+"'")
        String countryId = countryGeo.getValueForKey("Country_ID", 0);

        Moderation moderation = new Moderation()
        Geo result = moderation.getAllRevisionsForTypeAndCountry(Integer.parseInt(countryId), Integer.parseInt(typeId))

        int resultCount = result.getRowCount()
        ArrayList<ArrayList<String>> resultValues = result.getValues()

        String html = "";
        for (int i=0; i<resultCount; i++) {
            html += "<a href='"+ Tokens.BASE_URL + Tokens.FACT_SHEET_URI +"?pid="+ resultValues.get(i).get(0) +"' target='_blank'>" + resultValues.get(i).get(1) + "</a><br/>";
        }
        return html

    }

}
