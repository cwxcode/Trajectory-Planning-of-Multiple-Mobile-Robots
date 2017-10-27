package tt.euclid2i.trajectory;

import java.util.Collection;

import tt.euclid2i.Region;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.util.Util;
import tt.euclidtime3i.discretization.Straight;

public class SegmentedTrajectories {
	public static boolean isInFreeSpace(SegmentedTrajectory traj, Collection<Region> obstacles) {
		for (Straight segment  : traj.getSegments()) {
			if (!Util.isVisible(segment.getStart().getPosition(), segment.getEnd().getPosition(), obstacles)) {
				return false;
			}
		}
		return true;
	}
}
