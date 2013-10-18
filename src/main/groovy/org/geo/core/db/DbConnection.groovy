package org.geo.core.db

import groovy.transform.TypeChecked

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author: Harihar Shankar, 3/28/13 2:49 PM
 */


@TypeChecked
public class DbConnection {

    private Connection connection = null;

    public Connection getConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/geoDev?user=geo&password=0p3nM0d3!");
        } catch (SQLException ex) {
            //TODO sql error
            println(ex);
        }
        return connection;
    }

}
