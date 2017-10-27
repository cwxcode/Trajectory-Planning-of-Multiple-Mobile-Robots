package tt.euclid2d;


public class Line {
    private static final long serialVersionUID = -2519868162204278196L;

    private Point start;
    private Point end;

    public Line(Point start, Point end, double speed) {
        super();
        this.start = start;
        this.end = end;
    }

    public Line(tt.euclid2i.Line line) {
        super();
        this.start = line.getStart().toPoint2d();
        this.end = line.getEnd().toPoint2d();
    }

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public double getDistance() {
        return start.distance(end);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
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
        Line other = (Line) obj;
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
        return true;
    }


    @Override
    public String toString() {
        return String.format("(%s : %s)", start, end);
    }


}
