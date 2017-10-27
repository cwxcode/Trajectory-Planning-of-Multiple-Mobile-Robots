package tt.jointtraj.separableflow;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclidtime3i.discretization.softconstraints.BumpSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.ConstantSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.LinearSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.PairwiseConstraint;
import tt.euclidtime3i.discretization.softconstraints.PenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.PenaltyIntegrator;
import tt.euclidtime3i.vis.TimeParameter;
import tt.util.AgentColors;
import tt.util.Verbose;
import tt.vis.FastAgentsLayer;
import tt.vis.FastTrajectoriesLayer;
import tt.vis.FastTrajectoriesLayer.ColorProvider;
import tt.vis.FastTrajectoriesLayer.TrajectoriesProvider;
import tt.vis.ParameterControlLayer;
import tt.vis.TimeParameterHolder;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;

public class PenaltyOptimizer {

    private static final int TAN_COEF = 1;
    private static boolean showProgress;
    private static boolean keyTrace;
    protected static EvaluatedTrajectory[] currentTrajectoriesForVis;
    protected static int[] bodyRadiuses;

    public static EvaluatedTrajectory[] solve(
            TrajectoryOptimizer[] trajectoryOptimizers,
            final PenaltyFunction[][] penaltyFunctions, 
            int k, 
            double maxCost,
            int penaltySamplingInterval,
            long runtimeDeadlineMs, boolean showProgressFlag, boolean keyTraceFlag) {

        int nAgents = trajectoryOptimizers.length;
        showProgress = showProgressFlag;
        keyTrace = keyTraceFlag;
        Verbose.setVerbose(showProgress);

        // Debug visualisation
        if (showProgress) {
            bodyRadiuses = new int[penaltyFunctions.length];
            // HACK: Assuming that everyone has the same radius and that we have at least two agents
            int radius = 0;
            if (penaltyFunctions[0].length == 1 ) {
            	radius = 50;
	        } else {
	            if (penaltyFunctions[0][1] instanceof BumpSeparationPenaltyFunction) {
	                 radius = (int) (((BumpSeparationPenaltyFunction)penaltyFunctions[0][1]).getMinSeparation()/2);
	            } else if (penaltyFunctions[0][1] instanceof LinearSeparationPenaltyFunction) {
	                radius = (int) (((LinearSeparationPenaltyFunction)penaltyFunctions[0][1]).getMinSeparation()/2);
	            } else if (penaltyFunctions[0][1] instanceof ConstantSeparationPenaltyFunction) {
	                radius = (int) (((ConstantSeparationPenaltyFunction)penaltyFunctions[0][1]).getMinSeparation()/2);
	            }

	            for (int i=0; i<trajectoryOptimizers.length; i++) {
	                bodyRadiuses[i] = radius;
	            }
            }
            initProgressVisio();
        }

        EvaluatedTrajectory[] trajectories = new EvaluatedTrajectory[nAgents];
        if (k == 1) {
            trajectories = prioritizedPlanning(trajectories,
                    trajectoryOptimizers, penaltyFunctions, maxCost,
                    runtimeDeadlineMs);
        }

        if (k >= 2) {
            // Find initial unconstrained trajectories
            trajectories = initUnconstrainedTrajectories(trajectoryOptimizers,
                    maxCost, runtimeDeadlineMs);

            if (trajectories == null)
                return null;

            if (k >= 3) {
                // Iteratively replan
                int iterations = nAgents * (k - 2);
                trajectories = iterativelyReplan(trajectories,
                        trajectoryOptimizers, penaltyFunctions, iterations,
                        penaltySamplingInterval,
                        maxCost, runtimeDeadlineMs);

                if (trajectories == null)
                    return null;

            }

            // Replan with hard-constraints
            trajectories = replanWithInfiniteWeight(trajectories,
	            trajectoryOptimizers, penaltyFunctions, maxCost,
	            runtimeDeadlineMs);
            
            // Consistency check
            
            double remainingPenalty = computeGlobalPenalty(penaltyFunctions, trajectories, penaltySamplingInterval);
            if (remainingPenalty < 0.00001) {
            	return trajectories;
            } else {
            	Verbose.printf("!!! FAIL -- the final trajectories are not consistent! Global Penalty %.4f\n", remainingPenalty);
            	return null;
            }
        }

        return trajectories;
    }

	private static void initProgressVisio() {
    	
        if (TimeParameterHolder.time == null) {
            TimeParameterHolder.time = new TimeParameter(10);
            VisManager.registerLayer(ParameterControlLayer.create(TimeParameterHolder.time));
        }
        int timeStep = TimeParameterHolder.time.getTimeStep();
        
        VisManager.registerLayer(KeyToggleLayer.create("t", true, 
        		FastTrajectoriesLayer.create(
                        new TrajectoriesProvider() {

                            @Override
                            public Trajectory[] getTrajectories() {
                                return currentTrajectoriesForVis;
                            }
                        }, new ColorProvider() {

                            @Override
                            public Color getColor(int i) {
                                return AgentColors.getColorForAgent(i);
                            }

                        }, 3, /*timeStep*/ 2)));



        VisManager.registerLayer(KeyToggleLayer.create("x", true, 
        	FastAgentsLayer.create(new FastAgentsLayer.TrajectoriesProvider() {

            @Override
            public Trajectory[] getTrajectories() {
                return currentTrajectoriesForVis;
            }

            @Override
            public int[] getBodyRadiuses() {
                return bodyRadiuses;
            }
        }, new FastAgentsLayer.ColorProvider() {
            @Override
            public Color getColor(int i) {
                return AgentColors.getColorForAgent(i);
            }
        }, TimeParameterHolder.time)));
    }

    private static EvaluatedTrajectory[] prioritizedPlanning(
            EvaluatedTrajectory[] trajectories,
            TrajectoryOptimizer[] trajectoryOptimizers,
            final PenaltyFunction[][] penaltyFunctions, double maxCost,
            long runtimeDeadlineMs) {
        int nAgents = trajectoryOptimizers.length;

        for (int i = 0; i < nAgents; i++) {

            Verbose.printf("Prioritized planning for agent %d. \n", i);

            if (keyTrace) {
	            try {
	                System.in.read();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
            }

            Trajectory[] otherTrajectories = higherPriorityTrajectories(
                    trajectories, i);
            PenaltyFunction[] penaltyFunctionsToOtherTrajectories = constraintsWithHigherPriorityAgents(
                    penaltyFunctions, i);
            penaltyFunctionsToOtherTrajectories = applyWeight(
                    penaltyFunctionsToOtherTrajectories,
                    Double.POSITIVE_INFINITY);

            EvaluatedTrajectory bestResponseTraj = trajectoryOptimizers[i]
                    .getOptimalTrajectoryConstrained(
                            penaltyFunctionsToOtherTrajectories,
                            otherTrajectories, null, maxCost, runtimeDeadlineMs);

            if (bestResponseTraj != null) {
                trajectories[i] = bestResponseTraj;
                currentTrajectoriesForVis = trajectories;
            } else {
                return null;
            }
        }

        return trajectories;
    }

    private static EvaluatedTrajectory[] iterativelyReplan(
            EvaluatedTrajectory[] trajectories,
            TrajectoryOptimizer[] trajectoryOptimizers,
            final PenaltyFunction[][] penaltyFunctions, 
            int nIterations,
            int penaltySamplingInterval,
            double maxCost, long runtimeDeadlineMs) {
        int nAgents = trajectoryOptimizers.length;
        
        // create random sequence of robots
        int[] agentSequence = new int[nIterations];
        for (int i=0; i < nIterations; i++) {
        	agentSequence[i] = i % nAgents;
        }
        agentSequence = shuffleArray(agentSequence, new Random(1));
        
        Verbose.printf(">>> Iterative phase: will iterate using sequence: %s \n", Arrays.toString(agentSequence));
        
        double gcost = 0;
        double gpenalty = 0;
        if (Verbose.isVerbose()) {
	        gcost = computeGlobalCost(trajectories);
	        gpenalty = computeGlobalPenalty(penaltyFunctions, trajectories, penaltySamplingInterval);
        }
        
        Verbose.printf("Global cost: %.4f (initial) Global Penalty: %.4f (initial)\n\n", gcost, gpenalty);

        for (int i = 0; i < nIterations; i++) {
            int r = agentSequence[i];

            if (keyTrace) {
	            try {
	                System.in.read();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
            }

            double w = calculateSoftConstraintsWeight(i, nIterations);
            Verbose.printf("Agent: %d; k:%d/%d  w: %.4f. \n", r, i, nIterations, w);

            Trajectory[] otherTrajectories = otherTrajectories(trajectories, r);
            PenaltyFunction[] penaltyFunctionsOfOtherTrajectories 
            	= constraintsOf(penaltyFunctions, r);

            PenaltyFunction[] penaltyFunctionsOfOtherTrajectoriesWeighted 
            	= applyWeight(penaltyFunctionsOfOtherTrajectories, w);
            
            if (Verbose.isVerbose()) {
	            printPenaltyBreakout(trajectories[r],
	                    penaltyFunctionsOfOtherTrajectories, otherTrajectories, w);
            }

            EvaluatedTrajectory bestResponseTraj = trajectoryOptimizers[r]
                    .getOptimalTrajectoryConstrained(
                            penaltyFunctionsOfOtherTrajectoriesWeighted,
                            otherTrajectories, trajectories[r], maxCost,
                            runtimeDeadlineMs);

            if (bestResponseTraj != null) {
                trajectories[r] = bestResponseTraj;
                currentTrajectoriesForVis = trajectories;
            } else {
                return null;
            }
            
            if (Verbose.isVerbose()) {
	            double newgcost = computeGlobalCost(trajectories);
	            double newgpenalty = computeGlobalPenalty(penaltyFunctions, trajectories, penaltySamplingInterval);
	            Verbose.printf("New global cost: %.4f (Δ: %.4f) New global Penalty: %.4f (Δ: %.4f)\n\n", newgcost, (newgcost-gcost), newgpenalty, (newgpenalty-gpenalty));
	            gcost = newgcost;
	            gpenalty = newgpenalty;
            }
        }

        return trajectories;
    }

    private static double computeGlobalPenalty(
            PenaltyFunction[][] penaltyFunctions,
            EvaluatedTrajectory[] trajectories, 
            int penaltySamplingInterval) {
        double penalty = 0;
        for (int i = 0; i < penaltyFunctions.length; i++) {
            for (int j = 0; j < i; j++) {
                penalty += PenaltyIntegrator.integratePenaltySingle(trajectories[i], penaltyFunctions[i][j], trajectories[j], penaltySamplingInterval);
            }
        }
        return penalty;

    }

    private static double computeGlobalCost(EvaluatedTrajectory[] trajectories) {
        double cost = 0;
        for (int i = 0; i < trajectories.length; i++) {
            cost += trajectories[i].getCost();
        }
        return cost;
    }

    private static void printPenaltyBreakout(EvaluatedTrajectory thisTraj,
            PenaltyFunction[] penaltyFunctionsOfOtherTrajectories,
            Trajectory[] otherTrajectories, double w) {

        Verbose.printf("Trajectory cost: %.4f;\t", thisTraj.getCost());
        Verbose.printf("Penalties with other robots: ");
        
        for (int i = 0; i < otherTrajectories.length; i++) {
            double penalty = PenaltyIntegrator.integratePenaltySingle(thisTraj,
                    penaltyFunctionsOfOtherTrajectories[i],
                    otherTrajectories[i], 10);

            Verbose.printf("%.4f * %.4f = %.4f\t", penalty, w, penalty*w);
        }

        Verbose.print("\n");
    }

    private static PenaltyFunction[] applyWeight(
            final PenaltyFunction[] penaltyFunctions, final double w) {

        PenaltyFunction[] weighted = new PenaltyFunction[penaltyFunctions.length];

        for (int i = 0; i < weighted.length; i++) {
            final int iFinal = i;
            weighted[i] = new PenaltyFunction() {

                @Override
                public double getPenalty(double dist, double t) {
                    double penalty = penaltyFunctions[iFinal].getPenalty(dist, t);
                    if (penalty == 0) {
                        return 0;
                    } else {
                        return w * penaltyFunctions[iFinal].getPenalty(dist, t);
                    }
                }

				@Override
				public int getSeparationSquare() {
					return penaltyFunctions[iFinal].getSeparationSquare();
				}
                
                
                
            };
        }

        return weighted;
    }

    private static EvaluatedTrajectory[] replanWithInfiniteWeight(
            EvaluatedTrajectory[] trajectories,
            TrajectoryOptimizer[] trajectoryOptimizers,
            final PenaltyFunction[][] penaltyFunctions, double maxCost,
            long runtimeDeadlineMs) {
        int nAgents = trajectoryOptimizers.length;

        for (int i = 0; i < nAgents; i++) {

            Verbose.printf("Hard constraints planning for agent %d. \n", i);

            Trajectory[] otherTrajectories = otherTrajectories(trajectories, i);
            PenaltyFunction[] penaltyFunctionsOfOtherTrajectories = constraintsOf(
                    penaltyFunctions, i);
            penaltyFunctionsOfOtherTrajectories = applyWeight(
                    penaltyFunctionsOfOtherTrajectories,
                    Double.POSITIVE_INFINITY);

            EvaluatedTrajectory bestResponseTraj = trajectoryOptimizers[i]
                    .getOptimalTrajectoryConstrained(
                            penaltyFunctionsOfOtherTrajectories,
                            otherTrajectories, trajectories[i], maxCost,
                            runtimeDeadlineMs);
            
            if (bestResponseTraj != null) {
                trajectories[i] = bestResponseTraj;
                currentTrajectoriesForVis = trajectories;
                Verbose.printf("Found final trajectory for agent %d. Cost: %.4f\n", i, trajectories[i].getCost());
            } else {
            	Verbose.printf("FAILED to find final trajectory for agent %d. \n", i, trajectories[i].getCost());
            }
        }

        return trajectories;
    }

    private static EvaluatedTrajectory[] initUnconstrainedTrajectories(
            TrajectoryOptimizer[] trajectoryOptimizers, double maxCost,
            long runtimeDeadlineMs) {

        EvaluatedTrajectory[] trajectories = new EvaluatedTrajectory[trajectoryOptimizers.length];
        currentTrajectoriesForVis = trajectories;

        for (int i = 0; i < trajectoryOptimizers.length; i++) {
            Verbose.printf("Unconstrained planning for agent %d. \n", i);
            trajectories[i] = trajectoryOptimizers[i]
                    .getOptimalTrajectoryUnconstrained(maxCost,
                            runtimeDeadlineMs);
            
            Verbose.printf("Found initial trajectory for agent %d. Cost: %.4f\n", i, trajectories[i].getCost());
            
            if (trajectories[i] == null) {
                return null;
            }
        }

        return trajectories;
    }

    private static void assertSymmetric(PairwiseConstraint[][] constraints) {
        for (int i = 0; i < constraints.length; i++) {
            for (int j = 0; j < constraints[i].length; j++) {
                if (i != j) {
                    assert constraints[i][j] == constraints[j][i];
                }
            }
        }
    }

    private static double calculateSoftConstraintsWeight(int i,
            int maxIterations) {
        return TAN_COEF * Math.tan(Math.PI / 2 * (i+1) / (maxIterations + 1));
    }

    private static Trajectory[] higherPriorityTrajectories(Trajectory[] trajs,
            int id) {
        return Arrays.copyOfRange(trajs, 0, id);
    }

    private static PenaltyFunction[] constraintsWithHigherPriorityAgents(
            PenaltyFunction[][] penaltyFunctions, int id) {
        return Arrays.copyOfRange(penaltyFunctions[id], 0, id);
    }

    private static Trajectory[] otherTrajectories(Trajectory[] trajs,
            int excludeId) {
        Trajectory[] otherTrajs = new Trajectory[trajs.length - 1];
        int j = 0;
        for (int i = 0; i < trajs.length; i++) {
            if (i != excludeId)
                otherTrajs[j++] = trajs[i];
        }
        return otherTrajs;
    }

    private static PenaltyFunction[] constraintsOf(
            PenaltyFunction[][] penaltyFunctions, int excludeId) {
        PenaltyFunction[] otherConstraints = new PenaltyFunction[penaltyFunctions[excludeId].length - 1];
        int j = 0;
        for (int i = 0; i < penaltyFunctions[excludeId].length; i++) {
            if (i != excludeId)
                otherConstraints[j++] = penaltyFunctions[excludeId][i];
        }
        return otherConstraints;
    }
    
    private static int[] shuffleArray(int[] array, Random rnd)
    {
        int index, temp;
        for (int i = array.length - 1; i > 0; i--) {
            index = rnd.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
        
        return array;
    }



}
