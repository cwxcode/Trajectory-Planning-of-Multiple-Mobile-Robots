package tt.euclidtime3i.discretization;

import tt.euclidtime3i.Point;


public class Straight {

    private Point start;
    private Point end;

    public Straight(tt.euclid2i.Point start, int tStart, tt.euclid2i.Point end, int tEnd) {
        this(new Point(start, tStart), new Point(end, tEnd));
    }

    public Straight(Point start, Point end) {
        super();
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

    public Straight cut(int time1, int time2) {
        Point p1 = interpolate(time1);
        Point p2 = interpolate(time2);

        return new Straight(p1, p2);
    }

    public tt.euclid2d.Point interpolateFloat(int time) {
        int tStart = start.getTime();
        int tEnd = end.getTime();

        if (time < tStart || tEnd < time)
            throw new RuntimeException("This straight is not defined in time " + time);

        if (time == tStart)
            return start.getPosition().toPoint2d();

        if (time == tEnd)
            return end.getPosition().toPoint2d();

        double scale = ((double) (time - tStart)) / (tEnd - tStart);

        int xStart = start.getX();
        int xEnd = end.getX();

        int yStart = start.getY();
        int yEnd = end.getY();

        double x = xStart + (xEnd - xStart) * scale;
        double y = yStart + (yEnd - yStart) * scale;

        return new tt.euclid2d.Point(x, y);
    }

    public Point interpolate(int time) {
        int tStart = start.getTime();
        int tEnd = end.getTime();

        if (time < tStart || tEnd < time)
            throw new RuntimeException("This straight is not defined in time " + time);

        if (time == tStart)
            return start;

        if (time == tEnd)
            return end;

        double scale = ((double) (time - tStart)) / (tEnd - tStart);

        int xStart = start.getX();
        int xEnd = end.getX();

        int yStart = start.getY();
        int yEnd = end.getY();

        int x = (int) Math.round(xStart + (xEnd - xStart) * scale);
        int y = (int) Math.round(yStart + (yEnd - yStart) * scale);

        return new Point(x, y, time);
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
        Straight other = (Straight) obj;
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
