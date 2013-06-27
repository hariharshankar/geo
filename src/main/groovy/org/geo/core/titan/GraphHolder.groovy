package org.geo.core.titan;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.configuration.BaseConfiguration;

/**
 * @author: Harihar Shankar, 4/2/13 12:16 PM
 */
public class GraphHolder {

    public static GeoGraph getGraph() {
        BaseConfiguration conf = new BaseConfiguration();
        conf.setProperty("storage.hostname", "127.0.0.1");
        conf.setProperty("storage.backend", "cassandra");
        //conf.setProperty("storage.index.search.backend", "elasticsearch");
        //conf.setProperty("storage.index.search.hostname", "127.0.0.1");

        //return new GeoGraph(TitanFactory.open(conf));

        TitanGraph g = TitanFactory.open(conf);
        g.createKeyIndex("name", Vertex.class);
        g.createKeyIndex("uri", Vertex.class);

        //g.makeType().name("name").dataType(String.class).indexed("search", Vertex.class).indexed(Vertex.class).unique(Direction.OUT).makePropertyKey();
        //g.makeType().name("uri").dataType(String.class).indexed(Vertex.class).unique(Direction.BOTH).makePropertyKey();

        //g.commit();

        return new GeoGraph(g);
    }
}
