package tt.jointeuclid2ni.probleminstance.generator;

import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;

public interface ProblemCreatedListener {
    public void handleNewProblem(EarliestArrivalProblem problem);
}
