package tt.jointtrajineuclidtime3i;

import java.util.Arrays;

import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.Point;

public class State {
    EvaluatedTrajectory[] trajs;

    public State(EvaluatedTrajectory[] trajs) {
        super();
        this.trajs = trajs;
    }

    public EvaluatedTrajectory get(int i) {
        return trajs[i];
    }

    public boolean trajectoriesEndInTheSameTime() {
        int time = Integer.MIN_VALUE;
        for (int i = 0; i < trajs.length; i++) {
            if (time == Integer.MIN_VALUE) {
                time = trajs[i].getMaxTime();
            } else if (time != trajs[i].getMaxTime()){
                return false;
            }
        }
        return true;
    }

    public int nAgents() {
        return trajs.length;
    }

    public Point getEndPoint(int i) {
        Point pos = trajs[i].get(trajs[i].getMaxTime());
        return pos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(trajs);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        State other = (State) obj;
        if (!Arrays.equals(trajs, other.trajs))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Arrays.toString(trajs);
    }



}
