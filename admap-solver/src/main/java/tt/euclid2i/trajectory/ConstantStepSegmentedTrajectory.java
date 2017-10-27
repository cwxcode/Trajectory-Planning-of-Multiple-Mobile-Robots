package tt.euclid2i.trajectory;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.trajectory.Trajectories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public class ConstantStepSegmentedTrajectory implements SegmentedTrajectory, EvaluatedTrajectory {

	private double cost;
    private Straight[] segments;
	private int timeStep;
    

    public ConstantStepSegmentedTrajectory(List<Straight> segments, int timeStep, double cost) {
        checkNonEmpty(segments);
        checkConstantStep(segments, timeStep);
        checkContinuity(segments);

        this.segments = segments.toArray(new Straight[segments.size()]);
        this.timeStep = timeStep;
        this.cost = cost;
    }

    public static void checkConstantStep(List<Straight> segments, int step) {
    	for (Straight segment : segments) {
    		if (segment.duration() != step) {
    			throw new IllegalArgumentException(String.format("The trajectory does not have constant step (%s) has duration %d. Expected %d.", segment, segment.duration(), step));
    		}
    	}
	}

	protected static void checkContinuity(List<Straight> segments) {
        for (int i = 1; i < segments.size(); i++) {
            Straight a = segments.get(i - 1);
            Straight b = segments.get(i);
            if (!a.getEnd().equals(b.getStart()))
                throw new IllegalArgumentException(String.format("The trajectory is not continuous (%s -!-> %s)", a, b));
        }
    }

    private void checkNonEmpty(List<Straight> segments) {
        if (segments.isEmpty())
            throw new RuntimeException("Trajectory can not be created from empty list");
    }

    @Override
    public Point get(int t) {
        if (t < 0 || t > segments.length * timeStep) {
            return null;
        } 
        
        int segmentIndex = (t / timeStep);
        Straight segment = segments[segmentIndex];
        return segment.interpolate(t).getPosition();
    }


    @Override
    public List<Straight> getSegments() {
        return Arrays.asList(segments);
    }

    @Override
    public int getMinTime() {
        return 0;
    }

    @Override
    public int getMaxTime() {
        return segments.length * timeStep;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((segments == null) ? 0 : segments.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConstantStepSegmentedTrajectory other = (ConstantStepSegmentedTrajectory) obj;
        if (segments == null) {
            if (other.segments != null)
                return false;
        } else if (!segments.equals(other.segments))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < segments.length; i++) {
        	sb.append(segments[i].getStart() + " ");
		}
        
        if (segments.length > 0) {
        	sb.append(segments[segments.length-1].getEnd());
        }
        
        return sb.toString();
    }


}
