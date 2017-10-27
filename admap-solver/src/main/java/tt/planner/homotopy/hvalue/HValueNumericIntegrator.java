package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

import java.util.List;


/**
 * This class implements simple numeric integrator. This approach is <b>suitable for every graph</b>, but is computationally
 * <b>slow</b>. For some special cases it might be better to use analytic integrator.
 */

public class HValueNumericIntegrator implements HValueIntegrator {

    private final int minSamples;
    private final double step;
    private final List<Complex> qRoots;
    private final List<Complex> pRoots;

    /**
     * @param qRoots     (not necessarily N-1) roots of the nominator (samples of free space)
     * @param pRoots     N roots of the denominator (samples representing obstacles)
     * @param step       discretization step
     * @param minSamples minimum number of samples per edge
     */
    public HValueNumericIntegrator(List<Complex> qRoots, List<Complex> pRoots, double step, int minSamples) {
        this.qRoots = qRoots;
        this.pRoots = pRoots;
        this.step = step;
        this.minSamples = minSamples;
    }

    @Override
    public Complex lineSegmentIncrement(Complex start, Complex end) {
        Complex integralSum = Complex.ZERO;

        int samples = (int) (end.minus(start).magnitude() / step);
        samples = (samples < minSamples) ? minSamples : samples;

        for (double i = 0; i < samples; i++) {
            double lambda = i / samples;
            Complex point = start.times(1 - lambda).plus(end.times(lambda));
            integralSum = integralSum.plus(fractionValue(point));
        }

        return integralSum.times(end.minus(start)).divide(samples);
    }

    private Complex fractionValue(Complex point) {
        Complex qEvaluated = logEvaluatePolynomial(qRoots, point);
        Complex pEvaluated = logEvaluatePolynomial(pRoots, point);
        return qEvaluated.minus(pEvaluated).exp();
    }

    private Complex logEvaluatePolynomial(List<Complex> roots, Complex point) {
        Complex evaluated = Complex.ZERO;
        for (Complex root : roots) {
            evaluated = evaluated.plus(point.minus(root).log());
        }
        return evaluated;
    }
}
