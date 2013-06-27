package org.geo.resources

import org.geo.core.db.GeoPoint;
import org.geo.core.db.Moderation
import org.geo.core.db.SeedInfo;
import org.geo.core.db.Select;
import org.geo.core.db.Geo;
import org.geo.core.services.MapLocations;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

/**
 * @author: Harihar Shankar, 5/1/13 7:18 PM
 */

@Path("/services/json/map")
@Produces(MediaType.APPLICATION_JSON)
public class GetMapJson {

    @GET
    public MapLocations getMapJson(@QueryParam("country_id") final String countryId,
                             @QueryParam("type_id") final String typeId,
                             @QueryParam("description_id") final String descriptionId) {

        if (descriptionId != null && Integer.parseInt(descriptionId) > 0) {
            return getMapJsonForDescriptionId(descriptionId)
        }
        else if (countryId != null && Integer.parseInt(countryId) > 0 && typeId != null && Integer.parseInt(typeId) > 0) {
            return getMapJsonForCountryAndType(countryId, typeId)
        }

        return null
    }

    public static MapLocations getMapJsonForCountryAndType(final String countryId, final String typeId) {
        Moderation moderation = new Moderation();
        ArrayList<Integer> revisions = moderation.getAllRevisionsForTypeAndCountry(Integer.parseInt(countryId), Integer.parseInt(typeId));

        // TYPE values
        final Select type =  new Select();
        final Geo typeGeo = type.read("Type", null, "Type_ID=" + typeId);
        final String typeName = typeGeo.getValueForKey("Type", 0);

        // Country table
        final Select country =  new Select();
        final Geo countryGeo = country.read("Country", null, "Country_ID=" + countryId);
        final String countryName = countryGeo.getValueForKey("Country", 0);

        ArrayList<GeoPoint> loc = new ArrayList<GeoPoint>(revisions.size());
        for (int descriptionId : revisions) {
            // saving lat lng in hidden fields for plotting in google maps
            Select location = new Select();
            Geo locationGeo = location.read(typeName + "_Location", "", "Description_ID=" + descriptionId);
            String latitude = locationGeo.getValueForKey("Latitude_Start", 0);
            String longitude = locationGeo.getValueForKey("Longitude_Start", 0);

            Select description = new Select();
            Geo descriptionSearchResult = description.read(typeName + "_Description", "Name_omit", "Description_ID=" + descriptionId);
            String name = descriptionSearchResult.getValueForKey("Name_omit", 0);

            GeoPoint gp = new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude), name);
            loc.add(gp);
        }

        return new MapLocations(loc, countryName);
    }


    public static MapLocations getMapJsonForDescriptionId(@QueryParam("description_id") final String descriptionId) {

        //ArrayList<ArrayList<String>> loc = new ArrayList<ArrayList<String>>(2);
        ArrayList<GeoPoint> loc = new ArrayList<GeoPoint>(2);

        SeedInfo seedInfo = new SeedInfo(Integer.parseInt(descriptionId))
        Map<String,String> type = seedInfo.getType()
        String typeName = type.get("typeName")

        Map<String,String> country = seedInfo.getCountry()
        String countryName = country.get("countryName")

        Map<String,String> state = seedInfo.getState()
        String stateName = state.get("stateName")

        // fetching lat lng from the _Location table
        Select location = new Select();
        Geo locationGeo = location.read(typeName + "_Location", "", "Description_ID=" + descriptionId);
        String latitude = locationGeo.getValueForKey("Latitude_Start", 0);
        String longitude = locationGeo.getValueForKey("Longitude_Start", 0);

        // fetching overlays from the _Overlays table
        Select overlayTable = new Select();
        Geo overlaysGeo = overlayTable.read(typeName + "_Overlays", "", "Description_ID=" + descriptionId);
        ArrayList<Map<String,String>> overlays = new ArrayList<>(overlaysGeo.getRowCount());

        for (int i=0; i<overlaysGeo.getRowCount(); i++) {
            overlays.add([
                    color: overlaysGeo.getValueForKey("Color", i),
                    weight: overlaysGeo.getValueForKey("Weight", i),
                    opacity: overlaysGeo.getValueForKey("Opacity", i),
                    points: overlaysGeo.getValueForKey("Points", i),
                    numLevels: overlaysGeo.getValueForKey("Num_Levels", i),
                    zoomFactor: overlaysGeo.getValueForKey("Zoom_Factor", i),
                    overlayType: overlaysGeo.getValueForKey("Overlay_Type", i),
                    overlayName: overlaysGeo.getValueForKey("Overlay_Name", i)
            ])
        }

        // fetching the name of the plant
        Select description = new Select();
        Geo descriptionSearchResult = description.read(typeName + "_Description", "Name_omit", "Description_ID=" + descriptionId);
        String name = descriptionSearchResult.getValueForKey("Name_omit", 0);

        //ArrayList<String> rev = new ArrayList<String>(1);
        //rev.add(0, latitude);
        //rev.add(1, longitude);
        //rev.add(2, name);
        GeoPoint gp = new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude), name, overlays);
        loc.add(gp);

        return new MapLocations(loc, null);
    }
}
