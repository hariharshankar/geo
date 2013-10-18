package org.geo.core

import org.geo.core.db.Geo
import org.geo.core.db.Select

/**
 @author: Harihar Shankar, 10/3/13 9:05 AM
 */
public class GeoSystem extends Entity {

    private Integer parentPlantId
    private HashMap<String, String> type = new HashMap<String, String>();
    private HashMap<String, String> country = new HashMap<String, String>();
    private HashMap<String, String> state = new HashMap<String, String>();


    public GeoSystem(final Integer id) {
        super(id)
    }

    public Boolean isValidId() {
        final Select main = new Select()
        final Geo mainGeo = main.read("History", null, "Description_ID="+getId().toString())
        if (mainGeo.getValueForKey("Description_ID", 0)) {
            return true
        }
        return false
    }

    public HashMap<String, String> getType() {
        if (this.type.size() == 0) {
            final Select main =  new Select();
            final Geo mainGeo = main.read("History", null, "Description_ID=" + getId().toString());
            final String typeId = mainGeo.getValueForKey("Type_ID", 0);

            final Select typeSelect =  new Select();
            final Geo typeGeo = typeSelect.read("Type", null, "Type_ID=" + typeId);
            this.type.put("typeId", typeId);
            this.type.put("typeName", typeGeo.getValueForKey("Type", 0))
            this.type.put("typeDatabase", typeGeo.getValueForKey("Database_Type", 0));
        }
        return this.type;
    }

    public HashMap<String, String> getCountry() {
        if (this.country.size() == 0) {
            final Select main =  new Select();
            final Geo mainGeo = main.read("History", null, "Description_ID=" + getId().toString());
            final String countryId = mainGeo.getValueForKey("Country_ID", 0);

            final Select countrySelect =  new Select();
            final Geo countryGeo = countrySelect.read("Country", null, "Country_ID=" + countryId);
            this.country.put("countryId", countryId);
            this.country.put("countryName", countryGeo.getValueForKey("Type", 0))
            this.country.put("countryDatabase", countryGeo.getValueForKey("Database_Type", 0));
        }
        return this.country;
    }

    public HashMap<String,String> getState() {
        if (this.state.size() == 0) {
            final Select main =  new Select();
            final Geo mainGeo = main.read("History", null, "Description_ID=" + getId().toString());
            final String stateId = mainGeo.getValueForKey("State_ID", 0);

            final Select stateSelect =  new Select();
            final Geo stateGeo = stateSelect.read("State", null, "State_ID=" + stateId);
            this.state.put("stateId", stateId);
            this.state.put("stateName", stateGeo.getValueForKey("State", 0))
            this.state.put("stateDatabase", stateGeo.getValueForKey("Database_Type", 0));
        }
        return this.state;
    }

    public String getFeatures() {
        if (type.size() == 0) {
            getType()
        }
        String typeId = type.get("typeId")
        final Select typeFeatures =  new Select();
        final Geo typeFeaturesGeo = typeFeatures.read("Type_Features", null, "Type_ID=" + typeId);
        final String features = typeFeaturesGeo.getValueForKey("Features", 0);
        return features
    }

    public Integer getParentPlantId() {
        Select history = new Select()
        Geo historyGeo = history.read("History", "Parent_Plant_ID", "Description_ID=" + getId().toString())
        parentPlantId = Integer.parseInt(historyGeo.getValueForKey("Parent_Plant_ID", 0))
        history.close()
        return parentPlantId
    }

    public Integer getLatestRevisionId() {

        if (parentPlantId <= 0) {
            getParentPlantId()
        }
        Select history = new Select();
        Geo historyGeo = history.read("History", "", "Parent_Plant_ID=" + parentPlantId + " AND Accepted=" + 1, "Description_ID DESC", "0,1");

        Integer latestRevisionId = Integer.parseInt(historyGeo.getValueForKey("Description_ID", 0));
        history.close()
        return latestRevisionId;
    }


    public ArrayList<Integer> getAllRevisionIds() {

        if (parentPlantId <= 0) {
            getParentPlantId()
        }

        Select history = new Select();
        Geo historyGeo = history.read("History", "", "Parent_Plant_ID=" + parentPlantId + " AND Accepted=" + 1, "Description_ID DESC");

        Integer rowCount = historyGeo.getRowCount();
        ArrayList<Integer> revisions = new ArrayList<Integer>(rowCount);

        for (int i=0; i<rowCount; i++) {
            revisions.add(Integer.parseInt(historyGeo.getValueForKey("Description_ID", i)));
        }
        history.close()
        return revisions;
    }
}
