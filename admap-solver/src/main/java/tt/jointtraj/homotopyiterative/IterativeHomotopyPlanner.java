package tt.jointtraj.homotopyiterative;

import org.jgrapht.DirectedGraph;
import org.jscience.mathematics.number.Complex;
import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;
import tt.euclidtime3i.discretization.softconstraints.LinearSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.PairwiseConstraint;
import tt.euclidtime3i.discretization.softconstraints.SeparationConstraint;
import tt.jointeuclid2ni.probleminstance.AgentMission;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblems;
import tt.jointtraj.separableflow.PenaltyOptimizer;
import tt.planner.homotopy.HomotopyUtils;
import tt.planner.homotopy.hvalue.HValueAllowAllPolicy;
import tt.planner.homotopy.hvalue.HValueAnalyticIntegrator;
import tt.planner.homotopy.hvalue.HValueIntegrator;
import tt.planner.homotopy.hvalue.HValuePolicy;
import tt.util.Verbose;

import java.util.List;
import java.util.Random;

public class IterativeHomotopyPlanner {

	private TrajectoryOptimizerWithHomotopyConstraints[] trajectoryOptimizers;
	private PairwiseConstraint[][] constraints;
    private PlannerStrategy strategy;

    private HomotopyCombinationTree plannerTree;

    private EvaluatedTrajectory[] bestSolution;
    private EvaluatedTrajectory[] currentSolution;
    private double bestSolutionCost; // Upper bound
    private int nAgents;
    private double threshold;

    private NewSolutionListener solutionListener;
	private long runtimeDeadline;


//    public static EvaluatedTrajectory[] solveProblem(PlannerStrategy strategy,
//                                                     EarliestArrivalProblem problem,
//                                                     DirectedGraph<Point, Line> spatialGraphs[],
//                                                     int maxTime,
//                                                     int waitTimeDuration,
//                                                     NewSolutionListener solutionListener) {
//        Random random = new Random();
//        AgentMission[] missions = EarliestArrivalProblems
//                .agentMissions(problem);
//        PairwiseConstraint constraint = new SeparationConstraint(
//                new LinearSeparationPenaltyFunction(MAX_PENALTY), 5);
//
//        List<Complex> freeSpaceSamples = HomotopyUtils.sampleFreeSpace(
//                problem.getObstacles(), problem.getBounds(), random);
//        List<Complex> obstacleSamples = HomotopyUtils.sampleObstacles(
//                problem.getObstacles(), random);
//        HValueIntegrator integrator = new HValueAnalyticIntegrator(
//                freeSpaceSamples, obstacleSamples);
//        // HValueIntegrator integrator = new
//        // HValueNumericIntegrator(freeSpaceSamples, obstacleSamples, 5, 4);
//
//        IterativeHomotopyPlanner solver = new IterativeHomotopyPlanner(
//                strategy, spatialGraphs, missions, constraint, integrator,
//                maxTime, waitTimeDuration, solutionListener);
//
//        solver.solve();
//        return solver.getBestSolution();
//    }

	public IterativeHomotopyPlanner(TrajectoryOptimizerWithHomotopyConstraints[] trajectoryOptimizers,
                                    PairwiseConstraint[][] constraints,
                                    PlannerStrategy strategy,
                                    NewSolutionListener solutionListener) {
        this.nAgents = trajectoryOptimizers.length;
        this.strategy = strategy;
        this.trajectoryOptimizers = trajectoryOptimizers;
        this.constraints = constraints;
        this.bestSolutionCost = Double.POSITIVE_INFINITY;

        this.plannerTree = new HomotopyCombinationTree();
        this.solutionListener = solutionListener;
    }

    public EvaluatedTrajectory[] getBestSolution() {
        return bestSolution;
    }

    // used only for visualization purposes
    public EvaluatedTrajectory[] getCurrentSolution() {
        return currentSolution;
    }

    public void solve() {
        Verbose.println(String.format("\n>>>Creating root: threshold=%.2f", strategy.costThreshold()));

        try {
            this.plannerTree.add(createRoot());
        } catch (TrajectoryNotFoundException e) {
            Verbose.println("\n>>>NO INITIAL SOLUTION FOUND");
            return;
        }

        this.runtimeDeadline = System.currentTimeMillis() + strategy.maxRuntime();

        while (strategy.terminatingCondition(plannerTree) && checkRuntime(runtimeDeadline)) {

            Verbose.printf("*The tree has %d nodes. C: %d O: %d LB=%.2f UB=%.2f\n",
                    plannerTree.size(),
                    plannerTree.getClosedNodes().size(),
                    plannerTree.getOpenedNodes().size(),
                    plannerTree.getGlobalLowerBound(),
                    plannerTree.getGlobalUpperBound());

            ActionSelection actionSelection = strategy.chooseAction(plannerTree);
            HomotopyCombinationNode node = actionSelection.getNode();
            double costThreshold = strategy.costThreshold();
            switch (actionSelection.getAction()) {
                case BRANCH:
                    Verbose.println(String.format("\n>>>ACTION: Branch %s threshold=%.2f", node, costThreshold));
                    branch(node);
                    break;

                case REFINE:
                    int k = strategy.nextK(node);

                    Verbose.println(String.format("\n>>>ACTION: Refine %s k=%d threshold=%.2f", node, k, costThreshold));
                    refine(node, k, costThreshold);
                    break;
            }

            prune();
        }

        Verbose.println("\nTERMINATED");
    }

    private boolean checkRuntime(long deadline) {
        return System.currentTimeMillis() < deadline;
    }

    private void branch(HomotopyCombinationNode node) {
        for (int i = 0; i < nAgents; i++) {
            HomotopyCombinationNode child;

            try {
                child = createChild(node, i, strategy.costThreshold());
            } catch (TrajectoryNotFoundException e) {
                Verbose.printf("-- node %n skipped because no initial trajectory was found %s%n", node);
                continue;
            }

            Verbose.println(String.format("-- new child created: %s", child));
            double lowerBound = child.getLowerBound();

            if (lowerBound < strategy.costThreshold()) {
                Verbose.printf("-- --  added new node to open set: %s", child);
                plannerTree.add(child);
            } else {
                Verbose.printf("-- --  didnt add the candidate node: %s", child);
            }
        }

        plannerTree.close(node);
    }

    private void refine(HomotopyCombinationNode node, int k, double costThreshold) {
        HValuePolicy[] policies = getAllowedPolicies(node);

        LocalHClassSolution solution;
        solution = solveWithHomotopyConstraints(trajectoryOptimizers, constraints, policies, k, costThreshold, runtimeDeadline);

        if (solution == null) {
            Verbose.printf("-- --  no trajectory found with cost bound %.2f, the best known solution is still: %.2f\n", strategy.costThreshold(), bestSolutionCost);
            return;
        }

        node.setSolutionCost(solution.cost);

        Verbose.println(String.format("-- cost %.0f", solution.cost));

        setAsCurrentSolution(solution);
        setAsBestSolutionIfBetter(solution);
    }

    private void prune() {
        plannerTree.prune(strategy.costThreshold());
    }

    private HomotopyCombinationNode createChild(HomotopyCombinationNode parent, int i, double costThreshold) throws TrajectoryNotFoundException {
        HValuePolicy[] policies = getInitialPolicies(parent, i);

        LocalHClassSolution solution = solveUnconstrained(trajectoryOptimizers, policies, costThreshold, runtimeDeadline);
        setAsCurrentSolution(solution);

        return parent.createChild(i, solution.hValues[i], solution.cost);
    }

    private HomotopyCombinationNode createRoot() throws TrajectoryNotFoundException {
        HValuePolicy[] policies = new HValuePolicy[nAgents];
        for (int j = 0; j < nAgents; j++) {
            policies[j] = new HValueAllowAllPolicy();
        }

        LocalHClassSolution solution = solveUnconstrained(trajectoryOptimizers, policies, strategy.costThreshold(), runtimeDeadline);

        // the solution returned is unconstrained, thus it is merely a lower-bound
        double lb = solution.cost;

        return HomotopyCombinationNode.createRoot(plannerTree, solution.hValues, lb);
    }

    private void setAsCurrentSolution(LocalHClassSolution solution) {
        currentSolution = solution.trajectories;
    }

    private void setAsBestSolutionIfBetter(LocalHClassSolution solution) {
        if (solution.cost < bestSolutionCost) {
            bestSolutionCost = solution.cost;
            bestSolution = solution.trajectories;
            solutionListener.notifyNewSolution(bestSolution);
            Verbose.println("-- best solution updated");
        } else {
            Verbose.println("-- not a best solution");
        }
    }

    private HValuePolicy[] getInitialPolicies(HomotopyCombinationNode parent, int i) {
        HValuePolicy[] policies = getAllowedPolicies(parent);
        policies[i] = parent.forbidHistoryOfIthValues(i);
        return policies;
    }


    private HValuePolicy[] getAllowedPolicies(HomotopyCombinationNode parent) {
        HValuePolicy[] policies = new HValuePolicy[nAgents];

        for (int j = 0; j < nAgents; j++)
            policies[j] = parent.allowIthHValue(j);

        return policies;
    }

    public static LocalHClassSolution solveWithHomotopyConstraints(
    		final TrajectoryOptimizerWithHomotopyConstraints[] trajectoryOptimizers,
            final PairwiseConstraint[][] constraints, HValuePolicy[] policies,
            int k, double maxCost, long runtimeDeadlineMs)
            {

    	for (int i=0; i<trajectoryOptimizers.length; i++) {
    		trajectoryOptimizers[i].setHomotopyPolicy(policies[i]);
    	}

    	EvaluatedTrajectory[] trajs = null; //SeparableFlowOptimizer.solve(trajectoryOptimizers, constraints, k, maxCost, runtimeDeadlineMs, false);

    	// Extract H-value...

    	Complex[] hValues = new Complex[trajectoryOptimizers.length];
    	for (int i=0; i<trajectoryOptimizers.length; i++) {
    		hValues[i] = trajectoryOptimizers[i].getHValueOfLastPath();
    	}

    	double cost = 0;
    	for (int i = 0; i < trajs.length; i++) {
			cost += trajs[i].getCost();
		}

		return new LocalHClassSolution(trajs, hValues, cost);
    }

    public static LocalHClassSolution solveUnconstrained(
    		final TrajectoryOptimizerWithHomotopyConstraints[] trajectoryOptimizers,
    		HValuePolicy[] policies,
            double maxCost, long runtimeDeadlineMs)
            {

    	// Dummy policy that allow all homotopy classes
    	for (int i=0; i<trajectoryOptimizers.length; i++) {
    		trajectoryOptimizers[i].setHomotopyPolicy(new HValuePolicy() {

				@Override
				public boolean isAllowed(Complex hValue, double precision) {
					return true;
				}
			});
    	}

    	// Dummy constraints
    	PairwiseConstraint[][] constraints = new PairwiseConstraint[trajectoryOptimizers.length][trajectoryOptimizers.length];
    	for (int i=0; i<trajectoryOptimizers.length; i++) {
    		for (int j=0; j<trajectoryOptimizers.length; j++) {
    			constraints[i][j] = new PairwiseConstraint() {

					@Override
					public double getPenalty(Trajectory t1, Trajectory t2) {
						return 0;
					}
				};
    		}
    	}


    	// TODO fix this, does not stick to the new API
    	EvaluatedTrajectory[] trajs = null; //SeparableFlowOptimizer.solve(trajectoryOptimizers, constraints, 1, maxCost, runtimeDeadlineMs, false);

    	// Extract H-value...

    	Complex[] hValues = new Complex[trajectoryOptimizers.length];
    	for (int i=0; i<trajectoryOptimizers.length; i++) {
    		hValues[i] = trajectoryOptimizers[i].getHValueOfLastPath();
    	}

    	double cost = 0;
    	for (int i = 0; i < trajs.length; i++) {
			cost += trajs[i].getCost();
		}

		return new LocalHClassSolution(trajs, hValues, cost);
    }
}
