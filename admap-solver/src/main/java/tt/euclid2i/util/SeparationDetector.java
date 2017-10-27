package tt.euclid2i.util;

import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Geometry3i;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.trajectory.Trajectories;

import java.util.*;

public class SeparationDetector {

    // ----------------------------------------- ANALYTIC COLLISION CHECKER -------------------------------------------

    public static boolean hasAnyPairwiseConflictAnalytic(SegmentedTrajectory thisTrajectory, SegmentedTrajectory[] otherTrajectories, int separation) {
        int[] separations = new int[otherTrajectories.length];
        Arrays.fill(separations, separation);
        return hasAnyPairwiseConflictAnalytic(thisTrajectory, otherTrajectories, separations);
    }

    public static boolean hasConflictAnalytic(SegmentedTrajectory thisTrajectory, SegmentedTrajectory otherTrajectory, int separation) {
        return hasAnyPairwiseConflictAnalytic(thisTrajectory, new SegmentedTrajectory[]{otherTrajectory}, new int[]{separation});
    }

    public static boolean hasAnyPairwiseConflictAnalytic(SegmentedTrajectory thisTrajectory, SegmentedTrajectory[] otherTrajectories, int[] separations) {
        Trajectories.checkNonEmpty(thisTrajectory);
        Iterable<Straight> segmentsA = extendListFromMinToMaxTime(thisTrajectory);

        for (int i = 0; i < otherTrajectories.length; i++) {
            SegmentedTrajectory otherTrajectory = otherTrajectories[i];
            Trajectories.checkNonEmpty(otherTrajectory);

            if (!Trajectories.overlapInTime(thisTrajectory, otherTrajectory))
                continue;

            Iterable<Straight> segmentsB = extendListFromMinToMaxTime(otherTrajectory);
            if (hasConflictAnalytic(segmentsA, segmentsB, separations[i]))
                return true;
        }

        return false;
    }

    private static boolean hasConflictAnalytic(Iterable<Straight> segmentsA, Iterable<Straight> segmentsB, int separation) {
        Iterator<Straight> iteratorA = segmentsA.iterator();
        Iterator<Straight> iteratorB = segmentsB.iterator();

        return hasCollision(iteratorA, iteratorB, separation);
    }

    private static Collection<Straight> extendListFromMinToMaxTime(SegmentedTrajectory traj) {
        LinkedList<Straight> segments = new LinkedList<Straight>(traj.getSegments());

        tt.euclidtime3i.Point startPoint3i = Trajectories.start(segments);
        tt.euclidtime3i.Point endPoint3i = Trajectories.end(segments);

        if (traj.getMinTime() < startPoint3i.getTime()) {
            tt.euclidtime3i.Point minTimeStart = new tt.euclidtime3i.Point(startPoint3i.getPosition(), traj.getMinTime());
            segments.addFirst(new Straight(minTimeStart, startPoint3i));
        } else if (traj.getMinTime() > startPoint3i.getTime()) {
            throw new IllegalArgumentException("Trajectory starts before its minTime");
        }

        if (traj.getMaxTime() > endPoint3i.getTime()) {
            tt.euclidtime3i.Point maxTimeEnd = new tt.euclidtime3i.Point(endPoint3i.getPosition(), traj.getMaxTime());
            segments.addLast(new Straight(endPoint3i, maxTimeEnd));
        } else if (traj.getMaxTime() < endPoint3i.getTime()) {
        	System.out.println(traj.getClass().getName() + traj);
            throw new IllegalArgumentException("Trajectory ends before its maxTime");
        }

        return segments;
    }

    private static boolean hasCollision(Iterator<Straight> iteratorA, Iterator<Straight> iteratorB, int separation) {
        Straight a = null, b = null;

        do {
            if (a == null && b == null) {
                a = iteratorA.next();
                b = iteratorB.next();
            } else if (endsAtSameTime(a, b)) {
            	if (iteratorA.hasNext() && iteratorB.hasNext()) {
            		 a = iteratorA.next();
                     b = iteratorB.next();
            	} else {
            		return false;
            	}
            } else if (endsEarlier(a, b)) {
                if (iteratorA.hasNext()) a = iteratorA.next();
                else break;

            } else {
                if (iteratorB.hasNext()) b = iteratorB.next();
                else break;
            }

            if (haveTimeOverlap(a, b) && collide(a, b, separation))
                return true;
        }
        while (iteratorA.hasNext() || iteratorB.hasNext());

        return false;
    }


    private static boolean haveTimeOverlap(Straight a, Straight b) {
        int intersectionStart = Math.max(a.getStart().getTime(), b.getStart().getTime());
        int intersectionEnd = Math.min(a.getEnd().getTime(), b.getEnd().getTime());

        return intersectionStart <= intersectionEnd;
    }

    private static boolean endsAtSameTime(Straight a, Straight b) {
        return a.getEnd().getTime() == b.getEnd().getTime();
    }

    private static boolean collide(Straight a, Straight b, int separation) {
        return Geometry3i.distance(a, b) < separation;
    }

    private static boolean endsEarlier(Straight a, Straight b) {
        return a.getEnd().getTime() < b.getEnd().getTime();
    }

    // ----------------------------------------- ANALYTIC COLLISION CHECKER -------------------------------------------

    public static boolean hasAnyPairwiseConflict(Trajectory thisTrajectory, Trajectory[] otherTrajectories, int separation, int samplingInterval) {
        int[] separations = new int[otherTrajectories.length];
        Arrays.fill(separations, separation);
        return hasAnyPairwiseConflict(thisTrajectory, otherTrajectories, separations, samplingInterval);
    }

    public static boolean hasAnyPairwiseConflict(Trajectory thisTrajectory, Trajectory[] otherTrajectories, int[] separations, int samplingInterval) {

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory
                .getMaxTime(); t += samplingInterval) {
            Point thisTrajectoryPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.length; j++) {

                if (otherTrajectories[j] != null) {
                    if (t >= otherTrajectories[j].getMinTime()
                            && t <= otherTrajectories[j].getMaxTime()) {
                        Point otherTrajectoryPos = otherTrajectories[j]
                                .get(t);
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) < separations[j]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasAnyPairwiseConflict(Trajectory[] trajectories, int separations[], int samplingInterval) {

        int minTime = Integer.MAX_VALUE;
        for (int i = 0; i < trajectories.length; i++) {
            if (trajectories[i].getMinTime() < minTime) {
                minTime = trajectories[i].getMinTime();
            }
        }

        int maxTime = Integer.MIN_VALUE;
        for (int i = 0; i < trajectories.length; i++) {
            if (trajectories[i].getMaxTime() > maxTime) {
                maxTime = trajectories[i].getMaxTime();
            }
        }

        // iterate over all time points
        for (int t = minTime; t <= maxTime; t += samplingInterval) {
            // check all pairs of agents for conflicts at timepoint t
            for (int j = 0; j < trajectories.length; j++) {
                for (int k = j + 1; k < trajectories.length; k++) {
                    // check the distance between j and k
                    Trajectory a = trajectories[j];
                    Trajectory b = trajectories[k];

                    if (t >= a.getMinTime() && t <= a.getMaxTime() &&
                            t >= b.getMinTime() && t <= b.getMaxTime()) {
                        if (a.get(t).distance(b.get(t)) < separations[j] + separations[k]) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    public static boolean hasAnyPairwiseConflict(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval) {

        List<Trajectory> trajectories = new ArrayList<Trajectory>(trajectoriesCollection);

        int minTime = Integer.MAX_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMinTime() < minTime) {
                minTime = trajectory.getMinTime();
            }
        }

        int maxTime = Integer.MIN_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMaxTime() > maxTime) {
                maxTime = trajectory.getMaxTime();
            }
        }

        // iterate over all time points
        for (int t = minTime; t <= maxTime; t += samplingInterval) {
            // check all pairs of agents for conflicts at timepoint t
            for (int j = 0; j < trajectories.size(); j++) {
                for (int k = j + 1; k < trajectories.size(); k++) {
                    // check the distance between j and k
                    Trajectory a = trajectories.get(j);
                    Trajectory b = trajectories.get(k);

                    if (t >= a.getMinTime() && t <= a.getMaxTime() &&
                            t >= b.getMinTime() && t <= b.getMaxTime()) {
                        if (a.get(t).distance(b.get(t)) < separation) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    /**
     * Computes pairwise conflicts between thisTrajectory and otherTrajectories.
     */
    public static List<tt.euclidtime3i.Point> computePairwiseConflicts(Trajectory thisTrajectory,
                                                                       Collection<Trajectory> otherTrajectoriesCollection,
                                                                       int separation, int samplingInterval) {


        List<Trajectory> otherTrajectories = new ArrayList<Trajectory>(otherTrajectoriesCollection);
        List<tt.euclidtime3i.Point> conflicts = new LinkedList<tt.euclidtime3i.Point>();

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
            Point thisTrajectoryPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.size(); j++) {

                if (otherTrajectories.get(j) != null) {
                    if (t >= otherTrajectories.get(j).getMinTime() && t <= otherTrajectories.get(j).getMaxTime()) {
                        Point otherTrajectoryPos = otherTrajectories.get(j).get(t);
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) < separation) {
                            conflicts.add(new tt.euclidtime3i.Point(thisTrajectoryPos, t));
                        }
                    }
                }
            }
        }
        return conflicts;
    }


    /**
     * Computes all pairwise conflicts between all pairs of trajectories from trajectoriesCollection.
     */
    public static List<tt.euclidtime3i.Point> computeAllPairwiseConflicts(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval) {

        List<Trajectory> trajectories = new ArrayList<Trajectory>(trajectoriesCollection);
        List<tt.euclidtime3i.Point> conflicts = new LinkedList<tt.euclidtime3i.Point>();

        int minTime = Integer.MAX_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMinTime() < minTime) {
                minTime = trajectory.getMinTime();
            }
        }

        int maxTime = Integer.MIN_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMaxTime() > maxTime) {
                maxTime = trajectory.getMaxTime();
            }
        }

        // iterate over all time points
        for (int t = minTime; t <= maxTime; t += samplingInterval) {
            // check all pairs of agents for conflicts at timepoint t
            for (int j = 0; j < trajectories.size(); j++) {
                for (int k = j + 1; k < trajectories.size(); k++) {
                    // check the distance between j and k
                    Trajectory a = trajectories.get(j);
                    Trajectory b = trajectories.get(k);

                    if (t >= a.getMinTime() && t <= a.getMaxTime() &&
                            t >= b.getMinTime() && t <= b.getMaxTime()) {
                        if (a.get(t).distance(b.get(t)) < separation) {
                            conflicts.add(new tt.euclidtime3i.Point(a.get(t), t));
                            conflicts.add(new tt.euclidtime3i.Point(b.get(t), t));
                        }
                    }

                }
            }
        }
        return conflicts;
    }
    
    /**
     * finds the first conflicts and returns ids of the two agents involved in the conflict
     *
     * @return array containing ids of agents involved in the conflict, null if no conflicts are found
     */
    public static int[] findFirstConflict(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval) {

        List<Trajectory> trajectories = new ArrayList<Trajectory>(trajectoriesCollection);

        int minTime = Integer.MAX_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMinTime() < minTime) {
                minTime = trajectory.getMinTime();
            }
        }

        int maxTime = Integer.MIN_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMaxTime() > maxTime) {
                maxTime = trajectory.getMaxTime();
            }
        }

        // iterate over all time points
        for (int t = minTime; t <= maxTime; t += samplingInterval) {
            // check all pairs of agents for conflicts at timepoint t
            for (int j = 0; j < trajectories.size(); j++) {
                for (int k = j + 1; k < trajectories.size(); k++) {
                    // check the distance between j and k
                    Trajectory a = trajectories.get(j);
                    Trajectory b = trajectories.get(k);

                    if (t >= a.getMinTime() && t <= a.getMaxTime() &&
                            t >= b.getMinTime() && t <= b.getMaxTime()) {
                        if (a.get(t).distance(b.get(t)) < separation) {
                            return new int[]{j, k};
                        }
                    }

                }
            }
        }

        return null;
    }

    public static boolean hasConflict(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval) {
        return hasAnyPairwiseConflict(trajectoriesCollection, separation, samplingInterval);
    }

    public static boolean hasConflict(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval, int maxSpeed) {
        if (mayHaveConflict(trajectoriesCollection, separation, maxSpeed)) {
            return hasAnyPairwiseConflict(trajectoriesCollection, separation, samplingInterval);
        } else {
            return false;
        }

    }

    /**
     * Quickly determines if the trajectories are close enough for a conflict to occur.
     */
    public static boolean mayHaveConflict(Collection<Trajectory> trajectoriesCollection, int separation, int maxSpeed) {
        if (trajectoriesCollection.isEmpty()) return false;

        List<Trajectory> trajectories = new ArrayList<Trajectory>(trajectoriesCollection);

        Trajectory t = trajectories.get(0);

        int criticalDist = (t.getMaxTime() - t.getMinTime()) * maxSpeed + separation;

        for (int j = 0; j < trajectories.size(); j++) {
            for (int k = j + 1; k < trajectories.size(); k++) {
                // check the distance between j and k
                Trajectory a = trajectories.get(j);
                Trajectory b = trajectories.get(k);

                if (a.get(a.getMinTime()).distance(b.get(b.getMinTime())) < criticalDist) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if thisTrajectory has conflict with any of the trajectories from otherTrajectoriesCollection.
     */
    public static boolean hasConflict(Trajectory thisTrajectory, Collection<Trajectory> otherTrajectoriesCollection, int separation, int samplingInterval) {

        assert (thisTrajectory != null);
        assert (!otherTrajectoriesCollection.contains(null));

        List<Trajectory> otherTrajectories = new ArrayList<Trajectory>(otherTrajectoriesCollection);

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
            Point thisTrajectoryPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.size(); j++) {

                if (otherTrajectories.get(j) != null) {
                    if (t >= otherTrajectories.get(j).getMinTime() && t <= otherTrajectories.get(j).getMaxTime()) {
                        Point otherTrajectoryPos = otherTrajectories.get(j).get(t);
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) < separation) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
    
    /**
     * Determines if thisTrajectory has conflict with the other trajectory. Ignores conflicts that occur at the protected point. 
     */
    public static boolean hasConflictIgnoreProtectedPoint(Trajectory thisTrajectory, Trajectory otherTrajectory, tt.euclid2i.Point protectedPoint, int separation, int samplingInterval) {

        assert (thisTrajectory != null);
        assert (otherTrajectory != null);

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
            Point thisTrajectoryPos = thisTrajectory.get(t);
            
            if (!thisTrajectory.get(t).equals(protectedPoint)) {            
	            if (t >= otherTrajectory.getMinTime() && t <= otherTrajectory.getMaxTime()) {
	                Point otherTrajectoryPos = otherTrajectory.get(t);
	                if (thisTrajectoryPos.distance(otherTrajectoryPos) < separation) {
	                    return true;
	                }
	            }
            }
        }

        return false;
    }

    /**
     * Determines if thisTrajectory has conflict with any of the trajectories from otherTrajectoriesCollection.
     */
    public static boolean hasConflict(Trajectory thisTrajectory, Collection<Trajectory> otherTrajectoriesCollection, int separation, int samplingInterval, int maxSpeed) {

        assert (thisTrajectory != null);
        assert (!otherTrajectoriesCollection.contains(null));

        List<Trajectory> otherTrajectories = new ArrayList<Trajectory>(otherTrajectoriesCollection);

        int criticalDist = (thisTrajectory.getMaxTime() - thisTrajectory.getMinTime()) * maxSpeed + separation;

        boolean approximateConflict = false;

        for (int j = 0; j < otherTrajectories.size(); j++) {
            Trajectory b = otherTrajectories.get(j);
            if (b != null) {
                if (thisTrajectory.get(thisTrajectory.getMinTime()).distance(b.get(b.getMinTime())) < criticalDist) {
                    approximateConflict = true;
                }
            }
        }

        if (approximateConflict) {
            for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
                Point thisTrajectoryPos = thisTrajectory.get(t);
                for (int j = 0; j < otherTrajectories.size(); j++) {

                    if (otherTrajectories.get(j) != null) {
                        if (t >= otherTrajectories.get(j).getMinTime() && t <= otherTrajectories.get(j).getMaxTime()) {
                            Point otherTrajectoryPos = otherTrajectories.get(j).get(t);
                            if (thisTrajectoryPos.distance(otherTrajectoryPos) < separation) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Counts the number of trajectories from the given collection that have a conflict with the given trajectory
     */
    public static int countConflictingTrajectories(Trajectory thisTrajectory, Collection<Trajectory> otherTrajectoriesCollection, int separation, int samplingInterval) {

        int count = 0;

        assert (thisTrajectory != null);
        assert (!otherTrajectoriesCollection.contains(null));

        List<Trajectory> otherTrajectories = new ArrayList<Trajectory>(otherTrajectoriesCollection);

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
            Point thisTrajectoryPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.size(); j++) {

                if (otherTrajectories.get(j) != null) {
                    if (t >= otherTrajectories.get(j).getMinTime() && t <= otherTrajectories.get(j).getMaxTime()) {
                        Point otherTrajectoryPos = otherTrajectories.get(j).get(t);
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) < separation) {
                            count++;
                            continue;
                        }
                    }
                }
            }
        }

        return count;
    }
}
