package tt.jointtraj.homotopyiterative;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import tt.util.Verbose;

import com.google.common.collect.Sets;

public class HomotopyCombinationTree {

    private Set<HomotopyCombinationNode> closedNodes;
    private Set<HomotopyCombinationNode> openedNodes;

    public HomotopyCombinationTree() {
        closedNodes = new HashSet<HomotopyCombinationNode>();
        openedNodes = new HashSet<HomotopyCombinationNode>();
    }

    public Set<HomotopyCombinationNode> getClosedNodes() {
        return closedNodes;
    }

    public Set<HomotopyCombinationNode> getOpenedNodes() {
        return openedNodes;
    }

    public boolean hasOpenNodes() {
        return !openedNodes.isEmpty();
    }

    public void add(HomotopyCombinationNode node) {
        openedNodes.add(node);
    }

    public void close(HomotopyCombinationNode node) {
        if (!openedNodes.contains(node))
            throw new RuntimeException("No such node in open set");

        openedNodes.remove(node);
        closedNodes.add(node);
    }

    public void prune(double bestSolutionCost) {
        pruneSet(closedNodes, bestSolutionCost);
        pruneSet(openedNodes, bestSolutionCost);
    }

    private void pruneSet(Set<HomotopyCombinationNode> nodes, double bestSolutionCost) {
        Iterator<HomotopyCombinationNode> treeIterator = nodes.iterator();

        while (treeIterator.hasNext()) {
            HomotopyCombinationNode node = treeIterator.next();
            if (node.getLowerBound() > bestSolutionCost) {
                treeIterator.remove();
                Verbose.print("The node " + node + " has been pruned out");
            }
        }
    }

    public double getGlobalLowerBound() {
        Set<HomotopyCombinationNode> allNodes = Sets.union(closedNodes, openedNodes);
        assert !allNodes.isEmpty();
        double lowerBound = 0;
        for (HomotopyCombinationNode node : allNodes) {
            if (lowerBound == 0 || node.getLowerBound() < lowerBound) {
                lowerBound = node.getLowerBound();
            }
        }

        return lowerBound;
    }

    public double getGlobalUpperBound() {
        Set<HomotopyCombinationNode> allNodes = Sets.union(closedNodes, openedNodes);
        assert !allNodes.isEmpty();
        double upperBound = Double.POSITIVE_INFINITY;
        for (HomotopyCombinationNode node : allNodes) {
            if (node.getCurrentSolutionCost() < upperBound) {
                upperBound = node.getCurrentSolutionCost();
            }
        }

        return upperBound;
    }

    public int size() {
        return closedNodes.size() + openedNodes.size();
    }
}
