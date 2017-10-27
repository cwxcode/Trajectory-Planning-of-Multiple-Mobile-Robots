package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;
import tt.planner.homotopy.hclass.HClass;

/**
 * This class represents simple node wrapper which is used in HomotopyGraphWrapper. Using the HClass object it can be
 * distinguished whether two HNodes containing similar hValue belongs into the same homotopy class or not.
 */

public class HNode<V> {

    private final V node;
    private final Complex hValue;
    private final HClass hClass;

    //TODO fix passing precision in the constructor all the time...

    HNode(V node, Complex hValue, HClass hClass) {
        this.node = node;
        this.hValue = hValue;
        this.hClass = hClass;
    }

    public Complex getHValue() {
        return hValue;
    }

    public V getNode() {
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HNode hNode = (HNode) o;

        if (!hClass.equals(hNode.hClass)) return false;
        if (!node.equals(hNode.node)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = node.hashCode();
        result = 31 * result + hClass.hashCode();
        return result;
    }
}
