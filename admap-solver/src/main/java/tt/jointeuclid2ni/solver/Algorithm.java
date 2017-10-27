package tt.jointeuclid2ni.solver;

import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointtraj.solver.SearchResult;

public interface Algorithm {
    public SearchResult solve(final EarliestArrivalProblem problem, Parameters parameters);
}
