package tt.jointeuclid2ni.solver.impl;


import tt.euclid2i.EvaluatedTrajectory;
import tt.euclidtime3i.discretization.softconstraints.PairwiseConstraint;
import tt.euclidtime3i.discretization.softconstraints.PenaltyFunction;
import tt.jointtraj.separableflow.PenaltyOptimizer;
import tt.jointtraj.separableflow.TrajectoryOptimizer;
import tt.util.Args;



public class AlgorithmDPMD extends AbstractDPMBasedAlgorithm {

    private int k = 50;
    

    public AlgorithmDPMD() {
        this(new String[0]);
    }

    public AlgorithmDPMD(String[] args) {
    	super(args);
        k = Integer.parseInt(Args.getArgumentValue(args, "-k", true));
    }

    @Override
    protected EvaluatedTrajectory[] solveCore(
            TrajectoryOptimizer[] trajectoryOptimizers,
            PenaltyFunction[][] penalties) {

        EvaluatedTrajectory[] trajs = PenaltyOptimizer.solve(
                trajectoryOptimizers, penalties, k, 1000000,
                params.samplingInterval, 
                params.runtimeDeadlineMs, params.showVis, false);

        return trajs;
    }


}
