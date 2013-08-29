package org.geo.core.utils

import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject
import org.geo.core.db.DbConnection
import org.geo.core.db.Geo
import org.geo.core.db.Moderation
import org.geo.core.db.Select

import java.sql.Blob
import java.sql.Connection
import java.sql.PreparedStatement

/**
 @author: Harihar Shankar, 8/7/13 11:22 AM
 */
class Metadata {

    private static boolean insertToTable(final String tableName, final HashMap<String, Object> data) {

        Connection connection = new DbConnection().getConnection();
        ArrayList<Object> sqlValues = [];
        ArrayList<String> sqlValuesType = [];
        String sqlStatement = "INSERT INTO " + tableName + " (";
        String sqlFields = "`Type_ID`,`Country_ID`"
        String sqlParams = "?,?"

        Select select = new Select()
        Map<String, String> columnNames = select.readColumnName(tableName, null)
        select.close()
        for (String k : columnNames.keySet()) {
            if (!data.get(k))
                continue
            String value = data.get(k)
            if (!k.find("_ID") && value != null && !value.equals("")) {
                sqlFields += ",`" + k + "`"
                sqlParams += ",?"
                sqlValues.add(value)
                sqlValuesType.add(columnNames.get(k))
            }
        }

        sqlStatement += sqlFields + ") values (" + sqlParams + ")"

        PreparedStatement statement = connection.prepareStatement(sqlStatement)
        println(data.get("Type_ID"))
        statement.setInt(1, data.get("Type_ID"))
        statement.setInt(2, data.get("Country_ID"))

        for (int i=0; i<sqlValues.size(); i++) {
            if (sqlValuesType.get(i).find("integer"))
                statement.setInt(i+3, Integer.parseInt(sqlValues.get(i)))
            else if (sqlValuesType.get(i).find("double"))
                statement.setDouble(i+3, Double.parseDouble(sqlValues.get(i)))
            else if (sqlValuesType.get(i).find("blob")) {
                InputStream is = new ByteArrayInputStream(sqlValues.get(i).getBytes())
                statement.setBlob(i+3, is)
            }
            else
                statement.setString(i+3, sqlValues.get(i))
        }
        statement.execute()
        statement.close()
        connection.close()
    }


    public static void main(String[] args) {

        ArrayList<ArrayList<String>> fields = []
        fields.add(["_Performance", "Year_yr","Total_Gigawatt_Hours_Generated_nbr"])
        fields.add(["_Performance", "Year_yr", "CO2_Emitted_(Tonnes)_nbr"])
        fields.add(["_Unit_Description", "Date_Commissioned_dt", "Capacity_(MWe)_nbr"])

        Moderation moderation = new Moderation()

        Geo typesGeo = moderation.getTypeForDb("PowerPlants")
        for (ArrayList<String> types : typesGeo.getValues()) {

            final String typeName = types.get(1)
            final Integer typeId = Integer.parseInt(types.get(0))

            Geo countryGeo = moderation.getCountryForType(typeName)

            for (ArrayList<String> country : countryGeo.getValues()) {

                HashMap<String, JSONObject> tableFields = [:]

                Integer numberOfPlants = 0
                Integer startYear = 0;
                Integer endYear = 0;
                Float total_added = 0

                //HashMap<String, ArrayList<String>> fieldValues = new HashMap<String, ArrayList<String>>()

                final String countryName = country.get(1)
                final Integer countryId = Integer.parseInt(country.get(0))

                Geo revisionGeo = moderation.getAllRevisionsForTypeAndCountry(countryId, typeId)
                numberOfPlants = revisionGeo.getValues().size();

                for (ArrayList<String> field : fields) {
                    ArrayList<String> keys = new ArrayList<String>();
                    ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();

                    String f = field.get(1)
                    keys.add(field.get(1))

                    for (int y=2; y<field.size(); y++) {
                        f += "," + field.get(y)
                        keys.add(field.get(y))
                    }
                    //ArrayList<String> values = new ArrayList<String>();
                    HashMap<String, Double> cumulativeValue = new HashMap<String, Double>();

                    for (ArrayList<String> revision : revisionGeo.getValues()) {
                        final Integer descriptionId = Integer.parseInt(revision.get(0))

                        Select gwh = new Select();
                        Geo gwhGeo
                        try {
                            gwhGeo = gwh.read(typeName + field.get(0), f, "Description_ID=" + descriptionId);
                        }
                        catch (Exception e1) {
                            println(e1.getMessage())
                            gwh.close()
                            break
                        }
                        gwh.close()

                        Integer gwhYears = gwhGeo.getRowCount();

                        for (int y=0; y<gwhYears; y++) {
                            String year = gwhGeo.getValueForKey(field.get(1), y);
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
                                    gwhValue = Double.parseDouble(gwhGeo.getValueForKey(field.get(2), y));
                                    cumulativeValue.put(year, gwhValue);
                                } catch (Exception e) {}
                            }
                            else {
                                Double gwhValue = cumulativeValue.get(year);

                                try {
                                    gwhValue += Double.parseDouble(gwhGeo.getValueForKey(field.get(2), y));
                                } catch (Exception e) {}
                                cumulativeValue.put(year, gwhValue);
                            }
                        }
                    } // revision
                    for (int y=startYear; y<=endYear; y++) {
                        String v = (cumulativeValue.get(String.format("%d", y)) == null) ? 0.0 : cumulativeValue.get(String.format("%d", y))
                        total_added = total_added + Float.parseFloat(v)
                        values.add([String.format("%d", y), v])
                        //values.add(v)
                    }
                    JSONArray jValues = new JSONArray(values)
                    JSONArray jKeys = new JSONArray(keys)

                    JSONObject geoJson = new JSONObject();
                    geoJson.put("keys", jKeys)
                    geoJson.put("values", jValues)

                    if (field.get(2).contains("Capacity")) {
                        tableFields["New_Capacity_Added"] = geoJson
                    }
                    else if (field.get(2).contains("Gigawatt")) {
                        tableFields["Annual_Gigawatt_Hours_Generated"] = geoJson
                    }
                    else if (field.get(2).contains("CO2_Emitted")) {
                        tableFields["Annual_CO2_Emitted"] = geoJson
                    }

                    //fieldValues.put(field.get(2), values)
                } //fields
                /*
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
                */


                insertToTable("metadata", [Type_ID: typeId, Country_ID: countryId, Number_of_Plants: numberOfPlants, New_Capacity_Added: tableFields["New_Capacity_Added"].toString(), Annual_Gigawatt_Hours_Generated: tableFields['Annual_Gigawatt_Hours_Generated'], Annual_CO2_Emitted: tableFields['Annual_CO2_Emitted'], Cumulative_Capacity: total_added])

                //break;
            } // country
            //break;
        } // type
    }
}
