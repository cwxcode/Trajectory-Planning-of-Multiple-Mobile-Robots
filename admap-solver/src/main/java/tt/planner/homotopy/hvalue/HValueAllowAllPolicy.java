package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

public class HValueAllowAllPolicy implements HValuePolicy {

    public HValueAllowAllPolicy() {
    }

    @Override
    public boolean isAllowed(Complex hValue, double precision) {
        return true;
    }

}
