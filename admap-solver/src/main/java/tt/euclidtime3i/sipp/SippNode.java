package tt.euclidtime3i.sipp;

import tt.euclid2i.Point;
import tt.euclidtime3i.sipp.intervals.Interval;

public class SippNode {

    private Point point;
    private Interval safeInterval;
    private int time;

    public SippNode(Point point, Interval safeInterval, int time) {
        this.point = point;
        this.safeInterval = safeInterval;
        this.time = time;
    }

    public Point getPoint() {
        return point;
    }

    public int getTime() {
        return time;
    }

    public Interval getSafeInterval() {
        return safeInterval;
    }

    /*
     * Note that time is not part of the state information, thus equals and hashcode should ignore it.
     **/

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		result = prime * result
				+ ((safeInterval == null) ? 0 : safeInterval.hashCode());
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
		SippNode other = (SippNode) obj;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		if (safeInterval == null) {
			if (other.safeInterval != null)
				return false;
		} else if (!safeInterval.equals(other.safeInterval))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return String.format("SIPPNode{%s, %s, %d}", point, safeInterval, time);
    }
}
