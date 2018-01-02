import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    HashMap<Long, Point> vertices = new HashMap<>();

    public class Point {
        long id;
        double lat;
        double lon;

        Point(long id, double lat, double lon) {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
        }
        HashMap<Long, Point> neighbors = new HashMap<>();
    }

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */

    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        // your code here

        for (Point p : new ArrayList<>(vertices.values())) {
            if (p.neighbors.isEmpty()) {
                vertices.remove(p.id);
            }
        }

    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        return vertices.keySet();
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        return vertices.get(v).neighbors.keySet();
    }

//    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
//     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
//>>>>>>> 1c0df392fa29a10a01640a01900abcf4fb511fb7
    double distance(long v, long w) {
        return distance(v, vertices.get(w).lon, vertices.get(w).lat);
    }

    private double distance(long v, double lon, double lat) {
        Point vertexV = vertices.get(v);
        double lonDiffSq = Math.pow((vertexV.lon - lon), 2);
        double latDiffSq = Math.pow((vertexV.lat - lat), 2);
        return Math.sqrt(lonDiffSq + latDiffSq);
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        //Point p = new Point(0, lon, lat);
        long closestID = 0;
        double dist = Double.MAX_VALUE;
        double closest = dist;

        for (Long id : vertices.keySet()) {
            dist = distance(id, lon, lat);
            if (dist < closest) {
                closest = dist;
                closestID = id;
            }
        }
        return closestID;
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        Point p = vertices.get(v);
        return p.lon;
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        Point p = vertices.get(v);
        return p.lat;
    }

    void addPoint(long id, double lat, double lon) {
        vertices.put(id, new Point(id, lat, lon));
    }

    //creates edge between two vertices
    void addEdge(long v, long w) {
        Point a = vertices.get(v);
        Point b = vertices.get(w);
        a.neighbors.put(w, b);
        b.neighbors.put(v, a);
    }

}
