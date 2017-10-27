package cz.agents.alite.planner.spatialmaneuver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import cz.agents.alite.planner.spatialmaneuver.maneuver.Maneuver;
import cz.agents.alite.planner.spatialmaneuver.maneuver.StartManeuver;
import cz.agents.alite.planner.spatialmaneuver.maneuver.ToEndManeuver;


/**
 * Path finder by maneuvers - using maneuver A* algorithm
 *
 * @author Antonin Komenda
 */
public class PathFinderByManeuvers {

    private PathFinderByManeuvers() {
    }

    /**
     * Finds path between two points using maneuver A* algorithm.
     *
     * @param specification specification of path find problem
     * 				(from/to points, plane parameters, ...)
     * @return last maneuver in found maneuver sequence (found path)
     * @throws ATCException throws {@link PathNotFoundException} when path cannot be found
     */
    public static Maneuver findPath(PathFindSpecification specification) {
        specification.check();
        return findPathProcess(specification);
    }

    private static Maneuver findPathProcess(PathFindSpecification specification) {
        Maneuver neighbors[];
        HashMap<Maneuver, Maneuver> nodeCache = new HashMap<Maneuver, Maneuver>();
        PriorityQueue<Maneuver> open = new PriorityQueue<Maneuver>();
        HashSet<Maneuver> close = new HashSet<Maneuver>();
        Maneuver current, maneuver;

        //DebugDrawListener.addLine(specification.getFrom(), specification.getTo(), Color.BLUE);

        current = new StartManeuver(specification);
        open.add(current);

        int i = 0;
        while(!open.isEmpty()) {
            current = open.poll();
            close.add(current);

            if (current.isEnding()) {
                break;
            }

            if (i++ % 100 == 0) {
//                System.out.println("OPEN:" + open.size());
//                System.out.println("CLOSED:" + close.size());

//                DebugDrawListener.setPointsFromManeuvers(close);
//                DebugDrawListener.redraw();
            }

//            if (i > 300) {
//                throw new RuntimeException("PathNotFoundException");
//            }

            // prepare all neighbours for expansion
            neighbors = current.generateNeighbours();

            for (Maneuver m : neighbors) {
                if (m == null) {
                    // all neighbors processed
                    break;
                }
                if (close.contains(m)) {
                    // skip node
                    continue;
                }

                m.computeGh();

                if ((maneuver = nodeCache.get(m)) != null) {
                    // check if current is better than old
                    if (m.compareTo(maneuver) <= 0) {
                        // skip
                        continue;
                    }
                    // replace it
                    open.remove(m);
                }

                open.add(m);
                nodeCache.put(m, m);
            }
        }

//        DebugDrawListener.clear();

        if (!(current instanceof StartManeuver) && !(current instanceof ToEndManeuver)) {
            throw new RuntimeException("PathNotFoundException");
        }

        return current;
    }
}
