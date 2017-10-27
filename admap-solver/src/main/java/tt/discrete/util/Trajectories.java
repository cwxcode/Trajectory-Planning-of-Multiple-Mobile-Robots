package tt.discrete.util;

import tt.discrete.EvaluatedTrajectory;

public class Trajectories {
    public static <S> EvaluatedTrajectory<S> concatenate(final EvaluatedTrajectory<S> traj1, final EvaluatedTrajectory<S> traj2) {

        if (traj1.getMaxTime() != traj2.getMinTime()) {
            throw new RuntimeException("The trajectories are not aligned.");
        }

        EvaluatedTrajectory<S> result = new EvaluatedTrajectory<S>() {

            @Override
            public int getMinTime() {
                return traj1.getMinTime();
            }

            @Override
            public int getMaxTime() {
                return traj2.getMaxTime();
            }

            @Override
            public S get(int t) {
                if (t >= traj1.getMinTime() && t <= traj1.getMaxTime()) {
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

        };

        return result;
    }

    public static <S> EvaluatedTrajectory<S> createSinglePointTrajectory(final S point, final int time, final double cost) {
        return new EvaluatedTrajectory<S>() {

            @Override
            public int getMinTime() {
                return time;
            }

            @Override
            public int getMaxTime() {
                return time;
            }

            @Override
            public S get(int t) {
                return point;
            }

            @Override
            public double getCost() {
                return cost;
            }

        };
    }

}
