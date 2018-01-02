import java.util.HashMap;

/**
 * Created by jessicahsiao on 4/16/17.
 */
public class Node implements Comparable<Node> {

    public Node NW, NE, SW, SE;
    public double lrlon, ullon, ullat, lrlat;
    public double lonDPP;
    public String tileName;
    public int depth;

    // root node constructor
    public Node() {
        this.lrlon = MapServer.ROOT_LRLON;
        this.ullon = MapServer.ROOT_ULLON;
        this.ullat = MapServer.ROOT_ULLAT;
        this.lrlat = MapServer.ROOT_LRLAT;
        this.lonDPP = Math.abs((this.ullon - this.lrlon)) / 256;
        this.tileName = "root";
        this.depth = 0;

        this.getChildren();
    }
//root1
    // children node constructor
    public Node(String digit, Node parent) {
        String combined = parent.tileName + digit;
        if (parent.depth == 0) {
            this.tileName = combined.substring(4);
        } else {
            this.tileName = combined;
        }

        if (digit.equals("1")) {
            this.ullon = parent.ullon;
            this.ullat = parent.ullat;
            this.lrlon = (parent.ullon + parent.lrlon) / 2;
            this.lrlat = (parent.ullat + parent.lrlat) / 2;
        } else if (digit.equals("2")) {
            this.ullon = (parent.ullon + parent.lrlon) / 2;
            this.ullat = parent.ullat;
            this.lrlon = parent.lrlon;
            this.lrlat = (parent.ullat + parent.lrlat) / 2;
        } else if (digit.equals("3")) {
            this.ullon = parent.ullon;
            this.ullat = (parent.ullat + parent.lrlat) / 2;
            this.lrlon = (parent.ullon + parent.lrlon) / 2;
            this.lrlat = parent.lrlat;
        } else {
            this.ullon = (parent.ullon + parent.lrlon) / 2;
            this.ullat = (parent.ullat + parent.lrlat) / 2;
            this.lrlon = parent.lrlon;
            this.lrlat = parent.lrlat;
        }

        this.lonDPP = Math.abs((this.ullon - this.lrlon)) / 256;
        this.depth = this.tileName.length();

        this.getChildren();
    }

    public void getChildren() {
        if (this.depth >= 7) {
            return;
        }
        this.NW = new Node("1", this);
        this.NE = new Node("2", this);
        this.SW = new Node("3", this);
        this.SE = new Node("4", this);
    }


    /* reference: http://stackoverflow.com/questions/
     306316/determine-if-two-rectangles-overlap-each- */
    public boolean intersectsTile(double query_ullon, double query_lrlon,
                                  double query_ullat, double query_lrlat) {
        return !(this.ullon > query_lrlon || this.lrlon < query_ullon ||
                this.ullat < query_lrlat || this.lrlat > query_ullat);
    }

    public boolean lonDPPSmallerThanOrIsLeaf(double query_lonDPP) {
        if (this.depth == 7) {
            return true;
        } else if (this.lonDPP < query_lonDPP) {
            return true;
        }
        return false;
    }

    public int compareTo(Node query) {

        if (this.ullat > query.ullat) {
            return -1;
        } else if (this.ullat == query.ullat) {
            if (this.ullon < query.ullon) {
                return -1;
            } else if (this.ullon == query.ullon) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

//    public static void main(String[] args) {
//        Node a = new Node();
//        Node b = new Node("1", a);
//        Node c = new Node("2", a);
//        Node d = new Node("3", a);
//        Node e = new Node ("4", a);
//        Node f = new Node("1", b);
//        System.out.println(a.tileName);
//        System.out.println(b.tileName);
//        //System.out.println(c.tileName);
//        System.out.println(f.tileName);
//        System.out.println(b.ullon);
//        //System.out.println(b.)
//        //System.out.println(b.tileName.substring(4));
////        System.out.println(b.compareTo(c)); //1 < 2
////        System.out.println(c.compareTo(d)); //2 < 3
////        System.out.println(d.compareTo(e)); //3 < 4
////        System.out.println(a.compareTo(b)); //root == 1
////       // System.out.println(a.compareTo(e));


}


