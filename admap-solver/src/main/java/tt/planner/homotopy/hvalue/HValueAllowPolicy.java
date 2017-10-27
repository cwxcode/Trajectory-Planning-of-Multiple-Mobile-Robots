package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HValueAllowPolicy implements HValuePolicy {

    private Set<Complex> allowed;

    public HValueAllowPolicy() {
        allowed = new HashSet<Complex>();
    }

    public HValueAllowPolicy(Complex allow) {
        this();
        allowed.add(allow);
    }

    public HValueAllowPolicy(Collection<Complex> allow) {
        this();
        allowed.addAll(allow);
    }

    @Override
    public boolean isAllowed(Complex hValue, double precision) {
        for (Complex value : allowed) {
            if (hValue.equals(value))
                return true;

            double diff = value.minus(hValue).magnitude();
            double abs = (value.magnitude() + hValue.magnitude()) / 2;

            if (diff / abs < precision) return true;
        }
        return false;
    }

    public void allow(Complex hValue) {
        allowed.add(hValue);
    }

    public void allowAll(Collection<Complex> hValues) {
        allowed.addAll(hValues);
    }
}
