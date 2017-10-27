package tt.jointtraj.homotopyiterative;

import tt.euclid2i.EvaluatedTrajectory;

@SuppressWarnings("unchecked")


public interface NewSolutionListener {
    void notifyNewSolution(EvaluatedTrajectory[] trajs);
}