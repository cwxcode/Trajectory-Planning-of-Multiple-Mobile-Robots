package cz.agents.alite.planner.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Path planner using the A* algorithm
 */
public class Planner {

    private Planner() {
    }

    /**
     * Finds path between two points using A* algorithm.
     *
     * @param problem specification of the planning problem
     * @return last operator in the resulting sequence (found path)
     */
    public static Operator findPath(Problem specification) {
        //specification.check();
        return findPathProcess(specification);
    }

    private static Operator findPathProcess(Problem problem) {
        Operator neighbors[];
        HashMap<Operator, Operator> nodeCache = new HashMap<Operator, Operator>();
        PriorityQueue<Operator> open = new PriorityQueue<Operator>();
        HashSet<Operator> close = new HashSet<Operator>();
        Operator current, operatorCached;

        current = problem.getStartingOperator();
        open.add(current);

        while(!open.isEmpty()) {
            current = open.poll();
            close.add(current);

            if (current.isGoal()) {
                break;
            }

            // prepare all neighbours for expansion
            neighbors = current.getNeighbors();

            for (Operator neighbor : neighbors) {
                if (neighbor == null) {
                    // all neighbors processed
                    break;
                }
                if (close.contains(neighbor)) {
                    // skip node
                    continue;
                }

                if ((operatorCached = nodeCache.get(neighbor)) != null) {
                    // check if current is better than old
                    if (neighbor.compareTo(operatorCached) <= 0) {
                        // skip
                        continue;
                    }
                    // replace it
                    open.remove(neighbor);
                }

                open.add(neighbor);
                nodeCache.put(neighbor, neighbor);
            }
        }

        if (!current.isGoal()) {
            throw new RuntimeException("PathNotFoundException");
        }

        return current;
    }
}
