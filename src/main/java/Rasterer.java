import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    Node root;
    String[][] renderGrid;
    double rasterUlLon;
    double rasterUlLat;
    double rasterLrLon;
    double rasterLrLat;
    int depth;
    boolean querySuccess;

    /**
     * imgRoot is the name of the directory containing the images.
     * You may not actually need this for your class.
     */
    public Rasterer(String imgRoot) {
        root = new Node();

    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
<<<<<<< HEAD
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>Has dimensions of at least w by h, where w and h are the user viewport width
     * and height.</li>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
=======
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
>>>>>>> 1c0df392fa29a10a01640a01900abcf4fb511fb7
     * </p>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified:
     * "renderGrid"   -> String[][], the files to display
     * "rasterUlLon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "rasterUlLat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "rasterLrLon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "rasterLrLat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image
     * string. <br>
     * "querySuccess" -> Boolean, whether the query was able to successfully complete. Don't
     * forget to set this to true! <br>
     * @see //REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results = new HashMap<>();

        PriorityQueue<Node> tiles = new PriorityQueue<>();
        double lrlon = params.get("lrlon");
        double ullon = params.get("ullon");
        double ullat = params.get("ullat");
        double lrlat = params.get("lrlat");
        double width = params.get("w");
        double queryLonDPP = Math.abs(ullon - lrlon) / width;

        createGrid(root, queryLonDPP, ullon, lrlon, ullat, lrlat, tiles);

        Node t = tiles.peek();
        PriorityQueue<Node> orderedTiles = new PriorityQueue<>();
        int x = 0;
        int y = 0;
        while (!tiles.isEmpty()) {
            Node tile = tiles.remove();
            if (tile.ullat == t.ullat) {
                y += 1;
            }
            if (tile.ullon == t.ullon) {
                x += 1;
            }
            orderedTiles.add(tile);
        }

        renderGrid = new String[x][y];
        rasterUlLon = orderedTiles.peek().ullon;
        rasterUlLat = orderedTiles.peek().ullat;
        querySuccess = true;

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Node img = orderedTiles.remove();
                renderGrid[i][j] = "img/" + img.tileName + ".png";
                //System.out.println(renderGrid[i][j]);
                if (orderedTiles.size() == 0) {
                    rasterLrLon = img.lrlon;
                    rasterLrLat = img.lrlat;
                    depth = img.depth;
                }
            }
        }

        results.put("raster_ul_lon", rasterUlLon);
        results.put("depth", depth);
        results.put("raster_lr_lon", rasterLrLon);
        results.put("raster_lr_lat", rasterLrLat);
        results.put("render_grid", renderGrid);
        results.put("raster_ul_lat", rasterUlLat);
        results.put("query_success", querySuccess);

        return results;
    }


    public void createGrid(Node tile, double queryLonDPP, double ullon, double lrlon,
                           double ullat, double lrlat, PriorityQueue<Node> tiles) {

        if (tile.intersectsTile(ullon, lrlon, ullat, lrlat)) {
            if (tile.lonDPPSmallerThanOrIsLeaf(queryLonDPP)) {
                tiles.add(tile);
            } else {
                createGrid(tile.NW, queryLonDPP, ullon, lrlon, ullat, lrlat, tiles);
                createGrid(tile.NE, queryLonDPP, ullon, lrlon, ullat, lrlat, tiles);
                createGrid(tile.SW, queryLonDPP, ullon, lrlon, ullat, lrlat, tiles);
                createGrid(tile.SE, queryLonDPP, ullon, lrlon, ullat, lrlat, tiles);
            }
        }
    }
}
