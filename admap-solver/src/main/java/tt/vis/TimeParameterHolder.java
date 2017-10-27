package tt.vis;

import tt.euclidtime3i.vis.TimeParameter;

public class TimeParameterHolder {

    static public TimeParameter time;

    public static TimeParameter getInstance(int step) {
        if (time == null || time.getTimeStep() != step)
            time = new TimeParameter(step);
        return time;
    }

}
