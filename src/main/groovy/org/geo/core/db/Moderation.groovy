package org.geo.core.db

/**
 * @author: Harihar Shankar, 4/30/13 6:55 PM
 */


public class Moderation {

    public Integer getParentPlantId(final Integer descriptionId) {
        Select history = new Select()
        Geo historyGeo = history.read("History", "Parent_Plant_ID", "Description_ID=" + descriptionId)
        Integer parentPlantID = Integer.parseInt(historyGeo.getValueForKey("Parent_Plant_ID", 0))
        history.close()
        return parentPlantID
    }

    public Integer getLatestRevisionId(final Integer parentPlantID) {

        Select history = new Select();
        Geo historyGeo = history.read("History", "", "Parent_Plant_ID=" + parentPlantID + " AND Accepted=" + 1, "Description_ID DESC", "0,1");

        Integer descriptionID = Integer.parseInt(historyGeo.getValueForKey("Description_ID", 0));
        history.close()
        return descriptionID;
    }


    public ArrayList<Integer> getAllRevisionIds(final Integer parentPlantID) {

        Select history = new Select();
        Geo historyGeo = history.read("History", "", "Parent_Plant_ID=" + parentPlantID + " AND Accepted=" + 1, "Description_ID DESC");

        Integer rowCount = historyGeo.getRowCount();
        ArrayList<Integer> revisions = new ArrayList<Integer>(rowCount);
        for (int i=0; i<rowCount; i++)
            revisions.add(Integer.parseInt(historyGeo.getValueForKey("Description_ID", i)));

        history.close()
        return revisions;
    }


    public Geo getAllRevisionsForTypeAndCountry(final Integer countryId, final Integer typeId) {

        Select history = new Select();
        Geo historyGeo = history.read("History", "distinct(Parent_Plant_ID)", "Country_ID=" + countryId + " AND Type_ID=" + typeId + " AND Accepted=1");

        Integer rowCount = historyGeo.getRowCount();

        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();

        keys.add("Description_ID")
        keys.add("Name")

        for (int i=0; i<rowCount; i++) {
            ArrayList<String> v = new ArrayList<String>(1);
            String descriptionId = getLatestRevisionId(Integer.parseInt(historyGeo.getValueForKey("Parent_Plant_ID", i))).toString();

            // TYPE values
            final Select type =  new Select();
            final Geo typeGeo = type.read("Type", null, "Type_ID=" + typeId);
            type.close()
            final String typeName = typeGeo.getValueForKey("Type", 0);

            Select description = new Select();
            Geo descriptionSearchResult = description.read(typeName + "_Description", "Name_omit", "Description_ID=" + descriptionId);
            String name = descriptionSearchResult.getValueForKey("Name_omit", 0);

            description.close()
            v.add(descriptionId)
            v.add(name)

            values.add(v)
        }

        Geo revisions = new Geo();
        revisions.setKeys(keys)
        revisions.setValues(values)
        history.close()
        return revisions;
    }

    public Geo getTypeForDb(final String databaseType) {
        Select type = new Select();
        Geo typeGeo = type.read("Type", "Type_ID,Type", "Database_Type='"+databaseType+"'", "Type_ID ASC")
        type.close()
        return typeGeo
    }


    public Geo getCountryForType(final String type) {
        Select typeSelect = new Select();
        Geo typeGeo = typeSelect.read("Type", "Type_ID", "Type='"+type+"'")
        String typeId = typeGeo.getValueForKey("Type_ID", 0)
        typeSelect.close()

        Select history = new Select();
        Geo historyGeo = history.read("History", "distinct(Country_ID)", "Type_ID="+typeId)
        history.close()
        Integer numberOfCountries = historyGeo.getRowCount()

        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();

        keys.add("Country_ID")
        keys.add("Country")

        for (int c=0; c<numberOfCountries; c++) {
            String countryId = historyGeo.getValueForKey("Country_ID", c)
            Select countrySelect = new Select();
            Geo countryGeo = countrySelect.read("Country", "Country_ID,Country", "Country_ID="+countryId)

            countrySelect.close()

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
        Geo countryGeo = countrySelect.read("Country", "Country_ID", "Country LIKE '"+country+"'")
        String countryId = countryGeo.getValueForKey("Country_ID", 0);
        countrySelect.close()

        Select stateSelect = new Select();
        Geo stateGeo = stateSelect.read("State", "State_ID,State", "Country_ID="+countryId)
        stateSelect.close()
        return stateGeo
    }

    public Geo getDatabaseType(){

        Select type = new Select();
        Geo typeGeo = type.read("Type", "distinct(Database_Type)", "", "")
        type.close()
        return typeGeo
    }
}
