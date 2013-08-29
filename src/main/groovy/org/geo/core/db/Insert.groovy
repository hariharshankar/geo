package org.geo.core.db

import org.geo.core.utils.Tokens

import javax.ws.rs.core.MultivaluedMap;
import java.sql.Connection
import java.sql.PreparedStatement;

/**
 * @author: Harihar Shankar, 6/11/13 1:17 PM
 */


public class Insert {

    private static Connection connection;

    public Insert() {
        connection = new DbConnection().getConnection();
    }

    public void close() {
        connection.close()
    }

    public boolean insert(final String tableName, final MultivaluedMap<String,String> formData, final String moduleType) {

        if (tableName == null || tableName.equals("")) {
            return false;
        }

        if (moduleType == null || moduleType.equals("generic")) {
            insertGenericModule(tableName, formData)
        }
        else if (moduleType.equals("rowColumns")) {
            insertRowColumnModule(tableName, formData)
        }
        else if (moduleType.equals("performance")) {
            insertPerformanceModule(tableName, formData)
        }

        return false;
    }

    private static boolean insertGenericModule(final String tableName, final MultivaluedMap<String, String> formData) {

        ArrayList<Object> sqlValues = [];
        ArrayList<String> sqlValuesType = [];
        String sqlStatement = "INSERT INTO " + tableName + " (";
        String sqlFields = "`Description_ID`"
        String sqlParams = "?"

        Map<String, String> columnNames = new Select().readColumnName(tableName, null)
        for (String k : columnNames.keySet()) {
            if (!formData.get(k))
                continue
            String value = formData.get(k)[0].trim()
            if (!k.find("_ID") && value != null && !value.equals("")) {
                sqlFields += ",`" + k + "`"
                sqlParams += ",?"
                sqlValues.add(value)
                sqlValuesType.add(columnNames.get(k))
            }
        }

        sqlStatement += sqlFields + ") values (" + sqlParams + ")"

        PreparedStatement statement = connection.prepareStatement(sqlStatement)
        statement.setInt(1, Integer.parseInt(formData.get("Description_ID")[0]))

        for (int i=0; i<sqlValues.size(); i++) {
            if (sqlValuesType.get(i).find("integer"))
                statement.setInt(i+2, Integer.parseInt(sqlValues.get(i)))
            else if (sqlValuesType.get(i).find("double"))
                statement.setDouble(i+2, Double.parseDouble(sqlValues.get(i)))
            else
                statement.setString(i+2, sqlValues.get(i).toString())
        }
        statement.execute()
    }


    private static boolean insertPerformanceModule(String tableName, final MultivaluedMap<String, String> formData) {

        tableName = tableName.replace("_Annual", "")
        Map<String, String> columnNames = new Select().readColumnName(tableName, null)

        for (int year=Tokens.ANNUAL_PERFORMANCE_DECADE_END; year>=Tokens.ANNUAL_PERFORMANCE_DECADE_START; year--) {
            ArrayList<Object> sqlValues = [];
            ArrayList<String> sqlValuesType = [];

            String sqlStatement = "INSERT INTO " + tableName + " (";
            String sqlFields = "`Description_ID`, `Year_yr`"
            String sqlParams = "?,?"

            for (String k : columnNames.keySet()) {
                String fieldName = k + "_###_" + year;
                println(fieldName)
                if (formData.get(fieldName) == null)
                    continue
                String value = formData.get(fieldName)[0].trim()
                println(value)
                if (!k.find("_ID") && value != null && !value.equals("")) {
                    sqlFields += ",`" + k + "`"
                    sqlParams += ",?"
                    sqlValues.add(value)
                    sqlValuesType.add(columnNames.get(k))
                }
            }
            if (sqlValues.size() == 0)
                continue
            sqlStatement += sqlFields + ") values (" + sqlParams + ")"

            println(sqlStatement)

            PreparedStatement statement = connection.prepareStatement(sqlStatement)
            statement.setInt(1, Integer.parseInt(formData.get("Description_ID")[0]))
            statement.setString(2, year.toString())

            int i=0;
            for (i=0; i<sqlValues.size(); i++) {
                println("'"+sqlValues.get(i)+"'")
                if (sqlValuesType.get(i).find("integer"))
                    statement.setInt(i+3, Integer.parseInt(sqlValues.get(i)))
                else if (sqlValuesType.get(i).find("double"))
                    statement.setDouble(i+3, Double.parseDouble(sqlValues.get(i)))
                else
                    statement.setString(i+3, sqlValues.get(i).toString())
            }
            statement.execute()
        }


        return true;
    }


    private static boolean insertRowColumnModule(final String tableName, final MultivaluedMap<String, String> formData) {

        ArrayList<Object> sqlValues = [];
        ArrayList<String> sqlValuesType = [];
        String sqlStatement = "INSERT INTO " + tableName + " (";
        String sqlFields = "`Description_ID`"
        String sqlParams = "?"

        Map<String, String> columnNames = new Select().readColumnName(tableName, null)

        if (formData.get("numberOf"+tableName) == null)
            return false

        int numberOfRows = Integer.parseInt(formData.get("numberOf"+tableName)[0]);

        println(numberOfRows)
        for (int no=0; no<numberOfRows; no++) {
            for (String k : columnNames.keySet()) {
                String fieldName = k + "_###_" + no;
                if (formData.get(fieldName) == null)
                    continue
                String value = formData.get(fieldName)[0].trim()
                if (!k.find("_ID") && value != null && !value.equals("")) {
                    sqlFields += ",`" + k + "`"
                    sqlParams += ",?"
                    sqlValues.add(value)
                    sqlValuesType.add(columnNames.get(k))
                }
            }
            sqlStatement += sqlFields + ") values (" + sqlParams + ")"

            println(sqlStatement)

            PreparedStatement statement = connection.prepareStatement(sqlStatement)
            statement.setInt(1, Integer.parseInt(formData.get("Description_ID")[0]))

            for (int i=0; i<sqlValues.size(); i++) {
                println("'"+sqlValues.get(i)+"'")
                if (sqlValuesType.get(i).find("integer"))
                    statement.setInt(i+2, Integer.parseInt(sqlValues.get(i)))
                else if (sqlValuesType.get(i).find("double"))
                    statement.setDouble(i+2, Double.parseDouble(sqlValues.get(i)))
                else
                    statement.setString(i+2, sqlValues.get(i).toString())
            }
        }

        return true;
    }
}
