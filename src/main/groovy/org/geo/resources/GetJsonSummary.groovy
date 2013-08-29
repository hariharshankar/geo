package org.geo.resources

import org.geo.core.db.Geo
import org.geo.core.db.Moderation
import org.geo.core.db.Select

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

/**
 @author: Harihar Shankar, 8/7/13 10:45 AM
 */


@Path("/services/json/summary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

class GetJsonSummary {

    @GET
    public Geo getJsonSummary(@QueryParam("country_id") final String countryId,
                                @QueryParam("type_id") String typeId) {

        Geo summary = new Geo()

        Select select = new Select()

        if (Integer.parseInt(countryId) <= 0) {
            return null
        }
        if (typeId == null) {
            typeId = 0
        }
        String whereClause = "Country_ID=" + countryId
        String selectClause = null
        if (Integer.parseInt(typeId) > 0) {
            whereClause += " AND Type_ID=" + typeId
        }
        else {
            selectClause = "Type_ID,Country_ID,Number_of_Plants,Cumulative_Capacity"
        }

        summary = select.read("metadata", selectClause, whereClause)
        select.close()
        return summary
    }

}
