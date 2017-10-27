package tt.util.intervaltree;

public class Interval implements Cloneable {

    private double a;
    private double b;
    private double middle;

    public Interval(double a, double b) {
        if (a < b) {
            this.a = a;
            this.b = b;
        } else {
            this.a = b;
            this.b = a;
        }

        calculateMiddle();
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
        calculateMiddle();
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
        calculateMiddle();
    }

    private void calculateMiddle() {
        middle = (this.a + this.b) / 2;
    }

    public boolean intersects(double point) {
        return a <= point && b >= point;
    }

    public boolean intersects(Interval that) {
        Interval first;
        Interval second;

        if (this.a < that.a) {
            first = this;
            second = that;
        } else {
            first = that;
            second = this;
        }

        return first.a <= second.a && first.b >= second.a;
    }

    public boolean intersects(double a, double b) {
        return intersects(new Interval(a, b));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interval interval = (Interval) o;

        if (Double.compare(interval.a, a) != 0) return false;
        if (Double.compare(interval.b, b) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(a);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("Interval{a=%s, b=%s}", a, b);
    }

    @Override
    public Interval clone() {
        return new Interval(this.a, this.b);
    }
}
