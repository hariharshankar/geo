package org.geo.resources;

import org.geo.core.db.Moderation;
import org.geo.core.db.Select;
import org.geo.core.db.Geo;
import org.geo.core.services.LineChart;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author: Harihar Shankar, 5/1/13 8:00 PM
 */

@Path("/services/json/linechart")
@Produces(MediaType.APPLICATION_JSON)
public class GetLineChartJson {

    @GET
    public LineChart getLineChartJson(@QueryParam("country_id") final String countryId,
                                @QueryParam("type_id") final String typeId,
                                @QueryParam("module") final String module,
                                @QueryParam("fields") final String fields,
                                @QueryParam("chart") final String chart) {

        LineChart lineChart;
        if (chart.equals("cumulative")) {
            lineChart = getJsonForCumulativeChart(countryId, typeId, fields, module);
        }
        else {
            return null;
        }

        return lineChart;
    }

    private LineChart getJsonForCumulativeChart(final String countryId, final String typeId, final String fields, final String module) {

        // TYPE values
        final Select type =  new Select();
        final Geo typeGeo = type.read("Type", null, "Type_ID=" + typeId);
        final String typeName = typeGeo.getValueForKey("Type", 0);

        // Country table
        final Select country =  new Select();
        final Geo countryGeo = country.read("Country", null, "Country_ID=" + countryId);
        final String countryName = countryGeo.getValueForKey("Country", 0);

        Moderation moderation = new Moderation();
        ArrayList<Integer> revisions = moderation.getAllRevisionsForTypeAndCountry(Integer.parseInt(countryId), Integer.parseInt(typeId));

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

        HashMap<String, HashMap<String,Double>> cumulative = new HashMap<String, HashMap<String, Double>>();

        Integer startYear = 0;
        Integer endYear = 0;

        for (String field : fields.split(",")) {
            HashMap<String, Double> cumulativeValue = new HashMap<String, Double>();
            for (int descriptionId : revisions) {
                Select gwh = new Select();
                Geo gwhGeo = gwh.read(typeName + mod, fields + "," + xAxis, "Description_ID=" + descriptionId);

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
                cumulative.put(field, cumulativeValue);
            }
        }
        for (String k : cumulative.keySet()) {
            HashMap<String, Double> line = cumulative.get(k);
            for (int y=startYear; y<=endYear; y++) {
                if (line.get(String.format("%d", y)) == null) {
                    line.put(String.format("%d", y), 0.0);
                }
            }
            cumulative.put(k, line);
        }
        return new LineChart(cumulative);
    }
}
