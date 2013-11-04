package org.geo.resources

import org.geo.core.Geo
import org.geo.core.db.Select

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import java.sql.Connection

/**
 @author: Harihar Shankar, 8/7/13 10:45 AM
 */


@Path("/services/json/summary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

class GetJsonSummary {

    private final Connection connection;

    public GetJsonSummary(Connection connection) {
        this.connection = connection
    }

    @GET
    public Geo getJsonSummary(@QueryParam("country_id") final String countryId,
                                @QueryParam("type_id") String typeId) {

        Select countryDAO = new Select()
        Geo country = countryDAO.read(this.connection, "Country", null, "Country_ID="+countryId)
        if (!country) {
            return null
        }
        String cId = country.getValueForKey("Country_ID", 0)

        Select typeDAO = new Select()
        Geo type = typeDAO.read(this.connection, "Type", null, "Type_ID="+typeId)
        if (!type) {
            return null
        }
        String tId = type.getValueForKey("Type_ID", 0)
        String whereClause = "Country_ID=" + cId
        String selectClause = null
        if (tId > 0) {
            whereClause += " AND Type_ID=" + tId
        }
        else {
            selectClause = "Type_ID,Country_ID,Number_of_Plants,Cumulative_Capacity"
        }

        Select selectDAO = new Select()
        Geo summary = selectDAO.read(this.connection, "metadata", selectClause, whereClause)
        return summary
    }

}
