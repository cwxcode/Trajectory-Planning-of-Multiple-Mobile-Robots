package tt.euclidtime3i.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tt.euclid2i.Trajectory;
import tt.euclid2i.region.Circle;
import tt.euclid2i.util.SeparationDetector;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.region.MovingCircle;
import tt.util.NotImplementedException;

public class IntersectionChecker {

    public static boolean intersect(Region thisRegion, Collection<Region> obstacleCollection) {
        assert(thisRegion != null);
        assert(!obstacleCollection.contains(null));

        Region[] obstacles = obstacleCollection.toArray(new Region[1]);

        for (int j = 0; j < obstacles.length; j++) {
            if (obstacles[j] != null) {
            	if (/*thisRegion.getBoundingBox().intersects(obstacles[j].getBoundingBox())*/ true) {
            		if (intersect(thisRegion, obstacles[j])) {
            			return true;
            		}
            	}
            }
        }

        return false;
    }

	public static boolean intersect(Region thisRegion, Region otherRegion) {
		if (thisRegion instanceof MovingCircle && otherRegion instanceof MovingCircle) {
			MovingCircle thisMc = (MovingCircle) thisRegion;
			MovingCircle otherMc = (MovingCircle) otherRegion;

			return SeparationDetector.hasConflict(
					thisMc.getTrajectory(),
					Collections.singleton(otherMc.getTrajectory()),
					thisMc.getRadius() + otherMc.getRadius(),
					Math.min(thisMc.getRadius(), otherMc.getRadius())/4);
		}

		throw new NotImplementedException("The conflict checking of " + thisRegion.getClass() + " vs. " + otherRegion.getClass() + " not implemented yet");
	}
	
    /**
     * Computes all pairwise conflicts between all pairs of trajectories from trajectoriesCollection.
     */
    public static List<Circle> computeAllPairwiseConflicts(Collection<MovingCircle> mcCollections, int samplingInterval) {

        List<MovingCircle> movingCircles = new ArrayList<MovingCircle>(mcCollections);
        List<Circle> conflicts = new LinkedList<Circle>();

        int minTime = Integer.MAX_VALUE;
        for (MovingCircle mc : movingCircles) {
            if (mc.getTrajectory().getMinTime() < minTime) {
                minTime = mc.getTrajectory().getMinTime();
            }
        }

        int maxTime = Integer.MIN_VALUE;
        for (MovingCircle mc : movingCircles) {
            if (mc.getTrajectory().getMaxTime() > maxTime) {
                maxTime = mc.getTrajectory().getMaxTime();
            }
        }

        // iterate over all time points
        for (int t = minTime; t <= maxTime; t += samplingInterval) {
            // check all pairs of agents for conflicts at timepoint t
            for (int j = 0; j < movingCircles.size(); j++) {
                for (int k = j + 1; k < movingCircles.size(); k++) {
                    // check the distance between j and k
                    MovingCircle a = movingCircles.get(j);
                    MovingCircle b = movingCircles.get(k);

                    if (t >= a.getTrajectory().getMinTime() && t <= a.getTrajectory().getMaxTime() &&
                            t >= b.getTrajectory().getMinTime() && t <= b.getTrajectory().getMaxTime()) {
                        if (a.getTrajectory().get(t).distance(b.getTrajectory().get(t)) < a.getRadius() + b.getRadius()) {
                            conflicts.add(new Circle(a.getTrajectory().get(t), a.getRadius()));
                            conflicts.add(new Circle(b.getTrajectory().get(t),b.getRadius()));
                        }
                    }

                }
            }
        }
        return conflicts;
    }

}
