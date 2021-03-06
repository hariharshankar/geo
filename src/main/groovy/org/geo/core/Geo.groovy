package org.geo.core

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.ResultSetMetaData

/**
 * @author: Harihar Shankar, 4/26/13 11:30 AM
 */

@TypeChecked
public class Geo {

    private ArrayList<String> keys = new ArrayList<String>();
    private ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();

    public Geo() {}

    /*
    public Geo(ArrayList<String> keys) {
        this.keys = keys
    }
    */

    public Geo(ArrayList<ArrayList<String>> values) {
        this.values = values
    }

    public Geo(ArrayList<String> keys, ArrayList<ArrayList<String>> values) {
        this.keys = keys
        this.values = values
    }

    public void setKeys(ArrayList<String> keys) {
        this.keys = keys;
    }

    public ArrayList<String> getKeys() {
        return this.keys;
    }


    public void setValues(ArrayList<ArrayList<String>> values) {
        this.values = values;
    }

    public ArrayList<ArrayList<String>> getValues() {
        return values;
    }


    public Integer getRowCount() {
        return values.size();
    }

    public String getValueForKey(String key, int valueIndex) {

        if (this.values.size() == 0) {
            return null;
        }

        if (valueIndex >= this.values.size() || valueIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (this.keys.indexOf(key) == -1) {
            return null;
        }

        return this.values.get(valueIndex).get(this.keys.indexOf(key));
    }
}
