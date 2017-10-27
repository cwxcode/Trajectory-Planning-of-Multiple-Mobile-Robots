package tt.jointtraj.homotopyiterative;

public class ActionSelection {

    private HomotopyCombinationNode node;
    private IterativePlannerAction action;

    public ActionSelection(HomotopyCombinationNode node, IterativePlannerAction action) {
        this.node = node;
        this.action = action;
    }

    public HomotopyCombinationNode getNode() {
        return node;
    }

    public IterativePlannerAction getAction() {
        return action;
    }
}
