package org.geo.resources

import org.geo.core.db.Geo
import org.geo.core.db.Insert
import org.geo.core.db.Moderation
import org.geo.core.db.SeedInfo
import org.geo.core.db.Select
import org.geo.core.templates.SubmitResponse
import org.geo.core.utils.Tokens

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder;

/**
 * @author: Harihar Shankar, 6/11/13 1:49 PM
 */

@Path("/factsheet/submit")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//@Produces(MediaType.TEXT_HTML)

public class SubmitFactsheet {

    private String typeId;
    private String countryId;
    private String stateId;
    private String descriptionId;
    private String oldDescriptionId;

    private String typeName;
    private String countryName;
    private String stateName;
    private String typeDatabaseName;
    private String geoName;

    @POST
    public String submitFactSheet(MultivaluedMap<String, String> formData) {
        String result = "";

        oldDescriptionId = formData.get("Description_ID")[0]

        Integer parentPlantId = 0
        if (Integer.parseInt(oldDescriptionId) > 0) {
            Moderation moderation = new Moderation();
            parentPlantId = moderation.getParentPlantId(Integer.parseInt(oldDescriptionId))
        }

        Insert historyInsert = new Insert()
        formData.add("Moderated", "0")
        formData.add("Moderator_ID", "0")
        formData.add("Accepted", "0")
        formData.add("User_ID", "11")
        formData.add("Parent_Plant_ID", parentPlantId.toString())

        descriptionId = historyInsert.insert("History", formData, "history").toString()
        formData["Description_ID"][0] = descriptionId

        println(descriptionId)

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

            if (f.matches("Unit_Description|Environmental_Issues|Comments|References|Upgrade")) {
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
            else if (f.contains("History")) {
                continue
            }
            else if (f.contains("Owner_Details")) {
                Insert insertOwners = new Insert()
                insertOwners.insert(typeName+"_Owners", formData, "rowColumns")

                Insert insertOwnerDetails = new Insert()
                insertOwnerDetails.insert(tableName, formData, "generic")
            }
            else if (!f.contains("Associated_Infrastructure")) {
                Insert insert = new Insert()
                insert.insert(tableName, formData, "generic")
            }
        }

        String response = SubmitResponse.getTemplate()
        response = response.replace("{{newSubmission}}", "<a href=\""+Tokens.BASE_URL+"geoid?pid="+descriptionId.toString()+"\">New Submission</a>")
        response = response.replace("{{originalSubmission}}", "<a href=\""+Tokens.BASE_URL+"geoid?pid="+oldDescriptionId.toString()+"\">Original Fact Sheet</a>")
        response = response.replace("{{overviewPage}}", "<a href=\""+Tokens.BASE_URL+"search\">Overview Page</a>")
        return response

    }
}
