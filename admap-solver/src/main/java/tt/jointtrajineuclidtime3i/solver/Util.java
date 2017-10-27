package tt.jointtrajineuclidtime3i.solver;

import java.util.LinkedList;
import java.util.List;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Trajectory;

public class Util {
    /**
     * Return trajectories from all groups in the list, ignoring the ones given in excludeGroup arguments
     */
//    static List<Trajectory> generateSoftConstrainingTrajectories(List<Group> groups, Group... excludeGroup) {
//        List<Trajectory> trajectories = new LinkedList<Trajectory>();
//        Set<Group> exclude = new HashSet<Group>(Arrays.asList(excludeGroup));
//
//        for(Group group : groups) {
//            if (!exclude.contains(group)) {
//                trajectories.addAll(toList(group.getTrajectories()));
//            }
//        }
//        return trajectories;
//    }


    /**
     * Adds (and overwrites) all non-null elements from array b to array a.
     */
    public static EvaluatedTrajectory[] add(EvaluatedTrajectory[] a, EvaluatedTrajectory[] b){
        assert(a.length == b.length);
        for (int i = 0; i < a.length; i++) {
            if (b[i] != null) {
                a[i] = b[i];
            }
        }
        return a;
    }

    public static List<Trajectory> toList(EvaluatedTrajectory[] trajectories) {
        List<Trajectory> list = new LinkedList<Trajectory>();
        for (int i=0; i < trajectories.length; i++) {
            if (trajectories[i] != null) {
                list.add(trajectories[i]);
            }
        }
        return list;
    }

    public static double evaluateCost(EvaluatedTrajectory[] trajectories) {
        double cost = 0.0;
        for (EvaluatedTrajectory trajectory : trajectories) {
            if (trajectory != null) {
                cost += trajectory.getCost();
            }
        }
        return cost;
    }

    public static Trajectory[] toTrajectoryArray(
            EvaluatedTrajectory[] trajectories) {
        Trajectory[] result = new Trajectory[trajectories.length];
        System.arraycopy(trajectories, 0, result, 0, trajectories.length);
        return result;
    }
}
