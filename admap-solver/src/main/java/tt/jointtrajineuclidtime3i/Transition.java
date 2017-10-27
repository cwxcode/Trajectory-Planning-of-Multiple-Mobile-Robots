package tt.jointtrajineuclidtime3i;

import java.util.Arrays;

import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.trajectory.Trajectories;

public class Transition {
    EvaluatedTrajectory[] deltaTrajs;
    State start;
    State end;
    double softConstraintViolationPenalty = 0;

    public Transition(EvaluatedTrajectory[] deltaTrajs, State start, double softConstraintViolationPenalty) {
        super();
        this.deltaTrajs = deltaTrajs;
        this.start = start;
        this.softConstraintViolationPenalty = softConstraintViolationPenalty;

        // generate end state

        EvaluatedTrajectory[] endState = new EvaluatedTrajectory[deltaTrajs.length];
        for (int i = 0; i < deltaTrajs.length; i++) {
            if (deltaTrajs[i] != null) {
                endState[i] = Trajectories.concatenate(start.get(i), deltaTrajs[i]);
            } else {
                endState[i] = start.get(i);
            }
        }
        this.end =  new State(endState);
    }

    public State getStart() {
        return start;
    }

    public State getEnd() {
        return end;
    }

    public double getCost() {
        double cost = 0.0;
        for (int i = 0; i < deltaTrajs.length; i++) {
            if (deltaTrajs[i] != null) {
                cost += deltaTrajs[i].getCost();
            }
        }
        return cost + softConstraintViolationPenalty;
    }

    @Override
    public String toString() {
        return "[Δτ=" + Arrays.toString(deltaTrajs)
                + ", v="
                + softConstraintViolationPenalty + "]";
    }


}
