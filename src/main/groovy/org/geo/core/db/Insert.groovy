package org.geo.core.db

import org.geo.core.utils.Tokens

import javax.ws.rs.core.MultivaluedMap;
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement;

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

    public Integer insert(final String tableName, final MultivaluedMap<String,String> formData, final String moduleType) {

        if (tableName == null || tableName.equals("")) {
            return 0;
        }

        if (moduleType == null || moduleType.equals("generic")) {
            return insertGenericModule(tableName, formData)
        }
        else if (moduleType.equals("rowColumns")) {
            return insertRowColumnModule(tableName, formData)
        }
        else if (moduleType.equals("performance")) {
            return insertPerformanceModule(tableName, formData)
        }
        else if (moduleType.equals("history")) {
            return insertHistoryModule(formData)
        }

        return 0;
    }

    private static Integer insertHistoryModule(final MultivaluedMap<String,String> formdata) {
        String sqlFields = "`User_ID`, `Moderated`, `Moderator_ID`, `Type_ID`, `Country_ID`, `State_ID`, `Accepted`"
        String sqlValues = "?,?,?,?,?,?,?"

        if (Integer.parseInt(formdata["Parent_Plant_ID"][0]) > 0) {
            sqlFields += ",`Parent_Plant_ID`"
            sqlValues += ",?"
        }

        String sqlStatement = "INSERT INTO History (" + sqlFields + ") values (" + sqlValues + ")"
        PreparedStatement statement = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)
        statement.setInt(1, Integer.parseInt(formdata['User_ID'][0]))
        statement.setInt(2, Integer.parseInt(formdata['Moderated'][0]))
        statement.setInt(3, Integer.parseInt(formdata['Moderator_ID'][0]))
        statement.setInt(4, Integer.parseInt(formdata['Type_ID'][0]))
        statement.setInt(5, Integer.parseInt(formdata['Country_ID'][0]))
        statement.setInt(6, Integer.parseInt(formdata['State_ID'][0]))
        statement.setInt(7, Integer.parseInt(formdata['Accepted'][0]))
        if (Integer.parseInt(formdata['Parent_Plant_ID'][0]) > 0) {
            statement.setInt(8, Integer.parseInt(formdata['Parent_Plant_ID'][0]))
        }

        statement.execute()
        ResultSet resultSet = statement.getGeneratedKeys()
        resultSet.next()
        int descriptionId = resultSet.getInt(1)

        if (Integer.parseInt(formdata["Parent_Plant_ID"][0]) == 0) {
            String parentUpdateStmt = "UPDATE HISTORY SET Parent_Plant_ID=? WHERE Description_ID=?"
            PreparedStatement preparedStatement = connection.prepareStatement(parentUpdateStmt)
            preparedStatement.setInt(1, descriptionId)
            preparedStatement.setInt(2, descriptionId)
            preparedStatement.execute()
        }

        return descriptionId
    }

    private static Integer insertGenericModule(final String tableName, final MultivaluedMap<String, String> formData) {

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

        return 1
    }


    private static Integer insertPerformanceModule(String tableName, final MultivaluedMap<String, String> formData) {

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
            if (sqlValues.size() == 0)
                continue
            sqlStatement += sqlFields + ") values (" + sqlParams + ")"

            PreparedStatement statement = connection.prepareStatement(sqlStatement)
            statement.setInt(1, Integer.parseInt(formData.get("Description_ID")[0]))
            statement.setString(2, year.toString())

            for (int i=0; i<sqlValues.size(); i++) {
                if (sqlValuesType.get(i).find("integer"))
                    statement.setInt(i+3, Integer.parseInt(sqlValues.get(i)))
                else if (sqlValuesType.get(i).find("double"))
                    statement.setDouble(i+3, Double.parseDouble(sqlValues.get(i)))
                else
                    statement.setString(i+3, sqlValues.get(i).toString())
            }
            statement.execute()
        }
        return 1;
    }


    private static Integer insertRowColumnModule(final String tableName, final MultivaluedMap<String, String> formData) {

        Map<String, String> columnNames = new Select().readColumnName(tableName, null)

        println(tableName)
        if (formData.get("numberOf"+tableName) == null)
            return 0

        int numberOfRows = Integer.parseInt(formData.get("numberOf"+tableName)[0]);


        for (int no=1; no<=numberOfRows; no++) {
            ArrayList<Object> sqlValues = [];
            ArrayList<String> sqlValuesType = [];
            String sqlStatement = "INSERT INTO " + tableName + " (";
            String sqlFields = "`Description_ID`"
            String sqlParams = "?"

            for (String k : columnNames.keySet()) {
                String fieldName = k + "_###_" + no;
                if (formData.get(fieldName) == null)
                    continue
                String value = formData.get(fieldName)[0].trim()
                if (!k.find("_ID") && value != null && !value.equals("")) {
                    sqlFields += ",`" + k + "`"
                    sqlParams += ",?"
                    println(k + ": " + value)
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

        return 1;
    }
}
