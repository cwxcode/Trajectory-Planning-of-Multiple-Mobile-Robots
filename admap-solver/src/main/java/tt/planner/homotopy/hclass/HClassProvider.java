package tt.planner.homotopy.hclass;

import org.jscience.mathematics.number.Complex;


/**
 * This class is used to assign HClass labels to HNode<V> nodes. Nodes with same V node and HClass label are
 * considered to be equal (Important during search in Sets and Maps!).
 * <p/>
 * This clustering must be consistent in time! A case when node with same hValue is clustered
 * into different classes during the time is not acceptable and might cause nonstability of running planning algorithm.
 */
public interface HClassProvider<V> {

    public HClass assignHClass(V node, Complex hValue, double precision);
}
