package org.geo;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.jdbi.DBIFactory
import org.geo.resources.*
import org.skife.jdbi.v2.DBI

import java.sql.Connection;

/**
 * @author: Harihar Shankar, 3/28/13 4:07 PM
 */

public class GeoService extends Service<GeoConfiguration> {
    public static void main(String[] args) throws Exception {
        new GeoService().run(args);
    }

    @Override
    public void initialize(Bootstrap<GeoConfiguration> bootstrap) {
        bootstrap.setName("");
        bootstrap.addBundle(new ConfiguredAssetsBundle("/static/", "/static/"));
    }

    public void run(GeoConfiguration configuration,
                    Environment environment) {
        //environment.addHealthCheck(new DbConnectionHealthCheck());

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "mysql")

        Connection db = jdbi.open().getConnection()

        environment.addResource(new GetJsonSummary(db))
        environment.addResource(new GetFactSheet(db));
        environment.addResource(new SubmitFactsheet(db));
        environment.addResource(new SearchResources());
        environment.addResource(new GetSummaryResults(db));
        environment.addResource(new GetMapJson(db));
        environment.addResource(new GetLineChartJson(db));
        environment.addResource(new GetJsonList(db));
        environment.addResource(new GetPlantList(db));
        environment.addResource(new GetSearchMap(db));
    }

}