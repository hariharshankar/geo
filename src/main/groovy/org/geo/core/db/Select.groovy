package org.geo.core.db

import java.sql.*;

/**
 * @author: Harihar Shankar, 4/18/13 7:02 PM
 */

public class Select {

    private Connection connection;

    public Select() {
        connection = new DbConnection().getConnection();
    }


    public void close() {
        connection.close()
    }

    public Geo read(String tableName, String selectValues, String whereClause, String sortClause, String limitClause) {

        if (tableName == null || tableName.equals("")) {
            return null;
        }

        Geo geo = new Geo();

        if (selectValues == null || selectValues.equals("")) {
            selectValues = "*";
        }
        else {
            String sValues = selectValues;
            selectValues = "";
            String[] sSplit = sValues.split(",");
            if (sSplit.length-1 >= 0) {
                for (int i=0; i<sSplit.length-1; i++) {
                    selectValues += "`"+sSplit[i]+"`,";
                }
            }
            if (sSplit[sSplit.length-1].contains("distinct(") || sSplit[sSplit.length-1].contains("max(")) {
                selectValues += sSplit[sSplit.length-1];
            }
            else {
                selectValues += "`"+sSplit[sSplit.length-1]+"`";
            }
        }
        if (whereClause == null || whereClause.equals("")) {
            whereClause = "1";
        }

        String sql = "";
        try {
            sql = "SELECT " + selectValues + " FROM " + tableName + " WHERE " + whereClause;
            if (sortClause != null) {
                if (!sortClause.equals("")) {
                    sql += " ORDER BY " + sortClause;
                }
            }
            if (limitClause != null) {
                if (!limitClause.equals("")) {
                    sql += " LIMIT " + limitClause;
                }
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            ArrayList<String> keys = new ArrayList<String>();
            ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();

            int rowCount = 0;
            while (resultSet.next()) {
                ArrayList<String> v = new ArrayList<String>(1);
                for (int i=1; i<=columnCount; i++) {
                    if (rowCount == 0)
                        keys.add(resultSetMetaData.getColumnName(i));
                    v.add(resultSet.getString(i));
                }
                values.add(v);
                rowCount++;
            }

            if (keys.size() == 0) {
                Map<String,String> k = readColumnName(tableName, null);

                keys.addAll(k.keySet());
            }

            statement.close();
            connection.close();

            geo.setKeys(keys);
            geo.setValues(values);
        }
        catch (SQLException ex) {
            //System.out.println(sql);
            System.out.println(ex.getMessage());
            //TODO sql errors
        }

        return geo;
    }

    public Geo read(String tableName, String selectValues, String whereClause, String sortClause) {
        return read(tableName, selectValues, whereClause, sortClause, null);
    }

    public Geo read(String tableName, String selectValues, String whereClause) {
        return read(tableName, selectValues, whereClause, null, null);
    }

    public Geo read(String tableName, String selectValues) {
        return read(tableName, selectValues, null, null, null);
    }

    public Geo read(String tableName) {
        return read(tableName, null, null, null, null);
    }

    public Map<String, String> readColumnName(String tableName, String whereClause) {

        Map<String,String> returnValues = new HashMap<String, String>();

        String sql = "SHOW COLUMNS FROM " + tableName;
        if (whereClause != null) {
            if (!whereClause.equals("")) {
                sql +=  " LIKE '" + whereClause + "'";
            }
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                returnValues.put(resultSet.getString(1), resultSet.getString(2));
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
            //TODO sql errors
        }
        return returnValues;
    }

}
