package org.geo.resources

import org.geo.core.Geo
import org.geo.core.db.Moderation
import org.geo.core.db.Select
import org.geo.core.utils.Tokens

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import java.sql.Connection

/**
 @author: Harihar Shankar, 7/9/13 2:07 PM
 */

@Path("/plantlist")
@Produces(MediaType.TEXT_HTML)

class GetPlantList {

    private Connection connection

    public GetPlantList(Connection connection) {
        this.connection = connection
    }

    @GET
    public String getPlantList (@QueryParam("country") String reqCountry,
                                @QueryParam("type") String reqType,
                                @QueryParam("state") String state) {

        if (reqCountry == null || reqType == null) {
            return null
        }

        Select countryDAO = new Select()
        Geo country = countryDAO.read(this.connection, "Country", null, "Country LIKE '"+reqCountry+"'")
        if (!country) {
            return null
        }
        Integer countryId = Integer.parseInt(country.getValueForKey("Country_ID", 0))

        Select typeDAO = new Select()
        Geo type = typeDAO.read(this.connection, "Type", null, "Type LIKE '"+reqType+"'")
        if (!type) {
            return null
        }
        Integer typeId = Integer.parseInt(type.getValueForKey("Type_ID", 0))

        Moderation moderation = new Moderation(connection)
        Geo result = moderation.getAllRevisionsForTypeAndCountry(countryId, typeId)

        int resultCount = result.getRowCount()
        ArrayList<ArrayList<String>> resultValues = result.getValues()

        StringBuilder html = new StringBuilder();
        html.append("<div class='tab-content'>")
        for (int i=0; i<resultCount; i++) {
            if (i%2 == 0) {
                html.append("<div class='even-row'>")
            }
            else {
                html.append("<div class='odd-row'>")
            }
            html.append("<span class='row-number'>" + (i+1) + "</span>")
            html.append("<a href='"+ Tokens.BASE_URL + Tokens.FACT_SHEET_URI +"?pid="+ resultValues.get(i).get(0) +"' target='_blank'>" + resultValues.get(i).get(1) + "</a>");
            html.append("</div>")
        }
        html.append("</div>")
        return html.toString()
    }
}
