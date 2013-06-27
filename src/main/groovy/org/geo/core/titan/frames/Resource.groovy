package org.geo.core.titan.frames;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;

import java.net.URI;

/**
 * @author: Harihar Shankar, 3/30/13 11:11 AM
 */

public interface Resource extends VertexFrame {

    @Property("name")
    public String getName();

    @Property("name")
    public void setName(final String name);

    @Property("uri")
    public String getUri();

    @Property("uri")
    public void setUri(final String uri);

}
