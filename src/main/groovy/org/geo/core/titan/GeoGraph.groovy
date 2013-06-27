package org.geo.core.titan;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;
import com.tinkerpop.frames.FramedGraph;
import org.geo.core.titan.frames.Resource;

/**
 * @author: Harihar Shankar, 3/31/13 6:14 PM
 */

/*
public class GeoGraph<T extends Graph> extends FramedGraph<T> {


    public GeoGraph(final T baseGraph) {
        super(baseGraph);
    }


    public <R extends Resource> R getOrCreateNode(Map<String, Object> properties, final Class<R> type) {
        final Iterator<Vertex> itt = this.getVertices("name", properties.get("name")).iterator();
        if (itt.hasNext()) {
            final Vertex v = itt.next();
            return this.frame(v, type);
        }
        final Vertex v = this.addVertex(null);
        ElementHelper.setProperties(v, properties);
        return this.frame(v, type);
    }

    public void createRelationship(Resource r1, Resource r2, String relation) {
        try {
            this.addEdge(null, r1.asVertex(), r2.asVertex(), relation);
        }
        catch (Exception ex) {
            relation = "has" + relation.substring(0, 1).toUpperCase() + relation.substring(1);
            this.addEdge(null, r1.asVertex(), r2.asVertex(), relation);
        }
        return;
    }
}
*/
