package tt.jointeuclideanandtime2ni1i;


public class Point {
    final int time;
    final tt.jointeuclid2ni.Point positions;

    public Point(tt.euclid2i.Point[] positions, int time) {
        this.positions = new tt.jointeuclid2ni.Point(positions);
        this.time = time;
    }

    public Point(tt.jointeuclid2ni.Point positions, int time) {
        this.positions = positions;
        this.time = time;
    }


    public tt.euclid2i.Point[] getPositionsArray() {
        return positions.asArray();
    }

    public tt.jointeuclid2ni.Point getPositions() {
        return positions;
    }

    public int getTime() {
        return time;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i=0; i < getPositionsArray().length; i++) {
            sb.append(getPositionsArray()[i]);
            sb.append(", ");
        }

        sb.delete(sb.length()-2, sb.length());

        sb.append(" @"+getTime());
        sb.append("]");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((positions == null) ? 0 : positions.hashCode());
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
        Point other = (Point) obj;
        if (positions == null) {
            if (other.positions != null)
                return false;
        } else if (!positions.equals(other.positions))
            return false;
        if (time != other.time)
            return false;
        return true;
    }


}
