package tt.jointtraj.homotopyiterative;

import org.jscience.mathematics.number.Complex;
import tt.euclid2i.EvaluatedTrajectory;

public class LocalHClassSolution {

    public LocalHClassSolution() {
    }

    public LocalHClassSolution(EvaluatedTrajectory[] trajectories, Complex[] hValues, double cost) {
        this.trajectories = trajectories;
        this.hValues = hValues;
        this.cost = cost;
    }

    public EvaluatedTrajectory[] trajectories;
    public Complex[] hValues;
    public double cost;

}
