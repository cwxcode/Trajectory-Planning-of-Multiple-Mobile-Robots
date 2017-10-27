package tt.continous;

/**
 * This class represents a generalization of the concept of trajectory as understood in kinematics.
 * Here, we consider it as a mapping from a time interval (tmin, tmax) to an arbitrary space S,
 * representing a state of some object o at some time t in (tmin, tmax).
 *
 * The state of the object o in time t can be obtained using {@link #get(double)} and must be defined
 * for all time points s.t. {@link #getMinTime()} <= time <= {@link #getMaxTime()}.
 * For all other times, {@link #get(double)} returns null.
 */
public interface Trajectory<S> {
    public double getMinTime();
    public double getMaxTime();
    public S get(double t);
}
