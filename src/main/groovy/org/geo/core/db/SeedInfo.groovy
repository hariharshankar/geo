package org.geo.core.db


/**
 * @author: Harihar Shankar, 5/1/13 12:51 PM
 */


public class SeedInfo {

    private HashMap<String, String> type = new HashMap<String, String>();
    private HashMap<String, String> country = new HashMap<String, String>();
    private HashMap<String, String> state = new HashMap<String, String>();


    public SeedInfo(final Integer descriptionId) {

        final Select main =  new Select();
        final Geo mainGeo = main.read("History", null, "Description_ID=" + descriptionId);
        final String typeId = mainGeo.getValueForKey("Type_ID", 0);
        final String countryId = mainGeo.getValueForKey("Country_ID", 0);
        final String stateId = mainGeo.getValueForKey("State_ID", 0);

        // TYPE values
        final Select type =  new Select();
        final Geo typeGeo = type.read("Type", null, "Type_ID=" + typeId);
        final String typeName = typeGeo.getValueForKey("Type", 0);
        final String typeDatabaseName = typeGeo.getValueForKey("Database_Type", 0);
        setType(typeId, typeName, typeDatabaseName);

        // Country table
        final Select country =  new Select();
        final Geo countryGeo = country.read("Country", null, "Country_ID=" + countryId);
        final String countryName = countryGeo.getValueForKey("Country", 0);
        setCountry(countryId, countryName);

        // State table
        final Select state =  new Select();
        final Geo stateGeo = state.read("State", null, "State_ID=" + stateId);
        final String stateName = stateGeo.getValueForKey("State", 0);

        setState(stateId, stateName);
    }

    public HashMap<String, String> getType() {
        return this.type;
    }

    public HashMap<String, String> getCountry() {
        return this.country;
    }

    public HashMap<String,String> getState() {
        return this.state;
    }

    private void setType(final String typeId, final String typeName, final String typeDatabase) {
        this.type.put("typeId", typeId);
        this.type.put("typeName", typeName);
        this.type.put("typeDatabase", typeDatabase);
    }

    private void setCountry(final String countryId, final String countryName) {
        this.country.put("countryId", countryId);
        this.country.put("countryName", countryName);
    }

    private void setState(final String stateId, final String stateName) {
        this.state.put("stateId", stateId);
        this.state.put("stateName", stateName);
    }
}
