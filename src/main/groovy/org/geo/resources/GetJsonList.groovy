package org.geo.resources

import org.geo.core.db.Geo
import org.geo.core.db.Moderation
import org.geo.core.db.Select

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 @author: Harihar Shankar, 7/2/13 1:00 PM
 */

@Path("/services/json/list")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

class GetJsonList {

    private static List<String> KEY_TYPE = ["Type", "Country", "State", "plant_list"]

    @POST
    public static Geo getJsonList (final Map requestData) {

        Set<String> requestKeys = requestData.keySet()
        Geo result = new Geo();

        String returnType = requestData.get("return_type")

        if (returnType.equals("plant_list")) {
            if (requestKeys.contains("Type") && requestKeys.contains("Country")) {
                ArrayList<String> typeValues = [];
                ArrayList<String> countryValues = [];
                try {
                    typeValues = requestData.get("Type")
                    countryValues = requestData.get("Country")
                }
                catch (Exception e) {
                    return result
                }

                for (String typeName : typeValues) {
                    // TYPE values
                    final Select type =  new Select();
                    final Geo typeGeo = type.read("Type", null, "Type LIKE '" + typeName + "'");
                    final String typeId = typeGeo.getValueForKey("Type_ID", 0);

                    for (String countryName : countryValues) {

                        Select countrySelect = new Select();
                        Geo countryGeo = countrySelect.read("Country", "Country_ID", "Country LIKE '"+countryName+"'")
                        String countryId = countryGeo.getValueForKey("Country_ID", 0);

                        Moderation moderation = new Moderation()
                        result = moderation.getAllRevisionsForTypeAndCountry(Integer.parseInt(countryId), Integer.parseInt(typeId))
                    }
                }
            }
        }
        else if (returnType.equals("Database_Type")) {
            result = new Moderation().getDatabaseType()
        }
        else if (returnType.equals("Type")) {
            ArrayList<String> values = []
            try {
                values = requestData.get("Database_Type")
            }
            catch (Exception e) {
                return result
            }
            for (String v : values) {
                result = new Moderation().getTypeForDb(v)
            }
        }
        else if (returnType.equals("Country")) {
            ArrayList<String> values = []
            try {
                values = requestData.get("Type")
            }
            catch (Exception e) {
                return result
            }
            for (String v : values) {
                result = new Moderation().getCountryForType(v)
            }
        }
        else if (returnType.equals("State")) {
            ArrayList<String> values = []
            try {
                values = requestData.get("Country")
            }
            catch (Exception e) {
                return result
            }
            for (String v : values) {
                result = new Moderation().getStateForCountry(v)
            }
        }

        return result
    }

}
