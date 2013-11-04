package org.geo.core.db

import org.geo.core.Geo
import org.geo.core.GeoSystem

import java.sql.Connection

/**
 * @author: Harihar Shankar, 4/30/13 6:55 PM
 */


public class Moderation {

    private Connection connection

    public Moderation(Connection connection) {
        this.connection = connection
    }

    public Geo getAllRevisionsForTypeAndCountry(final Integer countryId, final Integer typeId) {

        Select history = new Select();
        Geo historyGeo = history.read(connection, "History", "distinct(Parent_Plant_ID)", "Country_ID=" + countryId + " AND Type_ID=" + typeId + " AND Accepted=1");

        Integer rowCount = historyGeo.getRowCount();

        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();

        keys.add("Description_ID")
        keys.add("Name")

        for (int i=0; i<rowCount; i++) {
            ArrayList<String> v = new ArrayList<String>(1);
            GeoSystem geoSystem = new GeoSystem(connection, Integer.parseInt(historyGeo.getValueForKey("Parent_Plant_ID",i)))
            String descriptionId = geoSystem.getLatestRevisionId().toString()

            Select typeDAO = new Select()
            Geo type = typeDAO.read(connection, "Type", null, "Type_ID="+typeId)
            if (!type) {
                return null
            }
            String typeName = type.getValueForKey("Type", 0)

            Select description = new Select();
            Geo descriptionSearchResult = description.read(connection, typeName + "_Description", "Name_omit", "Description_ID=" + descriptionId);
            String name = descriptionSearchResult.getValueForKey("Name_omit", 0);
            v.add(descriptionId)
            v.add(name)

            values.add(v)
        }

        Geo revisions = new Geo();
        revisions.setKeys(keys)
        revisions.setValues(values)
        return revisions;
    }

    public Geo getTypeForDb(final String databaseType) {
        Select type = new Select();
        Geo typeGeo = type.read(connection, "Type", "Type_ID,Type", "Database_Type='"+databaseType+"'", "Type_ID ASC")
        return typeGeo
    }


    public Geo getCountryForType(final String type) {
        Select typeSelect = new Select();
        Geo typeGeo = typeSelect.read(connection, "Type", "Type_ID", "Type='"+type+"'")
        String typeId = typeGeo.getValueForKey("Type_ID", 0)

        Select history = new Select();
        Geo historyGeo = history.read(connection, "History", "distinct(Country_ID)", "Type_ID="+typeId)
        Integer numberOfCountries = historyGeo.getRowCount()

        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();

        keys.add("Country_ID")
        keys.add("Country")

        for (int c=0; c<numberOfCountries; c++) {
            String countryId = historyGeo.getValueForKey("Country_ID", c)
            Select countrySelect = new Select();
            Geo countryGeo = countrySelect.read(connection, "Country", "Country_ID,Country", "Country_ID="+countryId)


            ArrayList<String> v = new ArrayList<String>(1);
            v.add(countryGeo.getValueForKey("Country_ID", 0))
            v.add(countryGeo.getValueForKey("Country", 0))
            values.add(v)
        }
        Geo geoCountry = new Geo();
        geoCountry.setKeys(keys)
        geoCountry.setValues(values)

        return geoCountry
    }

    public Geo getStateForCountry(final String country) {
        Select countrySelect = new Select();
        Geo countryGeo = countrySelect.read(connection, "Country", "Country_ID", "Country LIKE '"+country+"'")
        String countryId = countryGeo.getValueForKey("Country_ID", 0);

        Select stateSelect = new Select();
        Geo stateGeo = stateSelect.read(connection, "State", "State_ID,State", "Country_ID="+countryId)
        return stateGeo
    }

    public Geo getDatabaseType() {

        Select type = new Select();
        Geo typeGeo = type.read(connection, "Type", "distinct(Database_Type)", "", "")
        return typeGeo
    }
}
