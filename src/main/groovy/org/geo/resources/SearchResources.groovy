package org.geo.resources;

import com.yammer.metrics.annotation.Timed;
import org.geo.core.templates.Search;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author: Harihar Shankar, 4/29/13 8:51 PM
 */

@Path("/search")
@Produces(MediaType.TEXT_HTML)
public class SearchResources {

    @GET
    @Timed
    public String getFactSheet() {
        String html = "";

        String template = Search.getTemplate();

        html += template;

        return html;
    }

}
