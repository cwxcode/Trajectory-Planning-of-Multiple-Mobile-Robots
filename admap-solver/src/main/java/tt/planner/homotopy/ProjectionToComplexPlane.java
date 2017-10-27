package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;

public interface ProjectionToComplexPlane<S> {

    public Complex complexValue(S state);

}