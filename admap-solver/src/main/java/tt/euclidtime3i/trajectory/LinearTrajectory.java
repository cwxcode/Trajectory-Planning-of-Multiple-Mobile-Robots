package tt.euclidtime3i.trajectory;

import java.util.Collections;
import java.util.List;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;

public class LinearTrajectory implements SegmentedTrajectory, EvaluatedTrajectory  {

    private Straight straight;
	private double cost;

	public LinearTrajectory(Point start, Point end, double cost) {
       this.straight =  new Straight(start, end);
       this.cost = cost;
    }

	@Override
	public int getMinTime() {
		return straight.getStart().getTime();
	}

	@Override
	public int getMaxTime() {
		return straight.getEnd().getTime();
	}

	@Override
	public tt.euclid2i.Point get(int t) {
		return straight.interpolate(t).getPosition();
	}

	@Override
	public double getCost() {
		return cost;
	}

	@Override
	public List<Straight> getSegments() {
		return Collections.singletonList(straight);
	}

}
