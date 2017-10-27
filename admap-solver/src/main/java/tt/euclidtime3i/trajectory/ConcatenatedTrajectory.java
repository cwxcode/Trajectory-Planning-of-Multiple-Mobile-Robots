package tt.euclidtime3i.trajectory;

import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.Point;

class ConcatenatedTrajectory implements EvaluatedTrajectory {

    EvaluatedTrajectory traj1;
    EvaluatedTrajectory traj2;

    public ConcatenatedTrajectory(EvaluatedTrajectory traj1,
            EvaluatedTrajectory traj2) {
        super();
        this.traj1 = traj1;
        this.traj2 = traj2;
    }

    @Override
    public int getMinTime() {
        return traj1.getMinTime();
    }

    @Override
    public int getMaxTime() {
        return traj2.getMaxTime();
    }

    @Override
    public Point get(int t) {
        if (t >= traj1.getMinTime() && t <= traj2.getMaxTime()) {
            if (t < traj1.getMaxTime()) {
                return traj1.get(t);
            } else {
                return traj2.get(t);
            }
        }
        return null;
    }

    @Override
    public double getCost() {
        return traj1.getCost() + traj2.getCost();
    }

    @Override
    public int hashCode() {
        return traj1.hashCode() ^ traj2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConcatenatedTrajectory) {
            // FIXME this ignores the case when the trajectory was generated using a different sequence of motions, in fact,
            ConcatenatedTrajectory ct = (ConcatenatedTrajectory) obj;
            return traj1.equals(ct.traj1) && traj2.equals(ct.traj2);
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return traj1 + " " + traj2;
    }


};

