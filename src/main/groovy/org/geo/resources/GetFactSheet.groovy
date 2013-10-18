package org.geo.resources;

import com.yammer.metrics.annotation.Timed
import org.geo.core.GeoSystem;
import org.geo.core.serializations.html.Html
import org.geo.core.utils.Tokens;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType

/**
 * @author: Harihar Shankar, 3/28/13 4:07 PM
 */

@Path(Tokens.FACT_SHEET_URI)
@Produces(MediaType.TEXT_HTML)
public class GetFactSheet {


    @GET
    @Timed
    public static String getFactSheet(@QueryParam("pid") String descId) {

        if (Integer.parseInt(descId) <= 0) {
            return null;
        }
        GeoSystem geoSystem = new GeoSystem(Integer.parseInt(descId))

        if (!geoSystem.isValidId()) {
            return "Error: Could not retrieve plant info.";
        }
        Html html = new Html(descId)
        return html.generateFactSheet();
    }
}