package org.geo.core.serializations.html

import org.geo.core.GeoSystem
import org.geo.core.Geo
import org.geo.core.db.Select;
import org.geo.core.serializations.html.templates.Form
import org.geo.core.utils.Tokens

import java.sql.Connection;

/**
 * @author: Harihar Shankar, 4/22/13 7:33 PM
 */

public class Html {

    private Connection connection
    private String descriptionId
    private String typeId
    private String countryId
    private String stateId
    private String geoName

    private String typeName;
    private String countryName;
    private String stateName;
    private String typeDatabaseName;
    private String features;


    public Html(Connection connection, final String descriptionId) {
        this.connection = connection
        this.descriptionId = descriptionId
        GeoSystem geoSystem = new GeoSystem(connection, Integer.parseInt(descriptionId))

        typeId = geoSystem.getTypeId().toString()
        Select typeDAO = new Select()
        Geo type = typeDAO.read(connection, "Type", null, "Type_ID="+typeId)

        typeName = type.getValueForKey("Type", 0);
        typeDatabaseName = type.getValueForKey("Database_Type", 0);
        features = this.getFeatures(typeId);

        countryId = geoSystem.getCountryId().toString();

        Select countryDAO = new Select()
        Geo country = countryDAO.read(connection, "Country", null, "Country_ID="+countryId);
        countryName = country.getValueForKey("Country", 0)

        stateId = geoSystem.getStateId().toString();
        Select stateDAO = new Select()
        Geo state = stateDAO.read(connection, "State", null, "State_ID="+stateId)
        stateName = state.getValueForKey("State", 0);
    }

    private String getFeatures(String typeId) {
        final Select typeFeatures =  new Select();
        final Geo typeFeaturesGeo = typeFeatures.read(connection, "Type_Features", null, "Type_ID=" + typeId);
        final String features = typeFeaturesGeo.getValueForKey("Features", 0);
        return features
    }

    private static String normalizeToken (String token) {

        token = token.replace("_itf", "");
        token = token.replaceAll("_rng1|_rng2|_rng3", "");
        token = token.replace("_nbr", "");
        token = token.replace("_yr", "");
        token = token.replace("_dt", "");

        token = token.replaceAll("_", " ").trim();
        return token;
    }

    public String generateFactSheet() {

        // TypeFeatures table
        String template = Form.getTemplate();
        StringBuilder returnValue = new StringBuilder();

        returnValue.append(createHiddenField("Description_ID", descriptionId, ""))
        returnValue.append(createHiddenField("Type_ID", typeId, ""))
        returnValue.append(createHiddenField("Country_ID", countryId, ""))
        returnValue.append(createHiddenField("State_ID", stateId, ""))

        for (String f : features.split(",")) {
            StringBuilder moduleHtml = getModuleForFeature(f);

            if (moduleHtml.length() > 0) {
                if (!moduleHtml.toString().startsWith("<table>")) {
                    StringBuilder temp = new StringBuilder()
                    temp.append("<table>")
                    temp.append(moduleHtml)
                    temp.append("</table>");
                    moduleHtml = temp
                }
                String moduleTemplate = Form.getModuleTemplate();

                String module = moduleTemplate.replace("{{module_id}}", f);
                module = module.replace("{{module_heading}}", f.replace("_", " "));
                module = module.replace("{{module_content}}", moduleHtml.toString());
                if (f.matches("Environmental_Issues|Comments|References|Unit_Description|Upgrades|Owner_Details"))
                    module = module.replace("{{module_header_class}}", "single-row-module");
                else
                    module = module.replace("{{module_header_class}}", "generic-module");
                returnValue.append(module);
            }
        }

        String html = template.replace("{{content}}", returnValue.toString());
        if (geoName != null) {
            html = html.replace("{{title}}", geoName);
        }
        return html
    }

    private StringBuilder getModuleForFeature(final String feature) {

        if (feature.contains("Unit_")) {
            return makeUnitModule(feature);
        }
        else if (feature.contains("Location")) {
            return makeLocationModule();
        }
        else if (feature.contains("Annual_Performance")) {
            return makePerformanceModule();
        }
        else if (feature.contains("Identifiers")) {
            return makeIdentifiersModule(feature);
        }
        else if (feature.matches("Environmental_Issues|Comments|References|Upgrades")) {
            return makeSingleRowModule(feature);
        }
        else if (feature.matches("Owner_Details")) {
            StringBuilder html = new StringBuilder()
            html.append(makeSingleRowModule("Owners"))
            html.append(makeGenericModule(feature))
            return html
        }
        else {
            return makeGenericModule(feature);
        }
    }


    private StringBuilder makeGenericModule(final String feature) {
        StringBuilder returnValue = new StringBuilder()

        Select select = new Select();
        try {
            final Geo geo = select.read(connection, typeName + "_" + feature, null, "Description_ID=" + descriptionId);
            ArrayList<String> keys = geo.getKeys();

            for (final String k : keys) {
                String gen = createEditableRow(k, geo.getValueForKey(k, 0), typeName+"_"+feature, false)
                if (gen != null) {
                    returnValue.append("<tr>")
                    returnValue.append(gen);
                    returnValue.append("</tr>");
                }
            }
        } catch (Exception e) {
            System.out.println(feature + ": " +e);
        }

        return returnValue;
    }


    private StringBuilder makeIdentifiersModule(final String feature) {

        StringBuilder returnValue = new StringBuilder();

        Select select = new Select();
        try {
            final Geo geo = select.read(connection, typeName + "_Description", null, "Description_ID=" + descriptionId);
            ArrayList<String> keys = geo.getKeys();

            if (geo.getValueForKey("Name_omit", 0) != null) {
                geoName = geo.getValueForKey("Name_omit", 0);
                returnValue.append("<tr>");
                returnValue.append(createEditableRow("Name_omit", geoName, typeName+"_Description", true));
                returnValue.append("</tr>");
            }
            for (final String k : keys) {
                if (k.contains("_itf")) {
                    returnValue.append("<tr>")
                    returnValue.append(createEditableRow(k, geo.getValueForKey(k, 0), typeName+"_Description", true));
                    returnValue.append("</tr>")
                }
            }
        } catch (Exception e) {
            println(feature + ": " +e);
        }
        return returnValue;
    }

    private StringBuilder makeUnitModule(final String feature) {
        StringBuilder returnValue = new StringBuilder();
        Select s = new Select();
        try {
            final Geo geo = s.read(connection, typeName + "_" + feature, null, "Description_ID=" + descriptionId);
            ArrayList<String> keys = geo.getKeys();
            ArrayList<ArrayList<String>> values = geo.getValues();

            ArrayList<ArrayList<String>> unitValues = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> controlValues = new ArrayList<ArrayList<String>>();
            ArrayList<String> unitKeys = new ArrayList<String>();
            ArrayList<String> controlKeys = new ArrayList<String>();

            for (final String k : keys) {
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
            returnValue.append("<table>")
            returnValue.append(createSpreadSheetRow(unitKeys, unitValues, typeName+"_"+feature, "unit"));
            returnValue.append("</table>")
            returnValue.append("<table>")
            returnValue.append(createSpreadSheetRow(controlKeys, controlValues, typeName+"_"+feature, "unit"));
            returnValue.append("</table>")
        }
        catch (Exception e) {
            println(e);
        }
        return returnValue;
    }

    private StringBuilder makeLocationModule() {
        StringBuilder returnValue = new StringBuilder()

        returnValue.append("<div id='overlay-details'></div>")
        returnValue.append("<div id='map-resize'>");
        returnValue.append("<div id='map-container' style='height: 480px;'></div>");
        returnValue.append("</div>");
        returnValue.append(createHiddenField("map_json", Tokens.BASE_URL+"services/json/map?description_id="+descriptionId, "widget_urls"));
        return returnValue;
    }

    private StringBuilder makePerformanceModule() {
        StringBuilder returnValue = new StringBuilder();

        Select s = new Select();
        final Geo geo = s.read(connection, typeName + "_Performance", null, "Description_ID=" + descriptionId);

        ArrayList<String> keys = geo.getKeys();
        ArrayList<ArrayList<String>> values = geo.getValues();

        returnValue.append(createPerformanceTable(keys, values));

        return returnValue;
    }

    private StringBuilder makeSingleRowModule(final String feature) {
        StringBuilder returnValue = new StringBuilder();
        Select s = new Select();
        try {
            final Geo geo = s.read(connection, typeName + "_" + feature, null, "Description_ID=" + descriptionId);
            ArrayList<String> keys = geo.getKeys();
            ArrayList<ArrayList<String>> values = geo.getValues();

            returnValue.append(createSpreadSheetRow(keys, values, typeName+"_"+feature, ""));
        }
        catch (Exception e) {
            println(e.getMessage())
        }
        return returnValue;
    }


    private static Boolean shouldDisplay(final String key) {
        return !key.contains("_ID") && !key.contains("Year_yr");
    }


    private String createEnumField(String key, String value, String tableName) {
        StringBuilder row = new StringBuilder();

        String dbKey = key.split("_###_")[0]
        Select s = new Select();
        LinkedHashMap<String, String> en = s.readColumnName(connection, tableName, dbKey);

        String eV = en.get(dbKey).replace("enum(", "");
        eV = eV.substring(0, eV.length()-1);

        StringBuilder sb = new StringBuilder()
        Boolean replaceComma = false
        for (char c : eV) {

            if (c.charValue() == '(') {
                replaceComma = true
            }
            if (c.charValue() == ')') {
                replaceComma = false
            }
            if (replaceComma && c.charValue() == ',') {
                c = "@"
            }
            sb.append(c)
        }

        row.append("<select id=\""+key+"\" name=\""+key+"\">");
        for (String option : sb.toString().split(",")) {
            option = option.replaceAll("'", "");
            option = option.replaceAll("@", ",")
            if (option.equals(value))
                row.append("<option value=\""+option+"\" selected=\"selected\">"+option+"</option>");
            else
                row.append("<option value=\""+option+"\">"+option+"</option>");
        }
        row.append("</select>");
        return row.toString();
    }

    private String createSetField(String key, String value, String tableName) {
        StringBuilder row = new StringBuilder();

        Select s = new Select();
        LinkedHashMap<String, String> en = s.readColumnName(connection, tableName, key);

        String eV = en.get(key).replace("set(", "");
        eV = eV.substring(0, eV.length()-1);

        for (String option : eV.split(",")) {
            option = option.replaceAll("'", "");
            if (option.equals(value)) {
                row.append("<input type=\"checkbox\" name=\""+key+"_###_"+option+"\" id=\""+key+"_###_"+option+"\" value=\""+option+"\" selected=\"selected\"/>"+option);
            }
            else {
                row.append("<input type=\"checkbox\" name=\""+key+"_###_"+option+"\" id=\""+key+"_###_"+option+"\" value=\""+option+"\" />"+option);
            }
        }
        return row.toString();
    }

    private static String createNumberInputField(String key, String value) {
        return "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"11\" />";
    }


    public static String createHiddenField(String key, String value, String className) {
        return "<input type=\"hidden\" class=\""+className+"\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" />";
    }


    private static String createInputField(String key, String value, String moduleType) {
        if (moduleType.equals("unit"))
            return "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"10\" />";
        else if (moduleType.equals("generic"))
            return "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"30\" />";
        else if (moduleType.equals("performance"))
            return  "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"6\" />";
        else
            return  "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"100\" />";
    }

    public String createEditableRow(String key, String value, String tableName, Boolean itf) {

        if (value == null)
            value = "";

        if (!shouldDisplay(key))
            return null;

        if (!itf && key.contains("_itf"))
            return null;

        StringBuilder row = new StringBuilder()
        row.append("<td class=\"input_right\">");
        String k = key;
        if (k.contains("_rng2") || k.contains("_rng3")) {
            k = k.split("_rng2|_rng3")[1];
            k = "-- <i>" + k + "</i>";
        }
        else if (k.contains("_rng1")) {
            String[] t = key.split("_rng1");
            k = t[0];
            if (t.length > 1)
                k += " -- <i>" + t[1] + "</i>";
        }

        if (k.contains("_enumfield")) {
            row.append(createEnumField(key, value, tableName));
            row.append("</td>");
            if (key.contains("_rng2") || key.contains("_rng3"))
                row.insert(0, "<td class=\"label_rng\">"+normalizeToken(k.replace("_enumfield", ""))+"</td>");
            else
                row.insert(0, "<td class=\"label_left\">"+normalizeToken(k.replace("_enumfield", ""))+"</td>");
        }
        else if (k.contains("_setfield")) {
            row.append(createSetField(key, value, tableName));
            row.append("</td>");

            if (key.contains("_rng2") || key.contains("_rng3"))
                row.insert(0, "<td class=\"label_rng\">"+normalizeToken(k.replace("_setfield", ""))+"</td>");
            else
                row.insert(0, "<td class=\"label_left\">"+normalizeToken(k.replace("_setfield", ""))+"</td>");
        }
        else if (k.contains("_nbr")) {
            row.append(createNumberInputField(key, value));
            row.append("</td>");
            if (key.contains("_rng2") || key.contains("_rng3"))
                row.insert(0, "<td class=\"label_rng\">"+normalizeToken(k.replace("_nbr", ""))+"</td>");
            else
                row.insert(0, "<td class=\"label_left\">"+normalizeToken(k.replace("_nbr", ""))+"</td>");
        }
        else {
            row.append(createInputField(key, value, "generic"));
            row.append("</td>");
            if (key.contains("_rng2") || key.contains("_rng3"))
                row.insert(0, "<td class=\"label_rng\">"+normalizeToken(k.replace("_nbr", ""))+"</td>");
            else
                row.insert(0, "<td class=\"label_left\">"+normalizeToken(k.replace("_nbr", ""))+"</td>");
        }
        return row.toString();
    }


    public String createSpreadSheetRow(ArrayList<String> keys, ArrayList<ArrayList<String>> values, String tableName, String moduleType) {
        StringBuilder row = new StringBuilder();
        row.append("<tr>");

        // check box heading
        row.append("<th></th>")

        if (tableName.contains("Owners")) {
            row.append("<th>Owner #</th>")
        }
        else {
            row.append("<th>#</th>");
        }

        for (String k : keys) {
            if (shouldDisplay(k)) {
                row.append("<th>" + normalizeToken(k) + "</th>");
            }
        }
        row.append("</tr>");

        ArrayList<String> emptyValues = []
        if (values.size() == 0) {
            for (String k: keys) {
                emptyValues.add(null)
            }
            values.add(emptyValues)
        }
        int lineCount = 0;
        for (ArrayList<String> val : values) {
            int count = 0;
            row.append("<tr class='single-rows'>");
            row.append("<td><input type=\"checkbox\" id=\""+tableName+"_###_"+lineCount+"\" name=\""+tableName+"_###_"+lineCount+"\"></td>")
            row.append("<td>" + Integer.toString(++lineCount) + "</td>");
            for (String v : val) {
                if (!shouldDisplay(keys.get(count))) {
                    count++;
                    continue;
                }
                if (v == null)
                    v = "";

                if (keys.get(count).contains("_enumfield")) {
                    row.append("<td>" + createEnumField(keys.get(count)+"_###_"+lineCount, v, tableName) + "</td>");
                }
                else if (keys.get(count).contains("_setfield")) {
                    row.append("<td>" + createSetField(keys.get(count)+"_###_"+lineCount, v, tableName) + "</td>");
                }
                else if (keys.get(count).contains("_nbr")) {
                    row.append("<td>" + createNumberInputField(keys.get(count)+"_###_"+lineCount, v) + "</td>");
                }
                else {
                    if (keys.get(count).toLowerCase().find("year") || keys.get(count).toLowerCase().find("cost")) {
                        row.append("<td>" + createInputField(keys.get(count)+"_###_"+lineCount, v, "unit") + "</td>");
                    }
                    else {
                        row.append("<td>" + createInputField(keys.get(count)+"_###_"+lineCount, v, moduleType) + "</td>");
                    }
                }
                count++;
            }
            row.append("</tr>\n");
        }

        row.append("</tr>");
        row.append("<input type=\"hidden\" name=\"numberOf"+tableName+"\" value=\""+lineCount+"\" />");
        return row.toString();
    }


    public static String createPerformanceTable(ArrayList<String> keys, ArrayList<ArrayList<String>> values) {
        StringBuilder table = new StringBuilder();


        table.append("<tr>");

        int rowCount = 0;
        table.append("<td>");
        table.append("<table class='performance-label'>");
        table.append("<tr class='perf-row even-row'></tr>");
        rowCount++;

        for (String k : keys) {
            if (shouldDisplay(k)) {
                if (rowCount%2 == 0)
                    table.append("<tr class='perf-row even-row'>");
                else
                    table.append("<tr class='perf-row odd-row'>");
                table.append("<th>" + normalizeToken(k) + "</th>");
                table.append("</tr>");
                rowCount++;
            }
        }
        table.append("</table>");
        table.append("</td>");

        rowCount = 0;

        table.append("<td>");
        table.append("<table class='performance-values'>");
        table.append("<tr class='perf-row even-row'>");
        rowCount++;
        for (int year=Tokens.ANNUAL_PERFORMANCE_DECADE_START; year<=Tokens.ANNUAL_PERFORMANCE_DECADE_END; year++) {
            table.append("<th>" + Integer.toString(year) + "</th>");
        }
        table.append("</tr>");

        int keyCount=0;
        for (String k : keys) {
            int yearIndex = 1;
            if (k.equals("Year_yr"))
                yearIndex = keyCount;

            if (shouldDisplay(k)) {
                if (rowCount%2 == 0)
                    table.append("<tr class='perf-row even-row'>");
                else
                    table.append("<tr class='perf-row odd-row'>");
                for (int year=Tokens.ANNUAL_PERFORMANCE_DECADE_START; year<=Tokens.ANNUAL_PERFORMANCE_DECADE_END; year++) {
                    int v = 0;
                    for (v=0; v<values.size(); v++) {
                        if (values.get(v).get(yearIndex).contains(year.toString()))
                            break;
                    }
                    String value = ""
                    if (v < values.size())
                        value = values.get(v).get(keyCount)
                    if (value == null)
                        value = ""
                    table.append("<td>" + createInputField(k + "_###_" + Integer.toString(year), value, "performance") + "</td>");
                }
                table.append("</tr>\n");
                rowCount++;
            }
            keyCount++;
        }

        table.append("</table>");
        table.append("</td>");
        table.append("</tr>");

        return table;
    }
}
