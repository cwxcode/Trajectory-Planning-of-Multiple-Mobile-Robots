package tt.jointeuclid2ni.probleminstance.generator.missions;

import org.apache.commons.lang3.ArrayUtils;
import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;

public class MissionBases implements Missions {

    public static class Mission {
        public Point start, target;

        public Mission(Point start, Point target) {
            this.start = start;
            this.target = target;
        }

        @Override
        public String toString() {
            return String.format("Mission{%s, %s}", start, target);
        }
    }

    private int bodyRadius;
    private int speed;
    private int maxTime;

    private int[] radii;
	float[] speeds;
    private Point[] starts, targets;
    private SegmentedTrajectory[] trajectories;

    public MissionBases(int bodyRadius, int speed, int maxTime) {
        this.speed = speed;
        this.bodyRadius = bodyRadius;
        this.maxTime = maxTime;

        this.radii = new int[]{};
        this.speeds = new float[]{};
        this.starts = new Point[]{};
        this.targets = new Point[]{};
        this.trajectories = new SegmentedTrajectory[]{};
    }

    public void addMission(Mission mission, SegmentedTrajectory trajectory) {
        targets = ArrayUtils.add(targets, mission.target);
        starts = ArrayUtils.add(starts, mission.start);
        trajectories = ArrayUtils.add(trajectories, trajectory);
        radii = ArrayUtils.add(radii, bodyRadius);
        speeds = ArrayUtils.add(speeds, speed);
    }

    @Override
    public int nAgents() {
        return starts.length;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public float getSpeed() {
        return speed;
    }

    public int getBodyRadius() {
        return bodyRadius;
    }

    @Override
    public Point[] getStarts() {
        return starts;
    }

    @Override
    public Point[] getTargets() {
        return targets;
    }

    @Override
    public int[] getBodyRadiuses() {
        return radii;
    }

    @Override
    public float[] getSpeeds() {
        return speeds;
    }

    public SegmentedTrajectory[] getTrajectories() {
        return trajectories;
    }

}
