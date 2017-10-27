package tt.jointeuclid2ni.probleminstance;

import tt.euclid2i.Point;

public class AgentMissionImpl implements AgentMission {

    private Point start;
    private Point target;
    private int bodyRadius;
    private float maxSpeed;

    public AgentMissionImpl(Point start, Point target, int bodyRadius, float maxSpeed) {
        this.start = start;
        this.target = target;
        this.bodyRadius = bodyRadius;
        this.maxSpeed = maxSpeed;
    }

    public AgentMissionImpl(EarliestArrivalProblem problem, int agent) {
        this.start = problem.getStart(agent);
        this.target = problem.getTarget(agent);
        this.bodyRadius = problem.getBodyRadius(agent);
        this.maxSpeed = problem.getMaxSpeed(agent);
    }

    public Point getStart() {
        return start;
    }

    public Point getTarget() {
        return target;
    }

    public int getBodyRadius() {
        return bodyRadius;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bodyRadius;
		result = prime * result + Float.floatToIntBits(maxSpeed);
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		AgentMissionImpl other = (AgentMissionImpl) obj;
		if (bodyRadius != other.bodyRadius)
			return false;
		if (Float.floatToIntBits(maxSpeed) != Float
				.floatToIntBits(other.maxSpeed))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}


}
