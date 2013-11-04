package org.geo.core

import org.geo.core.db.Select

import java.sql.Connection

/**
 @author: Harihar Shankar, 10/3/13 9:05 AM
 */
public class GeoSystem extends Entity {

    private Integer parentPlantId
    private Integer typeId
    private Integer countryId
    private Integer stateId
    private Connection connection

    public GeoSystem(final Connection connection, final Integer id) {
        super(id)
        this.connection = connection
        final Select main =  new Select();
        final Geo mainGeo = main.read(connection, "History", null, "Description_ID=" + getId().toString());
        typeId = Integer.parseInt(mainGeo.getValueForKey("Type_ID", 0));
        countryId = Integer.parseInt(mainGeo.getValueForKey("Country_ID", 0));
        stateId = Integer.parseInt(mainGeo.getValueForKey("State_ID", 0));
    }

    public Boolean isValidId() {
        final Select main = new Select()
        final Geo mainGeo = main.read(connection, "History", null, "Description_ID="+getId().toString())
        if (mainGeo.getValueForKey("Description_ID", 0)) {
            return true
        }
        return false
    }

    public Integer getTypeId() {
        return typeId
    }

    public Integer getCountryId() {
        return this.countryId;
    }

    public Integer getStateId() {
        return this.stateId;
    }

    public Integer getParentPlantId() {
        Select history = new Select()
        Geo historyGeo = history.read(connection, "History", "Parent_Plant_ID", "Description_ID=" + getId().toString())
        parentPlantId = Integer.parseInt(historyGeo.getValueForKey("Parent_Plant_ID", 0))
        return parentPlantId
    }

    public Integer getLatestRevisionId() {

        if (parentPlantId <= 0) {
            getParentPlantId()
        }
        Select history = new Select();
        Geo historyGeo = history.read(connection, "History", "max(Description_ID) as Description_ID", "Parent_Plant_ID=" + parentPlantId + " AND Accepted=" + 1);

        Integer latestRevisionId = Integer.parseInt(historyGeo.getValueForKey("Description_ID", 0));
        return latestRevisionId;
    }


    public ArrayList<Integer> getAllRevisionIds() {

        if (parentPlantId <= 0) {
            getParentPlantId()
        }

        Select history = new Select();
        Geo historyGeo = history.read(connection, "History", "", "Parent_Plant_ID=" + parentPlantId + " AND Accepted=" + 1, "Description_ID DESC");

        Integer rowCount = historyGeo.getRowCount();
        ArrayList<Integer> revisions = new ArrayList<Integer>(rowCount);

        for (int i=0; i<rowCount; i++) {
            revisions.add(Integer.parseInt(historyGeo.getValueForKey("Description_ID", i)));
        }
        return revisions;
    }
}
