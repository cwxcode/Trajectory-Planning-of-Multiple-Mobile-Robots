package tt.euclidtime3i.sipprrts;

import tt.euclid2i.Point;
import tt.euclidtime3i.sipp.intervals.Interval;
import tt.euclidtime3i.sipp.intervals.SafeIntervalList;

/**
 * Created by Vojtech Letal on 2/25/14.
 */
public class SippRRTNode {

    private Point point;
    private SafeIntervalList intervals;
    private int sampledInterval;
    private int time;

    public SippRRTNode(Point point, SafeIntervalList intervals, int sampledInterval) {
        this.point = point;
        this.intervals = intervals;
        this.sampledInterval = sampledInterval;
        this.time = Integer.MIN_VALUE;
    }

    public SippRRTNode(Point point, SafeIntervalList intervals, int sampledInterval, int time) {
        this(point, intervals, sampledInterval);
        setTime(time);
    }

    public void setTime(int time) {
        Interval sampledInterval = intervals.get(this.sampledInterval);

        if (!sampledInterval.contains(time))
            throw new IllegalArgumentException("Time cannot be set outside the samples Interval" + time);

        this.time = time;
    }


    public Point getPoint() {
        return point;
    }

    public Interval getSafeInterval() {
        return intervals.get(sampledInterval);
    }

    public boolean isInLastSafeInterval() {
        return sampledInterval == intervals.size() - 1;
    }

    public int getTime() {
        if (time == Integer.MIN_VALUE)
            throw new RuntimeException("The time value has not been set yet");

        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SippRRTNode that = (SippRRTNode) o;

        if (sampledInterval != that.sampledInterval) return false;
        if (!intervals.equals(that.intervals)) return false;
        if (!point.equals(that.point)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = point.hashCode();
        result = 31 * result + intervals.hashCode();
        result = 31 * result + sampledInterval;
        return result;
    }

    @Override
    public String toString() {
        return String.format("Node{%s, %d, %d}", point, sampledInterval, time);
    }
}
