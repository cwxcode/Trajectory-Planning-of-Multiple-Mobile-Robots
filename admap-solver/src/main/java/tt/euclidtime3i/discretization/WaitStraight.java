package tt.euclidtime3i.discretization;

import tt.euclidtime3i.Point;

public class WaitStraight {
    private Point start;
    private Point end;
	private int waitFor;

    public WaitStraight(Point start, int waitFor, Point end) {
        this.waitFor = waitFor;
        if (start.getTime() <= end.getTime()) {
            this.start = start;
            this.end = end;
        } else {
            this.start = end;
            this.end = start;
        }
    }

    public int duration() {
        return end.getTime() - start.getTime();
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + waitFor;
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
		WaitStraight other = (WaitStraight) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (waitFor != other.waitFor)
			return false;
		return true;
	}

	@Override
    public String toString() {
        return String.format("(%s (w:%d): %s)", start, waitFor, end);
    }

}
