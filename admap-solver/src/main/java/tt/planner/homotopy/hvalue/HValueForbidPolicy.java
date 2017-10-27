package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HValueForbidPolicy implements HValuePolicy {

    private Set<Complex> forbidden;

    public HValueForbidPolicy() {
        forbidden = new HashSet<Complex>();
    }

    public HValueForbidPolicy(Complex forbid) {
        this();
        forbidden.add(forbid);
    }

    public HValueForbidPolicy(Collection<Complex> forbid) {
        this();
        forbidden.addAll(forbid);
    }

    @Override
    public boolean isAllowed(Complex hValue, double precision) {
        for (Complex value : forbidden) {
            if (hValue.equals(value))
                return false;

            double diff = value.minus(hValue).magnitude();
            double abs = (value.magnitude() + hValue.magnitude()) / 2;

            if (diff / abs < precision) return false;
        }
        return true;
    }

    public void forbid(Complex hValue) {
        forbidden.add(hValue);
    }

    public void forbidAll(Collection<Complex> hValues) {
        forbidden.addAll(hValues);
    }
}
