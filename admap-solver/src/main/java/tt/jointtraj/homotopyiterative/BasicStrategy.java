package tt.jointtraj.homotopyiterative;

import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.Sets;


/**
 * Current implementation force the planner to search through the planner tree in such manner that earch node is
 * refined only once. Each node in open set is branched after being refine until the search space explored or
 * time runs out.
 */
public class BasicStrategy implements PlannerStrategy {

    private int kStep;
    private int runtime;

    private HashMap<HomotopyCombinationNode, Integer> K;

    public BasicStrategy(int kStep, int runtime) {
        this.kStep = kStep;
        this.runtime = runtime;
        this.K = new HashMap<HomotopyCombinationNode, Integer>();
    }

    @Override
    public int maxRuntime() {
        return runtime;
    }

    @Override
    public int nextK(HomotopyCombinationNode node) {
        /* A bit to complicated, because of the chooseAction implementation is this method called
        * on each node only once! */
        Integer k = K.get(node);

        if (k == null) {
            k = kStep;
        } else {
            k += kStep;
        }

        K.put(node, k);
        return k;
    }

    @Override
    public ActionSelection chooseAction(HomotopyCombinationTree tree) {
        Set<HomotopyCombinationNode> refinedNodes = K.keySet();
        Set<HomotopyCombinationNode> openedNodes = tree.getOpenedNodes();

        Set<HomotopyCombinationNode> nodesToRefine = Sets.difference(openedNodes, refinedNodes);

        if (!nodesToRefine.isEmpty()) {
            for (HomotopyCombinationNode node : nodesToRefine) {
                return new ActionSelection(node, IterativePlannerAction.REFINE);
            }
        } else {
            for (HomotopyCombinationNode node : openedNodes) {
                return new ActionSelection(node, IterativePlannerAction.BRANCH);
            }
        }

        throw new RuntimeException("All nodes refined. No node to branch.");
    }

    @Override
    public boolean terminatingCondition(HomotopyCombinationTree tree) {
        return !tree.getOpenedNodes().isEmpty();
    }

    @Override
    public double costThreshold() {
        return Double.MAX_VALUE;
    }
}
