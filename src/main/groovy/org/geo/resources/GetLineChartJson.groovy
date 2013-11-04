package org.geo.resources;

import org.geo.core.db.Moderation;
import org.geo.core.Geo
import org.geo.core.db.Select;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType
import java.sql.Connection;

/**
 * @author: Harihar Shankar, 5/1/13 8:00 PM
 */

@Path("/services/json/linechart")
@Produces(MediaType.APPLICATION_JSON)
public class GetLineChartJson {

    private Connection connection

    public GetLineChartJson(Connection connection) {
        this.connection = connection
    }

    @GET
    public static Geo getLineChartJson(@QueryParam("country_id") final String countryId,
                                @QueryParam("type_id") final String typeId,
                                @QueryParam("module") final String module,
                                @QueryParam("fields") final String fields,
                                @QueryParam("chart") final String chart) {

        Geo lineChart;
        if (chart.equals("cumulative")) {
            lineChart = getJsonForCumulativeChart(countryId, typeId, fields, module);
        }
        else {
            return null;
        }

        return lineChart;
    }

    private Geo getJsonForCumulativeChart(final String countryId, final String typeId, final String fields, final String module) {

        // TYPE values
        final Select type =  new Select();
        final Geo typeGeo = type.read(connection, "Type", null, "Type_ID=" + typeId);
        final String typeName = typeGeo.getValueForKey("Type", 0);

        // Country table
        final Select country =  new Select();
        final Geo countryGeo = country.read(connection, "Country", null, "Country_ID=" + countryId);
        final String countryName = countryGeo.getValueForKey("Country", 0);

        Moderation moderation = new Moderation(connection);
        Geo revisions = moderation.getAllRevisionsForTypeAndCountry(Integer.parseInt(countryId), Integer.parseInt(typeId));

        String mod;
        String xAxis = "";
        if (module.equals("performance")) {
            mod = "_Performance";
            xAxis = "Year_yr";
        }
        else if (module.equals("unit")) {
            mod = "_Unit_Description";
            xAxis = "Date_Commissioned_dt";
        }
        else
            return null;

        Geo cumulative = new Geo();

        Integer startYear = 0;
        Integer endYear = 0;

        ArrayList<String> keys = new ArrayList<String>();
        HashMap<String, ArrayList<String>> fieldValues = new HashMap<String, ArrayList<String>>()

        keys.add(xAxis)

        for (String field : fields.split(",")) {
            HashMap<String, Double> cumulativeValue = new HashMap<String, Double>();
            ArrayList<String> values = new ArrayList<String>();

            for (ArrayList<String> res : revisions.getValues()) {
                int descriptionId = Integer.parseInt(res.get(0))
                Select gwh = new Select();
                Geo gwhGeo = gwh.read(connection, typeName + mod, fields + "," + xAxis, "Description_ID=" + descriptionId);

                Integer gwhYears = gwhGeo.getRowCount();

                for (int y=0; y<gwhYears; y++) {
                    String year = gwhGeo.getValueForKey(xAxis, y);
                    try {
                        year = year.split("-")[0];
                    } catch (Exception e) {
                    }

                    if (year == null || year.equals("")) {
                        continue;
                    }

                    if (startYear == 0 & y == 0) {
                        startYear = Integer.parseInt(year);
                    }
                    else if (y == 0 & startYear > Integer.parseInt(year)) {
                        startYear = Integer.parseInt(year);
                    }

                    if (endYear == 0 & y == gwhYears-1) {
                        endYear = Integer.parseInt(year);
                    }
                    else if (y == gwhYears-1 & endYear < Integer.parseInt(year)) {
                        endYear = Integer.parseInt(year);
                    }

                    if (cumulativeValue.get(year) == null ) {
                        Double gwhValue = 0.0;
                        try {
                            gwhValue = Double.parseDouble(gwhGeo.getValueForKey(field, y));
                            cumulativeValue.put(year, gwhValue);
                        } catch (Exception e) {}
                    }
                    else {
                        Double gwhValue = cumulativeValue.get(year);

                        try {
                            gwhValue += Double.parseDouble(gwhGeo.getValueForKey(field, y));
                        } catch (Exception e) {}
                        cumulativeValue.put(year, gwhValue);
                    }
                }
            }
            keys.add(field)
            for (int y=startYear; y<=endYear; y++) {
                String v = (cumulativeValue.get(String.format("%d", y)) == null) ? 0.0 : cumulativeValue.get(String.format("%d", y))
                values.add(v)
            }
            fieldValues.put(field, values)
        }

        ArrayList<String> years = new ArrayList<String>();

        ArrayList<ArrayList<String>> value = new ArrayList<ArrayList<String>>();
        for (int y=startYear; y<=endYear; y++) {
            years.add(String.format("%d", y))
        }
        fieldValues.put(keys.get(0), years)
        for (int i=0; i<fieldValues.get(keys.get(0)).size(); i++) {
            ArrayList<String> v = []
            for (String k : keys) {
                v.add(fieldValues.get(k).get(i))
            }
            value.add(v)
        }

        cumulative.setValues(value)
        cumulative.setKeys(keys)
        return cumulative;
    }
}
