package org.geo.resources

import org.geo.core.db.Geo
import org.geo.core.db.Insert
import org.geo.core.db.SeedInfo
import org.geo.core.db.Select;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author: Harihar Shankar, 6/11/13 1:49 PM
 */

@Path("/factsheet/submit")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
public class SubmitFactsheet {

    private String typeId;
    private String countryId;
    private String stateId;
    private String descriptionId;

    private String typeName;
    private String countryName;
    private String stateName;
    private String typeDatabaseName;
    private String geoName;

    @POST
    public String submitFactSheet(MultivaluedMap<String, String> formData) {
        String result = "";

        descriptionId = formData.get("Description_ID")[0]

        SeedInfo seedInfo = new SeedInfo(Integer.parseInt(descriptionId));

        if (seedInfo == null) {
            return "Error: Could not retrieve plant info.";
        }

        typeId = seedInfo.getType().get("typeId");
        typeName = seedInfo.getType().get("typeName");
        typeDatabaseName = seedInfo.getType().get("typeDatabaseName");

        countryId = seedInfo.getCountry().get("countryId");
        countryName = seedInfo.getCountry().get("countryName");

        stateId = seedInfo.getState().get("stateId");
        stateName = seedInfo.getState().get("stateName");


        // TypeFeatures table
        final Select typeFeatures =  new Select();
        final Geo typeFeaturesGeo = typeFeatures.read("Type_Features", null, "Type_ID=" + typeId);
        final String features = typeFeaturesGeo.getValueForKey("Features", 0);

        for (String f : features.split(",")) {

            final String tableName = typeName + "_" + f

            if (f.matches("Unit_|Environmental_Issues|Comments|References")) {
                Insert insert = new Insert()
                insert.insert(tableName, formData, "rowColumns")
            }
            else if (f.contains("Annual_Performance")) {
                Insert insert = new Insert()
                insert.insert(tableName, formData, "performance")
            }
            else if (f.contains("Identifiers")) {
                // taken care of by "Description" feature
                continue;
            }
            else {
                Insert insert = new Insert()
                insert.insert(tableName, formData, "generic")
            }
        }

        return result;
    }
}
