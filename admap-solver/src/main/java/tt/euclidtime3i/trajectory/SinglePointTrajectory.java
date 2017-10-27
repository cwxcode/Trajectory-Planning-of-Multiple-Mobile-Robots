package tt.euclidtime3i.trajectory;

import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.Point;

class SinglePointTrajectory implements EvaluatedTrajectory {
    Point point;
    int time;
    double cost;

    public SinglePointTrajectory(Point point, int time, double cost) {
        super();
        this.point = point;
        this.time = time;
        this.cost = cost;
    }

    @Override
    public int getMinTime() {
        return time;
    }

    @Override
    public int getMaxTime() {
        return time;
    }

    @Override
    public Point get(int t) {
        return point;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return point.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((point == null) ? 0 : point.hashCode());
        result = prime * result + time;
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
        SinglePointTrajectory other = (SinglePointTrajectory) obj;
        if (point == null) {
            if (other.point != null)
                return false;
        } else if (!point.equals(other.point))
            return false;
        if (time != other.time)
            return false;
        return true;
    }
};

