package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

public interface HValuePolicy {

    public boolean isAllowed(Complex hValue, double precision);
}
