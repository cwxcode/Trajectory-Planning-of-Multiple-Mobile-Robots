package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

/**
 * This class is used to calculate HValue increment on edge of graph.
 */
public interface HValueIntegrator {

    public Complex lineSegmentIncrement(Complex start, Complex end);
}
