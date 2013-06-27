package org.geo.health

import com.yammer.metrics.core.HealthCheck;
import com.yammer.metrics.core.HealthCheck.Result
import org.geo.core.db.DbConnection;

/**
 * Created with IntelliJ IDEA.
 * User: harihar
 * Date: 3/28/13
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */


public class DbConnectionHealthCheck extends HealthCheck {
    private final String template;

    public DbConnectionHealthCheck(String template) {
        super("");
        this.template = template;
    }

    @Override
    protected Result check() throws Exception {
        DbConnection db = new DbConnection();

        if (db == null) {
            return Result.unhealthy("Could not connect to database.");
        }
        return Result.healthy();
    }
}