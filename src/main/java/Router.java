import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
//priority = distances of all edges + euclidean distance
    //        f(n) = g(n) + h(n)


public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest, 
     * where the longs are node IDs.
     */
    private static class PointPriority implements Comparator {
        HashMap<Long, Double> distFromStart;
        HashMap<Long, Double> distToGoal;

        PointPriority(HashMap<Long, Double> distFromStart,
                      HashMap<Long, Double> distToGoal) {
            this.distFromStart = distFromStart;
            this.distToGoal = distToGoal;
        }

        public int compare(Object p1, Object p2) {
            long a = (long) p1;
            long b = (long) p2;
            double priorityA = distFromStart.get(a) + distToGoal.get(a);
            double priorityB = distFromStart.get(b) + distToGoal.get(b);

            if (priorityA < priorityB) {
                return -1;
            } else if (priorityA == priorityB) {
                return 0;
            } else {
                return 1;
            }

        }

    }

    public static LinkedList<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                                double destlon, double destlat) {

        LinkedList<Long> shortestPath = new LinkedList<>();
        long start = g.closest(stlon, stlat);
        long goal = g.closest(destlon, destlat);

        HashMap<Long, Double> distFromStart = new HashMap<>();
        HashMap<Long, Double> distToGoal = new HashMap<>();
        HashMap<Long, Long> edgeTo = new HashMap<>();

        //HashMap<GraphDB.Point, > best = new HashMap<>();
        PointPriority p = new PointPriority(distFromStart, distToGoal);
        PriorityQueue<Long> fringe = new PriorityQueue(p);

        for (Long id : g.vertices.keySet()) {
            if (id != start) {
                distFromStart.put(id, Double.MAX_VALUE);
                distToGoal.put(id, Double.MAX_VALUE);
                fringe.add(id);
            }
        }
        distFromStart.put(start, 0.0);
        distToGoal.put(start, g.distance(start, goal));
        fringe.add(start);

        //referenced lecture pseudocode for Dijkstra's algorithm
        while (!fringe.isEmpty()) {
            Long min = fringe.poll();
           // shortestPath.add(id);
            if (min == goal) {
                break;
            }
            for (Long id : g.vertices.get(min).neighbors.keySet()) {
                if (distFromStart.get(id) > distFromStart.get(min) + g.distance(id, min)) {
                    distFromStart.put(id, distFromStart.get(min) + g.distance(id, min));
                    distToGoal.put(id, g.distance(id, goal));
                    edgeTo.put(id, min);
                    if (fringe.contains(id)) {
                        fringe.remove(id);
                        fringe.add(id);
                    }
                }
            }
        }

        long previous = goal;
        while (previous != start) {
            shortestPath.addFirst(previous);
            previous = edgeTo.get(previous);
        }
        shortestPath.addFirst(start);
        return shortestPath;
    }

}
