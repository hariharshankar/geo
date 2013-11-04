package org.geo.core.db

import org.geo.core.Geo

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Statement

/**
 @author: Harihar Shankar, 11/3/13 5:01 PM
 */

class Select {

    private String sqlQuery;

    public Geo read(Connection connection, String tableName, String selectValues, String whereClause, String sortClause, String limitClause) {

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
        Statement statement = connection.createStatement();
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

            this.sqlQuery = sql;
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
                LinkedHashMap<String,String> k = readColumnName(connection, tableName, null);

                keys.addAll(k.keySet());
            }
            geo.setKeys(keys);
            geo.setValues(values);
            resultSet.close()
        }
        catch (SQLException ex) {
            //System.out.println(sql);
            System.out.println(ex.getMessage());
            //TODO sql errors
            return null
        }
        finally {
            statement.close()
        }
        return geo;
    }

    public Geo read(Connection connection, String tableName, String selectValues, String whereClause, String sortClause) {
        return read(connection, tableName, selectValues, whereClause, sortClause, null);
    }

    public Geo read(Connection connection, String tableName, String selectValues, String whereClause) {
        return read(connection, tableName, selectValues, whereClause, null, null);
    }

    public Geo read(Connection connection, String tableName, String selectValues) {
        return read(connection, tableName, selectValues, null, null, null);
    }

    public Geo read(Connection connection, String tableName) {
        return read(connection, tableName, null, null, null, null);
    }

    public static LinkedHashMap<String, String> readColumnName(Connection connection, String tableName, String whereClause) {

        LinkedHashMap<String,String> returnValues = new LinkedHashMap<String, String>();

        String sql = "SHOW COLUMNS FROM " + tableName;
        if (whereClause != null) {
            if (!whereClause.equals("")) {
                sql +=  " LIKE '" + whereClause + "'";
            }
        }

        Statement statement = connection.createStatement()
        try {
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                returnValues.put(resultSet.getString(1), resultSet.getString(2));
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
            //TODO sql errors
        }
        finally {
            statement.close()
        }
        return returnValues;
    }

    public String getSqlQuery() {
        return this.sqlQuery
    }
}
