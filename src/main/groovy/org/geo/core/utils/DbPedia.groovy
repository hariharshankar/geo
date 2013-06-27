package org.geo.core.utils;

import com.mysql.jdbc.jdbc2.optional.SuspendableXAConnection;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.apache.commons.configuration.BaseConfiguration;
import org.geo.core.titan.GeoGraph;
import org.geo.core.titan.GraphHolder;
import org.geo.core.titan.frames.Resource;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Harihar Shankar, 3/31/13 7:53 PM
 */

public class DbPedia {



    /*
     * Command line:
     * java -server -Xms1G -Xmx1G -cp geo-1.0-SNAPSHOT.jar org.geo.core.utils.DbPedia
     */
    public static void main(String[] args) {
    //public void loadFile(String fileName) {

        final String[] namespaces = [
                "http://dbpedia.org/property/",
                "http://dbpedia.org/ontology/",
                "http://dbpedia.org/resource/",
                "http://geonames.org/ontology#",
                "http://georss.org/georss/",
                "http://www.w3.org/2003/01/geo/wgs84_pos#",
                "http://www.w3.org/ns/prov#",
                "http://www.w3.org/2007/uwa/context/common.owl#",
                "http://www.w3.org/2000/01/rdf-schema#",
                "http://xmlns.com/foaf/0.1/",
                "http://www.opengis.net/gml/",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                "http://www.w3.org/2002/07/owl#",
                "http://schema.org/",
                "http://purl.org/ontology/bibo/"
        ];

        String fileName = "/Users/Harihar/dbpedia/geo_coordinates_en.nt";
        GeoGraph<TitanGraph> g = GraphHolder.getGraph();

        long fileSize = 0;
        long filePosition = 0;
        MappedByteBuffer f;

        try {
            fileSize = new RandomAccessFile(fileName, "r").length();

            if (fileSize >= Integer.MAX_VALUE)
                return;

            f = new RandomAccessFile(fileName, "r").getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, filePosition, fileSize);
        }
        catch (Exception e) {
            System.out.println("Error Reading file: " + e);
            return;
        }

        long i = 0;
        long lines = 0;
        while (i<fileSize) {
            Boolean commentLine = false;
            int j = 0;
            char[] line = new char[10000];

            char c = (char)f.get();
            i++;
            if (c == '#') {
                commentLine = true;
            }
            while (c != '\n') {
                line[j++] = c;
                c = (char)f.get();
                i++;
            }

            String[] parts;
            if (!commentLine) {
                lines++;
                String l = String.valueOf(line);
                l = l.replaceAll("<", "");
                l = l.replaceAll(">", "");
                parts = l.split(" ");

                if (parts.length != 4)
                    continue;

                // subject
                String subject = parts[0];
                for (String n : namespaces) {
                    if (subject.contains(n)) {
                        subject = subject.replace(n, "");
                        break;
                    }
                }
                subject = subject.replaceAll("_", " ");

                // predicate
                String predicate = parts[1];
                for (String n : namespaces) {
                    if (predicate.contains(n)) {
                        predicate = predicate.replace(n, "");
                    }
                }
                predicate = predicate.replaceAll("_", " ");

                // object
                String object = parts[2];
                if (parts[2].contains("^^")) {
                    object = parts[2].split("\\^\\^")[0];
                }
                else {
                    for (String n : namespaces) {
                        if (object.contains(n)) {
                            object = object.replace(n, "");
                        }
                    }
                }
                object = object.replaceAll("_", " ");

                System.out.println(subject + " " + predicate + " " + object);

          
                Map<String,Object> mSubject = new HashMap<String, Object>();
                mSubject.put("name", subject);
                mSubject.put("uri", parts[0]);
                Resource rSubject = g.getOrCreateNode(mSubject, Resource.class);

                Map<String,Object> mObject = new HashMap<String, Object>();
                mObject.put("name", object);
                mObject.put("uri", parts[2]);
                Resource rObject = g.getOrCreateNode(mObject, Resource.class);

                g.createRelationship(rSubject, rObject, predicate);
                g.getBaseGraph().commit();
                
            }
        }
    }
}
