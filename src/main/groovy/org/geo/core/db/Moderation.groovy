package org.geo.core.db


/**
 * @author: Harihar Shankar, 4/30/13 6:55 PM
 */


public class Moderation {

    public Integer getLatestRevisionId(final Integer parentPlantID) {

        Select history = new Select();
        Geo historyGeo = history.read("History", "", "Parent_Plant_ID=" + parentPlantID + " AND Accepted=" + 1, "Description_ID DESC", "0,1");

        Integer descriptionID = Integer.parseInt(historyGeo.getValueForKey("Description_ID", 0));

        return descriptionID;
    }


    public ArrayList<Integer> getAllRevisionIds(final Integer parentPlantID) {

        Select history = new Select();
        Geo historyGeo = history.read("History", "", "Parent_Plant_ID=" + parentPlantID + " AND Accepted=" + 1, "Description_ID DESC");

        Integer rowCount = historyGeo.getRowCount();
        ArrayList<Integer> revisions = new ArrayList<Integer>(rowCount);
        for (int i=0; i<rowCount; i++)
            revisions.add(Integer.parseInt(historyGeo.getValueForKey("Description_ID", i)));

        return revisions;
    }


    public ArrayList<Integer> getAllRevisionsForTypeAndCountry(final Integer countryId, final Integer typeId) {

        Select history = new Select();
        Geo historyGeo = history.read("History", "distinct(Parent_Plant_ID)", "Country_ID=" + countryId + " AND Type_ID=" + typeId + " AND Accepted=1");

        Integer rowCount = historyGeo.getRowCount();
        ArrayList<Integer> revisions = new ArrayList<Integer>(rowCount);

        for (int i=0; i<rowCount; i++) {
            revisions.add(getLatestRevisionId(Integer.parseInt(historyGeo.getValueForKey("Parent_Plant_ID", i))));
        }
        return revisions;
    }


}
