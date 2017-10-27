package tt.jointtraj.homotopyiterative;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import tt.util.Verbose;

import com.google.common.collect.Sets;

public class StochasticStrategy implements PlannerStrategy {

    private int kStep;
    private int runtime;
    private Random random;

    private HashMap<HomotopyCombinationNode, Integer> K;
    double threshold = Double.POSITIVE_INFINITY;
    /**
     * if set to true, the strategy will not branch. Needed if the environment contains no obstacles,
     * since then there is only one homotopy class for each agent and the branch action would try
     * to find the next homotopy class, which does not exist *
     */
    private boolean branchAllowed;

    public StochasticStrategy(int kStep, int runtime, int seed, boolean branchAllowed) {
        this.kStep = kStep;
        this.runtime = runtime;
        this.random = new Random(seed);
        this.K = new HashMap<HomotopyCombinationNode, Integer>();
        this.branchAllowed = branchAllowed;
    }

    @Override
    public int maxRuntime() {
        return runtime;
    }

    @Override
    public boolean terminatingCondition(HomotopyCombinationTree tree) {
        return true;
    }

    @Override
    public int nextK(HomotopyCombinationNode node) {
        Integer k = K.get(node);

        if (k == null) {
            k = 1;
        } else {
            k += kStep;
        }

        K.put(node, k);
        return k;
    }

    @Override
    public ActionSelection chooseAction(HomotopyCombinationTree tree) {
        Set<HomotopyCombinationNode> openedNodes = tree.getOpenedNodes();
        Set<HomotopyCombinationNode> closedNodes = tree.getClosedNodes();
        Set<HomotopyCombinationNode> refined = K.keySet();

        maintainThresholdInvariant(tree);

        Set<HomotopyCombinationNode> nodesToBranch = getNodesWithLowerBoundUnder(Sets.intersection(openedNodes, refined), threshold);
        Set<HomotopyCombinationNode> allNodes = getNodesWithLowerBoundUnder(Sets.union(closedNodes, openedNodes), threshold);

        assert !allNodes.isEmpty(); // we should always have at least the currently best homotopy combination node

        if (!nodesToBranch.isEmpty() && branchAllowed && random.nextBoolean()) {
            return new ActionSelection(randomElement(nodesToBranch), IterativePlannerAction.BRANCH);
        } else {
            return new ActionSelection(randomElement(allNodes), IterativePlannerAction.REFINE);
        }
    }

    private void maintainThresholdInvariant(HomotopyCombinationTree tree) {
        double globalLB = tree.getGlobalLowerBound();
        double globalUB = tree.getGlobalUpperBound();

        //  the threshold has not been set yet
        if (threshold == Double.POSITIVE_INFINITY) {
            threshold = globalLB * 1.3;
            Verbose.printf("Threshold initialized to %.2f", threshold);
        }

        if (threshold <= globalLB) {
            threshold = globalLB * 1.3;
            Verbose.printf("Threshold increased to %.2f", threshold);
        }

        if (threshold > globalUB) {
            threshold = globalUB;
            Verbose.printf("Threshold adjusted to fit UB %.2f \n", threshold);
        }
    }

    Set<HomotopyCombinationNode> getNodesWithLowerBoundUnder(Set<HomotopyCombinationNode> set, double costThreshold) {
        Set<HomotopyCombinationNode> usefulNodes = new HashSet<HomotopyCombinationNode>();
        for (HomotopyCombinationNode node : set) {
            if (node.getLowerBound() <= costThreshold) {
                usefulNodes.add(node);
            }
        }
        return usefulNodes;
    }

    private HomotopyCombinationNode randomElement(Set<HomotopyCombinationNode> set) {
        int rnd = random.nextInt(set.size());
        return (HomotopyCombinationNode) set.toArray()[rnd];
    }

    @Override
    public double costThreshold() {
        return threshold;
        //return Double.POSITIVE_INFINITY;
    }
}
