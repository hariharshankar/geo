package org.geo;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import org.geo.resources.*;

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
        environment.addResource(new GetFactSheet());
        environment.addResource(new SubmitFactsheet());
        environment.addResource(new SearchResources());
        environment.addResource(new GetSummaryResults());
        environment.addResource(new GetMapJson());
        environment.addResource(new GetLineChartJson());
        environment.addResource(new GetJsonList());
        environment.addResource(new GetPlantList());
        //environment.addHealthCheck(new DbConnectionHealthCheck());
    }

}