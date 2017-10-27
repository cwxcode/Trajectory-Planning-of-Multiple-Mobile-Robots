package tt.euclidtime3i.sipp.intervals;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.RandomAccess;

public class SafeIntervalList extends AbstractList<Interval> implements RandomAccess {

    private int lastID;
    private int maxTime;
    private LinkedList<Interval> safeIntervals;

    public SafeIntervalList(int maxTime) {
        this.maxTime = maxTime;
        this.lastID = -1;
        this.safeIntervals = new LinkedList<Interval>();
        this.safeIntervals.add(new Interval(0, maxTime));
    }

    public Interval get(int k) {
        return safeIntervals.get(k);
    }

    void markCollisionInTime(int id, int time) {
        if (time > maxTime) {
            String message = String.format("Collision marked after maxTime while creating safe interval %d %d", maxTime, time);
            throw new IllegalArgumentException(message);
        }

        if (id == lastID + 1) {
            shortenLastSafeInterval(time);
        } else {
            closePreviousSafeInterval(time);
            appendNewSafeInterval(time);
        }

        lastID = id;
    }

    private void closePreviousSafeInterval(int time) {
        Interval last = safeIntervals.getLast();
        last.end = time;

        if (last.start == last.end)
            safeIntervals.removeLast();
    }

    private void appendNewSafeInterval(int time) {
        safeIntervals.add(new Interval(time, maxTime));
    }

    private void shortenLastSafeInterval(int time) {
        Interval last = safeIntervals.getLast();
        last.start = time;

        if (last.start == maxTime)
            safeIntervals.removeLast();
    }

    @Override
    public Iterator<Interval> iterator() {
        return safeIntervals.iterator();
    }

    @Override
    public int size() {
        return safeIntervals.size();
    }

    @Override
    public String toString() {
        return String.format("SafeIntervals{%d, %s}", lastID, safeIntervals);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SafeIntervalList intervals = (SafeIntervalList) o;
        if (!safeIntervals.equals(intervals.safeIntervals)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return safeIntervals.hashCode();
    }
}
