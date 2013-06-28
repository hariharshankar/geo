package org.geo.resources;

import com.yammer.metrics.annotation.Timed;
import org.geo.core.db.SeedInfo;
import org.geo.core.db.Select;
import org.geo.core.db.Geo;
import org.geo.core.templates.Form;
import org.geo.core.utils.HTMLMarkup
import org.geo.core.utils.Tokens;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

/**
 * @author: Harihar Shankar, 3/28/13 4:07 PM
 */

@Path("/geoid")
@Produces(MediaType.TEXT_HTML)
public class GetFactSheet {

    private String typeId;
    private String countryId;
    private String stateId;
    private String descriptionId;

    private String typeName;
    private String countryName;
    private String stateName;
    private String typeDatabaseName;
    private String geoName;


    @GET
    @Timed
    public String getFactSheet(@QueryParam("pid") String descId) {

        if (Integer.parseInt(descId) <= 0) {
            return null;
        }

        descriptionId = descId;

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

        String template = Form.getTemplate();
        String returnValue = "";

        returnValue += HTMLMarkup.createHiddenField("Description_ID", descriptionId, "")

        for (String f : features.split(",")) {
            println(f)
            String moduleHtml = getModuleForFeature(f);
            if (moduleHtml != null) {
                if (!moduleHtml.equals("")) {
                    moduleHtml = "<table>" + moduleHtml + "</table>";
                    String moduleTemplate = Form.getModuleTemplate();

                    String module = moduleTemplate.replace("{{module_id}}", f);
                    module = module.replace("{{module_heading}}", f);
                    module = module.replace("{{module_content}}", moduleHtml);
                    if (f.matches("Environmental_Issues|Comments|References|Unit_Description"))
                        module = module.replace("{{module_header_class}}", "single-row-module");
                    else
                        module = module.replace("{{module_header_class}}", "generic-module");
                    returnValue += module;
                }
            }
        }


        String html = template.replace("{{content}}", returnValue);
        if (geoName != null) {
            html = html.replace("{{title}}", geoName);
        }

        return html;
    }


    private String getModuleForFeature(final String feature) {

        if (feature.contains("Unit_")) {
            return makeUnitModule(feature);
        }
        else if (feature.contains("Location")) {
            return makeLocationModule(feature);
        }
        else if (feature.contains("Annual_Performance")) {
            return makePerformanceModule(feature);
        }
        else if (feature.contains("Identifiers")) {
            return makeIdentifiersModule(feature);
        }
        else if (feature.matches("Environmental_Issues|Comments|References")) {
            return makeSingleRowModule(feature);
        }
        else {
            return makeGenericModule(feature);
        }
    }


    private String makeGenericModule(final String feature) {
        String returnValue = "";

        Select select = new Select();
        try {
            final Geo geo = select.read(typeName + "_" + feature, null, "Description_ID=" + descriptionId);
            ArrayList<String> keys = geo.getKeys();

            for (String k : keys) {
                String ret = HTMLMarkup.createEditableRow(k, geo.getValueForKey(k, 0), typeName+"_"+feature, false);
                if (ret != null && !ret.equals("")) {
                    returnValue += "<tr>" + ret + "</tr>\n";
                }
            }
        } catch (Exception e) {
            System.out.println(feature + ": " +e);
        }

        return returnValue;
    }


    private String makeIdentifiersModule(final String feature) {

        String returnValue = "";

        Select select = new Select();
        try {
            final Geo geo = select.read(typeName + "_Description", null, "Description_ID=" + descriptionId);
            ArrayList<String> keys = geo.getKeys();

            if (geo.getValueForKey("Name_omit", 0) != null) {
                geoName = geo.getValueForKey("Name_omit", 0);
                String ret = HTMLMarkup.createEditableRow("Name_omit", geoName, typeName+"_Description", true);
                if (ret != null && !ret.equals("")) {
                    returnValue += "<tr>" + ret + "</tr>";
                }
            }
            for (String k : keys) {
                if (k.contains("_itf")) {
                    String ret = HTMLMarkup.createEditableRow(k, geo.getValueForKey(k, 0), typeName+"_Description", true);
                    if (ret != null && !ret.equals("")) {
                        returnValue += "<tr>" + ret + "</tr>";
                    }
                }
            }
        } catch (Exception e) {
            println(feature + ": " +e);
        }

        return returnValue;
    }

    private String makeUnitModule(final String feature) {
        String returnValue = "";
        Select s = new Select();
        try {
            final Geo geo = s.read(typeName + "_" + feature, null, "Description_ID=" + descriptionId);
            ArrayList<String> keys = geo.getKeys();
            ArrayList<ArrayList<String>> values = geo.getValues();

            ArrayList<ArrayList<String>> unitValues = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> controlValues = new ArrayList<ArrayList<String>>();
            ArrayList<String> unitKeys = new ArrayList<String>();
            ArrayList<String> controlKeys = new ArrayList<String>();

            for (String k : keys) {
                if (!k.contains("Control_") && !k.contains("Monitor_")) {
                    unitKeys.add(k);
                }
                else {
                    controlKeys.add(k);
                }
            }

            int unitCount = 0;
            for (ArrayList<String> v : values) {
                unitValues.add(unitCount, new ArrayList<String>(v.subList(0, unitKeys.size())));
                unitCount++;
            }
            int controlCount = 0;
            for (ArrayList<String> v : values) {
                controlValues.add(controlCount, new ArrayList<String>(v.subList(unitKeys.size(), keys.size())));
                controlCount++;
            }
            returnValue += HTMLMarkup.createSpreadSheetRow(unitKeys, unitValues, typeName+"_"+feature, "unit");
            returnValue += HTMLMarkup.createSpreadSheetRow(controlKeys, controlValues, typeName+"_"+feature, "unit");
        }
        catch (Exception e) {
            println(e);
        }
        return returnValue;
    }

    private String makeLocationModule(final String feature) {
        String returnValue = ""

        returnValue += "<div id='overlay-details'></div>"
        returnValue += "<div id='map-container' style='height: 480px;'></div>";
        returnValue += HTMLMarkup.createHiddenField("map_json", Tokens.BASE_URL+"services/json/map?description_id="+descriptionId, "widget_urls");
        return returnValue;
    }

    private String makePerformanceModule(final String feature) {
        String returnValue = "";

        Select s = new Select();
        final Geo geo = s.read(typeName + "_Performance", null, "Description_ID=" + descriptionId);

        ArrayList<String> keys = geo.getKeys();
        ArrayList<ArrayList<String>> values = geo.getValues();

        returnValue += HTMLMarkup.createPerformanceTable(keys, values);

        return returnValue;
    }

    private String makeSingleRowModule(final String feature) {
        String returnValue = "";
        Select s = new Select();
        try {
            final Geo geo = s.read(typeName + "_" + feature, null, "Description_ID=" + descriptionId);
            ArrayList<String> keys = geo.getKeys();
            ArrayList<ArrayList<String>> values = geo.getValues();

            returnValue = HTMLMarkup.createSpreadSheetRow(keys, values, typeName+"_"+feature, "");
        }
        catch (Exception e) {
            println(e);
        }
        return returnValue;
    }

}