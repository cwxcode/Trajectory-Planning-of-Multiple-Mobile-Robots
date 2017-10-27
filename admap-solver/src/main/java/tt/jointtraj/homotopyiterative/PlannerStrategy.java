package tt.jointtraj.homotopyiterative;

public interface PlannerStrategy {

    /** Returns maximal runtime of the algorithm */
    public int maxRuntime();

    /** Additional condition for terminating the angorithm */
    public boolean terminatingCondition(HomotopyCombinationTree tree);

    /** Returns number of refine steps for given node */
    public int nextK(HomotopyCombinationNode node);

    /** Selects node and action to execute on that node */
    public ActionSelection chooseAction(HomotopyCombinationTree tree);

    public double costThreshold();
}
