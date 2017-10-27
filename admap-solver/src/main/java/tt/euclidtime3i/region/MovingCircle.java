package tt.euclidtime3i.region;

import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclid2i.util.SeparationDetector;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.trajectory.LinearTrajectory;


public class MovingCircle implements Region {
	
	public static final int USE_ANALYTIC_COLLISION_CHECKING = 0;
    int samplingInterval;
    tt.euclid2i.Trajectory trajectory;
    int radius;

    private static final int DEFAULT_SAMPLES_PER_RADIUS = 4;

    public MovingCircle(tt.euclid2i.Trajectory trajectory, int radius) {
        super();
        assert(trajectory != null);
        this.trajectory = trajectory;
        this.radius = radius;
        this.samplingInterval = (int) Math.floor((double)radius/(double)DEFAULT_SAMPLES_PER_RADIUS);
    }
    
    public MovingCircle(tt.euclid2i.Trajectory trajectory, int radius, int samplingInterval) {
        super();
        this.trajectory = trajectory;
        this.radius = radius;
        this.samplingInterval = samplingInterval;
    }

    @Override
    public boolean intersectsLine(Point p1, Point p2) {
        Point start;
        Point end;

        if (p1.getTime() < p2.getTime()) {
            start = p1;
            end = p2;
        } else {
            start = p2;
            end = p1;
        }
        
        if (samplingInterval == USE_ANALYTIC_COLLISION_CHECKING) {
        	if (!(trajectory instanceof SegmentedTrajectory)) 
        		throw new RuntimeException("Analytic collision checking can be only used on SegmentedTrajectories.");
        	return intersectsLineAnalytic(start, end);
        } else {
        	return intersectsLineNumeric(start, end);
        }
    }
    
	protected boolean intersectsLineNumeric(Point start, Point end) {
		int tmin = Math.max(trajectory.getMinTime(), start.getTime());
        int tmax = Math.min(trajectory.getMaxTime(), end.getTime());

        for (int t = tmin; t <= tmax; t += samplingInterval) {
            // todo what if end - start == 0???
            double alpha = 0;
            if (end.getTime() - start.getTime() > 0) {
                alpha = (double)(t - start.getTime()) / (double)(end.getTime() - start.getTime());
            } else if (end.getTime() - start.getTime() == 0) {
                alpha = 0;
                assert(start.equals(end));
            } else {
                throw new RuntimeException();
            }

            assert (alpha >= -0.00001 && alpha <= 1.00001);

            tt.euclid2d.Point pos2d = tt.euclid2d.Point.interpolate(
                    new tt.euclid2d.Point(start.x, start.y),
                    new tt.euclid2d.Point(end.x, end.y), alpha);

            tt.euclid2i.Point trajPoint = trajectory.get(t);
            tt.euclid2i.Point linePoint = new tt.euclid2i.Point((int) Math.round(pos2d.x), (int) Math.round(pos2d.y));

            if (trajPoint.distance(linePoint) < radius) {
                return true;
            }
        }

        return false;
	}
	
	public boolean intersectsPoint(tt.euclid2i.Point point) {
		int tmin = trajectory.getMinTime();
        int tmax = trajectory.getMaxTime();

        for (int t = tmin; t <= tmax; t += samplingInterval) {
            tt.euclid2i.Point trajPoint = trajectory.get(t);
            if (trajPoint.distance(point) < radius) {
                return true;
            }
        }
        return false;
	}

	protected boolean intersectsLineAnalytic(Point start, Point end) {
		assert trajectory instanceof SegmentedTrajectory;
		SegmentedTrajectory segTraj = (SegmentedTrajectory) trajectory;
		return SeparationDetector.hasAnyPairwiseConflictAnalytic(new LinearTrajectory(start, end, 0.0), new SegmentedTrajectory[] {segTraj}, radius);
	}

    @Override
    public boolean isInside(Point p) {
        if (p.getTime() >= trajectory.getMinTime() && p.getTime() <= trajectory.getMaxTime()) {
            return (p.getPosition().distance(trajectory.get(p.getTime())) < radius);
        } else {
            return false;
        }

    }

    public Trajectory getTrajectory() {
        return trajectory;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public HyperRectangle getBoundingBox() {
        return new HyperRectangle(new Point(Integer.MIN_VALUE, Integer.MIN_VALUE, getTrajectory().getMinTime()),
                                  new Point(Integer.MAX_VALUE, Integer.MAX_VALUE, getTrajectory().getMaxTime()));
    }

    @Override
    public String toString() {
        return "MC(" + Integer.toHexString(trajectory.hashCode()) + ", " + radius + ")";
    }
    
    public int getSamplingInterval() {
		return samplingInterval;
	}

}
