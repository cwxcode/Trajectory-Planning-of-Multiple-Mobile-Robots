package tt.jointtraj.homotopyiterative;

import java.util.Arrays;

import org.jscience.mathematics.number.Complex;

import tt.planner.homotopy.hvalue.HValueAllowAllPolicy;
import tt.planner.homotopy.hvalue.HValueAllowPolicy;
import tt.planner.homotopy.hvalue.HValueForbidPolicy;
import tt.planner.homotopy.hvalue.HValuePolicy;
import tt.util.Verbose;

public class HomotopyCombinationNode {

    private HomotopyCombinationTree tree;
    private HomotopyCombinationNode parent;
    private Complex[] hValues;
    private int[] id;

    private double lowerBound;
    private double solutionCost;

    public static HomotopyCombinationNode createRoot(HomotopyCombinationTree tree, Complex[] hValues, double lowerBound) {
        HomotopyCombinationNode rootNode = new HomotopyCombinationNode(tree, hValues, lowerBound);
        Verbose.printf("-- Created root %s \n", rootNode);
        return rootNode;

    }

    private HomotopyCombinationNode(HomotopyCombinationTree tree, Complex[] hValues, double lowerBound) {
        this.tree = tree;

        this.id = new int[hValues.length];
        Arrays.fill(id, 1);

        this.hValues = hValues;
        this.lowerBound = lowerBound;
        this.solutionCost = Double.POSITIVE_INFINITY;
    }

    private HomotopyCombinationNode(HomotopyCombinationNode parent, int i, Complex[] hValues, double lowerBound) {
        this.tree = parent.tree;
        this.parent = parent;

        this.id = Arrays.copyOf(parent.id, parent.id.length);
        this.id[i]++;

        this.hValues = hValues;
        this.lowerBound = lowerBound;
        this.solutionCost = Double.POSITIVE_INFINITY;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getCurrentSolutionCost() {
        return solutionCost;
    }

    public HValuePolicy allowIthHValue(int i) {
        Complex hValue = hValues[i];

        if (hValue == null) //root
            return new HValueAllowAllPolicy();
        else
            return new HValueAllowPolicy(hValue);
    }

    public HValuePolicy forbidHistoryOfIthValues(int i) {
        HValueForbidPolicy policy = new HValueForbidPolicy();

        HomotopyCombinationNode node = this;
        while (node != null) {
            policy.forbid(node.getHValue(i));
            node = node.getParent();
        }

        return policy;
    }

    public HomotopyCombinationNode getParent() {
        return parent;
    }

    public Complex getHValue(int i) {
        return hValues[i];
    }

    public HomotopyCombinationNode createChild(int i, Complex hValue, double lowerBound) {
        Complex[] hValues = Arrays.copyOf(this.hValues, this.hValues.length);
        hValues[i] = hValue;

        return new HomotopyCombinationNode(this, i, hValues, lowerBound);
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    @Override
    public String toString() {
        return String.format("[Node{%s} lb:%.2f c:%.2f]", Arrays.toString(id), lowerBound, solutionCost);
    }

    public void setSolutionCost(double solutionCost) {
        this.solutionCost = solutionCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HomotopyCombinationNode that = (HomotopyCombinationNode) o;

        if (!Arrays.equals(id, that.id)) return false;
        if (!tree.equals(that.tree)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tree.hashCode();
        result = 31 * result + Arrays.hashCode(id);
        return result;
    }
}
