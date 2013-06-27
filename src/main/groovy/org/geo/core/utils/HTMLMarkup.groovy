package org.geo.core.utils;

import org.geo.core.db.Select;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author: Harihar Shankar, 4/22/13 7:33 PM
 */

public class HTMLMarkup {

    private static String normalizeToken (String token) {

        token = token.replace("_itf", "");
        token = token.replaceAll("_rng1|_rng2|_rng3", "");
        token = token.replace("_nbr", "");
        token = token.replace("_yr", "");
        token = token.replace("_dt", "");

        token = token.replaceAll("_", " ").trim();
        return token;
    }

    private static Boolean shouldDisplay(final String key) {
        return !key.contains("_ID") && !key.contains("_omit") && !key.contains("Year_yr");
    }


    private static String createEnumField(String key, String value, String tableName) {
        String row = "";

        Select s = new Select();
        Map<String, String> en = s.readColumnName(tableName, key);

        String dbKey = key.split("_###_")[0]

        String eV = en.get(dbKey).replace("enum(", "");
        eV = eV.substring(0, eV.length()-1);

        row += "<select id=\""+key+"\">";
        for (String option : eV.split(",")) {
            option = option.replaceAll("'", "");
            if (option.equals(value))
                row += "<option value=\""+option+"\" selected=\"selected\">"+option+"</option>";
            else
                row += "<option value=\""+option+"\">"+option+"</option>";
        }
        row += "</select>";

        return row;
    }

    private static String createSetField(String key, String value, String tableName) {
        String row = "";

        Select s = new Select();
        Map<String, String> en = s.readColumnName(tableName, key);
        String eV = en.get(key).replace("set(", "");
        eV = eV.substring(0, eV.length()-1);

        for (String option : eV.split(",")) {
            option = option.replaceAll("'", "");
            if (option.equals(value))
                row += "<input type=\"checkbox\" name=\""+key+"_###_"+option+"\" id=\""+key+"_###_"+option+"\" value=\""+option+"\" selected=\"selected\"/>"+option;
            else
                row += "<input type=\"checkbox\" name=\""+key+"_###_"+option+"\" id=\""+key+"_###_"+option+"\" value=\""+option+"\" />"+option;
        }
        return row;
    }

    private static String createNumberInputField(String key, String value) {
        String row = "";

        row += "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"11\" />";
        return row;
    }


    public static String createHiddenField(String key, String value, String className) {
        String row = "";

        row += "<input type=\"hidden\" class=\""+className+"\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" />";
        return row;
    }


    private static String createInputField(String key, String value, String moduleType) {
        String row = "";

        if (moduleType.equals("unit"))
            row += "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"10\" />";
        else if (moduleType.equals("generic"))
            row += "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"30\" />";
        else if (moduleType.equals("performance"))
            row += "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"6\" />";
        else
            row += "<input type=\"text\" name=\""+key+"\" id=\""+key+"\" value=\""+value+"\" size=\"100\" />";
        return row;
    }

    public static String createEditableRow(String key, String value, String tableName, Boolean itf) {

        if (value == null)
            value = "";

        if (!shouldDisplay(key))
            return null;

        if (!itf && key.contains("_itf"))
            return null;

        String row = "<td class=\"input_right\">";
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
            row += createEnumField(key, value, tableName);
            row += "</td>";
            if (key.contains("_rng2") || key.contains("_rng3"))
                row = "<td class=\"label_rng\">"+normalizeToken(k.replace("_enumfield", ""))+"</td>" + row;
            else
                row = "<td class=\"label_left\">"+normalizeToken(k.replace("_enumfield", ""))+"</td>" + row;
        }
        else if (k.contains("_setfield")) {
            row += createSetField(key, value, tableName);
            row += "</td>";

            if (key.contains("_rng2") || key.contains("_rng3"))
                row = "<td class=\"label_rng\">"+normalizeToken(k.replace("_setfield", ""))+"</td>" + row;
            else
                row = "<td class=\"label_left\">"+normalizeToken(k.replace("_setfield", ""))+"</td>" + row;
        }
        else if (k.contains("_nbr")) {
            row += createNumberInputField(key, value);
            row += "</td>";
            if (key.contains("_rng2") || key.contains("_rng3"))
                row = "<td class=\"label_rng\">"+normalizeToken(k.replace("_nbr", ""))+"</td>" + row;
            else
                row = "<td class=\"label_left\">"+normalizeToken(k.replace("_nbr", ""))+"</td>" + row;
        }
        else {
            row += createInputField(key, value, "generic");
            row += "</td>";
            if (key.contains("_rng2") || key.contains("_rng3"))
                row = "<td class=\"label_rng\">"+normalizeToken(k.replace("_nbr", ""))+"</td>" + row;
            else
                row = "<td class=\"label_left\">"+normalizeToken(k.replace("_nbr", ""))+"</td>" + row;
        }
        return row;
    }


    public static String createSpreadSheetRow(ArrayList<String> keys, ArrayList<ArrayList<String>> values, String tableName, String moduleType) {
        String row = "";
        row += "<tr>";
        row += "<th>#</th>";
        for (String k : keys) {
            if (shouldDisplay(k)) {
                row += "<th>" + normalizeToken(k) + "</th>";
            }
        }
        row += "</tr>";

        int lineCount = 0;
        for (ArrayList<String> val : values) {
            int count = 0;
            row += "<tr class='single-rows'>";
            row += "<td>" + Integer.toString(++lineCount) + "</td>";
            for (String v : val) {
                if (!shouldDisplay(keys.get(count))) {
                    count++;
                    continue;
                }
                if (v == null)
                    v = "";

                if (keys.get(count).contains("_enumfield")) {
                    row += "<td>" + createEnumField(keys.get(count)+"_###_"+lineCount, v, tableName) + "</td>";
                }
                else if (keys.get(count).contains("_setfield")) {
                    row += "<td>" + createSetField(keys.get(count)+"_###_"+lineCount, v, tableName) + "</td>";
                }
                else if (keys.get(count).contains("_nbr")) {
                    row += "<td>" + createNumberInputField(keys.get(count)+"_###_"+lineCount, v) + "</td>";
                }
                else {
                    row += "<td>" + createInputField(keys.get(count)+"_###_"+lineCount, v, moduleType) + "</td>";
                }
                count++;
            }
            row += "</tr>\n";
        }

        row += "</tr>";
        row += "<input type=\"hidden\" name=\"numberOf"+tableName+"\" value=\""+lineCount+"\" />";
        return row;
    }


    public static String createPerformanceTable(ArrayList<String> keys, ArrayList<ArrayList<String>> values) {
        String table = "";


        table += "<tr>";

        int rowCount = 0;
        table += "<td>";
        table += "<table class='performance-label'>";
        table += "<tr class='perf-row even-row'></tr>";
        rowCount++;

        for (String k : keys) {
            if (shouldDisplay(k)) {
                if (rowCount%2 == 0)
                    table += "<tr class='perf-row even-row'>";
                else
                    table += "<tr class='perf-row odd-row'>";
                table += "<th>" + normalizeToken(k) + "</th>";
                table += "</tr>";
                rowCount++;
            }
        }
        table += "</table>";
        table += "</td>";

        rowCount = 0;

        table += "<td>";
        table += "<table class='performance-values'>";
        table += "<tr class='perf-row even-row'>";
        rowCount++;
        //table += "<th></th>";
        for (int year=Tokens.ANNUAL_PERFORMANCE_DECADE_START; year<=Tokens.ANNUAL_PERFORMANCE_DECADE_END; year++) {
            table += "<th>" + Integer.toString(year) + "</th>";
        }
        table += "</tr>";

        int keyCount=0;
        for (String k : keys) {
            int yearIndex = 1;
            if (k.equals("Year_yr"))
                yearIndex = keyCount;

            if (shouldDisplay(k)) {
                if (rowCount%2 == 0)
                    table += "<tr class='perf-row even-row'>";
                else
                    table += "<tr class='perf-row odd-row'>";
                //table += "<th>" + normalizeToken(k) + "</th>";
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
                    table += "<td>" + createInputField(k + "_###_" + Integer.toString(year), value, "performance") + "</td>";
                }
                table += "</tr>\n";
                rowCount++;
            }
            keyCount++;
        }

        table += "</table>";
        table += "</td>";
        table += "</tr>";

        return table;
    }

}
