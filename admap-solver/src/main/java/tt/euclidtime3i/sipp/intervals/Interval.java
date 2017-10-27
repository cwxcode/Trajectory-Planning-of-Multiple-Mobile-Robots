package tt.euclidtime3i.sipp.intervals;

public class Interval {

    public static Interval zeroToInfinity() {
        return toInfinity(0);
    }

    public static Interval toInfinity(int val) {
        return new Interval(val, Integer.MAX_VALUE);
    }

    public int start;
    public int end;

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int length() {
        return end - start;
    }

    public boolean contains(int time) {
        return time >= start && time <= end;
    }

    public Interval intersection(Interval that) {
        int start = Math.max(this.start, that.start);
        int end = Math.min(this.end, that.end);

        if (end < start)
            return null;
        else
            return new Interval(start, end);
    }

    @Override
    public String toString() {
        return String.format("Interval{%d,%d}", start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interval interval = (Interval) o;
        return end == interval.end
                && start == interval.start;
    }

    @Override
    public int hashCode() {
        return 31 * start + end;
    }
}

