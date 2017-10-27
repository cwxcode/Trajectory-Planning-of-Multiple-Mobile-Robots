package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

import java.util.ArrayList;
import java.util.List;

/**
 * This integrator implements the analytic approach as it is described in the paper. This approach is suitable only for
 * graphs which edges are only <b>SMALL LINE SEGMENTS (grid)</b>. It is caused by possible multiple discontinuity in
 * imaginary part of poleContribution.
 */

public class HValueAnalyticIntegrator implements HValueIntegrator {

    private List<Complex> qRoots;
    private List<Complex> pRoots;
    //
    private List<Complex> residues;


    /**
     * @param qRoots N-1 roots of the nominator (samples of free space)
     * @param pRoots N roots of the denominator (samples representing obstacles)
     */
    public HValueAnalyticIntegrator(List<Complex> qRoots, List<Complex> pRoots) {
        this.qRoots = qRoots;
        this.pRoots = pRoots;
        this.residues = calculateFractionCoefficient();
    }

    @Override
    public Complex lineSegmentIncrement(Complex start, Complex end) {
        Complex increment = Complex.ZERO;
        for (int i = 0; i < pRoots.size(); i++) {
            Complex poleContribution = poleContribution(start, end, pRoots.get(i));
            increment = increment.plus(residues.get(i).times(poleContribution));
        }
        return increment;
    }

    private Complex poleContribution(Complex start, Complex end, Complex pole) {
        Complex startToPole = start.minus(pole);
        Complex endToPole = end.minus(pole);

        //Real Part
        double realPart = Math.log(endToPole.magnitude()) - Math.log(startToPole.magnitude());

        //Imaginary Part
        double imaginaryPart = endToPole.argument() - startToPole.argument();
        while (imaginaryPart < -Math.PI) imaginaryPart += 2 * Math.PI;
        while (imaginaryPart > Math.PI) imaginaryPart -= 2 * Math.PI;

        return Complex.valueOf(realPart, imaginaryPart);
    }


    private List<Complex> calculateFractionCoefficient() {
        List<Complex> residues = new ArrayList<Complex>();

        for (int l = 0; l < pRoots.size(); l++) {
            Complex pEvaluated = logEvaluateDenominator(l);
            Complex qEvaluated = logEvaluateNominator(l);
            residues.add(qEvaluated.minus(pEvaluated).exp());
        }

        return residues;
    }

    private Complex logEvaluateNominator(int l) {
        Complex qEvaluated = Complex.ZERO;
        for (int j = 0; j < qRoots.size(); j++) {
            qEvaluated = qEvaluated.plus(qRoots.get(j).minus(pRoots.get(l)).log());

        }
        return qEvaluated;
    }

    private Complex logEvaluateDenominator(int l) {
        Complex pEvaluated = Complex.ZERO;
        for (int j = 0; j < pRoots.size(); j++) {
            if (l == j) continue;
            pEvaluated = pEvaluated.plus(pRoots.get(j).minus(pRoots.get(l)).log());

        }
        return pEvaluated;
    }
}
