package tt.util.intervaltree;

/**
 * @author Alternative
 */
public class IntervalAVLNode<V> {

    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    int heightL, heightR, directionFromParent;
    IntervalAVLNode<V> left, right, parent;
    V value;

    Interval interval;
    Interval covered;

    IntervalAVLNode(V value, Interval interval, IntervalAVLNode<V> parent, int directionFromParent) {
        this.value = value;
        this.directionFromParent = directionFromParent;
        this.parent = parent;
        this.heightL = -1;
        this.heightR = -1;
        this.interval = interval;
        this.covered = interval.clone();
    }

    IntervalAVLNode(V value, Interval interval) {
        this(value, interval, null, 0);
    }

    public void refreshUpperNodes() {
        //TODO write me like one of your french girls Jack

        covered.setB(interval.getB());
        covered.setB(Math.max(covered.getB(), (left == null) ? Double.NEGATIVE_INFINITY : left.covered.getB()));
        covered.setB(Math.max(covered.getB(), (right == null) ? Double.NEGATIVE_INFINITY : right.covered.getB()));

        covered.setA(interval.getA());
        covered.setA(Math.min(covered.getA(), (left == null) ? Double.POSITIVE_INFINITY : left.covered.getA()));
        covered.setA(Math.min(covered.getA(), (right == null) ? Double.POSITIVE_INFINITY : right.covered.getA()));

        if (parent != null) {
            if (directionFromParent == LEFT) {
                parent.heightL = Math.max(this.heightL, this.heightR) + 1;
            } else {
                parent.heightR = Math.max(this.heightL, this.heightR) + 1;
            }
            parent.refreshUpperNodes();
        }
    }

    @Override
    public String toString() {
        String side, ok;
        if (this.directionFromParent == 0) {
            side = "0";
        } else {
            if (this.directionFromParent > 0) {
                side = "R";
            } else {
                side = "L";
            }
        }
        if (this.isUnbalanced()) {
            ok = "xx";
        } else {
            ok = "oo";
        }
        return String.format("%s>%s> %s %s %s L:%d R:%d", ok, side, value, interval, covered, heightL, heightR);
    }

    public boolean isUnbalanced() {
        int difference = this.heightL - this.heightR;
        return difference < -1 || difference > 1;
    }
}
